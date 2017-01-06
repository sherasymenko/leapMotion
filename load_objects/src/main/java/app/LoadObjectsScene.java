package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import lm.LeapApp;
import lm.LeapApp.Mode;
import lm.event.PointEvent;
import lm.event.interfaces.PointMotionListener;
import lm.fx.CollisionDetector;
import lm.fx.HandFX3D;

@SuppressWarnings("restriction")
public class LoadObjectsScene extends Application implements PointMotionListener {
	private final double sceneWidth = 800;
	private final double sceneHeight = 600;
	private final XformCamera cameraXform = new XformCamera();
	private static final double CAMERA_INITIAL_DISTANCE = -400;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	private CollisionDetector collisionDetector = new CollisionDetector();
	private PerspectiveCamera camera;
	private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
	private Group group = new Group();
	private Scene scene;
	private Map<Integer, HandFX3D> hands = new HashMap<Integer, HandFX3D>();
	private List<MeshView> meshViewTable = new ArrayList<MeshView>();
	private double mousePosX, mousePosY, mouseOldX, mouseOldY, mouseDeltaX, mouseDeltaY;

	@Override
	public void start(Stage primaryStage) throws Exception {
		LeapApp.init(true);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		addObjectsToGroup();
		group.setDepthTest(DepthTest.ENABLE);
		group.setTranslateX(0);
		group.setTranslateY(0);
		group.setTranslateZ(0);
		// tworzenie sceny wraz z obiektami
		scene = new Scene(group, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
		scene.setFill(Color.BLANCHEDALMOND);
		cameraSetting();
		primaryStage.setScene(scene);
		primaryStage.show();
		mouseSetting();
		// synchronizacja z LeapMotion
		synchronizeWithLeapMotion();
		LeapApp.getMotionRegistry().addPointMotionListener(this);
	}

	// metoda umożliwiająca działanie animacji, czyli płynna zmiana pozycji rąk
	// w czasie

	private void synchronizeWithLeapMotion() {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0 / 60.0), ea -> LeapApp.update()));
		timeline.play();
	}

	// metoda identyfikuje ręce oraz ciągle aktualizuje ich położenie,
	// przy czym sprawdza czy zaszła kolizja między rękami a obiektami
	@Override
	public void pointMoved(PointEvent event) {
		int handId = event.getSource().id();
		HandFX3D hand = hands.get(handId);
		if (event.leftViewPort()) {
			hands.remove(handId);
			group.getChildren().remove(hand);
		} else if (hand == null) {
			hand = new HandFX3D(handId);
			hands.put(handId, hand);
			hand.setTranslateZ(-50);
			group.getChildren().add(hand);
		}
		if (hand != null) {
			hand.update(LeapApp.getController().frame().hand(handId));
			collisionDetector.checkCollisionWithObjects(hand, meshViewTable, scene, group);
		}
	}

	@Override
	public void pointDragged(PointEvent event) {
	}

	private void cameraSetting() {
		// ustawienia kamery, kąta widzenia, itd.
		camera = new PerspectiveCamera(true);
		camera.setVerticalFieldOfView(false);
		camera.setNearClip(0.1);
		camera.setFarClip(10000.0);
		camera.getTransforms().addAll(new Translate(0, 0, -1000));
		camera.setTranslateX(0);
		camera.setTranslateZ(0);
		camera.setTranslateY(-300);
		camera.setFieldOfView(40);
		group.getChildren().add(cameraXform);
		cameraXform.getChildren().add(camera);
		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		scene.setCamera(camera);
	}

	private void mouseSetting() {
		scene.setOnMousePressed((MouseEvent me) -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseOldX = me.getSceneX();
			mouseOldY = me.getSceneY();
		});
		scene.setOnMouseDragged((MouseEvent me) -> {
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseDeltaX = (mousePosX - mouseOldX);
			mouseDeltaY = (mousePosY - mouseOldY);
			if (me.isPrimaryButtonDown()) {
				cameraXform.ry(mouseDeltaX * 180.0 / scene.getWidth());
			} else if (me.isSecondaryButtonDown()) {
				camera.setTranslateZ(camera.getTranslateZ() + mouseDeltaY);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void addObjectsToGroup() throws IOException {
		StlMeshImporter stlImporter = new StlMeshImporter();
		final File nativeDir = new File("resources/stl/");
		final File[] nativeFiles = nativeDir.listFiles();
		for (File fileName : nativeFiles) {
			try {
				System.out.println("toURI " + fileName.toURL());
				System.out.println("getName " + fileName.getName());
				stlImporter.read(fileName.toURL());
			} catch (ImportException e) {
				e.printStackTrace();
				return;
			}
			TriangleMesh mesh = stlImporter.getImport();
			MeshView meshView = new MeshView(mesh);
			meshView.getTransforms().addAll(rotateX, rotateY, rotateZ, new Translate(0, 0, 0));
			meshViewTable.add(meshView);
		}
		stlImporter.close();

		for (int i = 0; i < meshViewTable.size(); i++) {
			MeshView m = meshViewTable.get(i);
			m.setTranslateZ(-100);
			m.setTranslateY(-100);
			group.getChildren().add(m);
		}
	}
}