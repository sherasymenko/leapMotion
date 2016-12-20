package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.input.KeyEvent;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import java.awt.event.KeyListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
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
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.event.EventHandler;

@SuppressWarnings("restriction")
public class MainClass extends Application implements PointMotionListener {

	private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

	private final Group group = new Group();
	private PerspectiveCamera camera;
	private Scene scene;
	private Map<Integer, HandFX3D> hands = new HashMap<Integer, HandFX3D>();
	private final Rotate cameraRotateX = new Rotate(-30, Rotate.X_AXIS);
	private final Rotate cameraRotateY = new Rotate(0, Rotate.Y_AXIS);
	private final double sceneWidth = 800;
	private final double sceneHeight = 600;
	private Sphere sphere = new Sphere(30);
	private Box box = new Box(50, 50, 50);
	private Box floor = new Box(2000, 10, 2000);
	private Box leftWall = new Box(10, 2000, 2000);
	private Box rightWall = new Box(10, 2000, 2000);
	private Box middleWall = new Box(2000, 2000, 10);
	private CollisionDetector collisionDetector = new CollisionDetector();
	List<MeshView> meshViewTable = new ArrayList<MeshView>();

	public static void main(String[] args) {
		System.out.println("test1");
		LeapApp.init(true);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		// System.out.println("args" + args.length);
		launch(args);
	}

	private List<String> getResourceFiles(String path) throws IOException {
		List<String> filenames = new ArrayList<>();

		try (InputStream in = getResourceAsStream(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String resource;

			while ((resource = br.readLine()) != null) {
				filenames.add(resource);
			}
		}

		return filenames;
	}

	private InputStream getResourceAsStream(String resource) {
		final InputStream in = getContextClassLoader().getResourceAsStream(resource);

		return in == null ? getClass().getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@SuppressWarnings("restriction")
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("test2");
		StlMeshImporter stlImporter123 = new StlMeshImporter();
		System.out.println("test3------------------------------ " + getResourceFiles("/stl"));

		for (String fileName : getResourceFiles("/stl")) {
			try {
				stlImporter123.read(this.getClass().getResource("/stl/" + fileName));
			} catch (ImportException e) {
				e.printStackTrace();
				return;
			}
			TriangleMesh mesh = stlImporter123.getImport();

			MeshView meshView = new MeshView(mesh);
			meshView.getTransforms().addAll(rotateX, rotateY, rotateZ, new Translate(0, 0, 0));
			meshViewTable.add(meshView);
		}

		stlImporter123.close();
	
		/*
		 * box.getTransforms().addAll(rotateX, rotateY, rotateZ, new
		 * Translate(0, 0, 0)); floor.getTransforms().addAll(rotateX, rotateY,
		 * rotateZ, new Translate(0, 0, 0));
		 * leftWall.getTransforms().addAll(rotateX, rotateY, rotateZ, new
		 * Translate(0, 0, 0)); rightWall.getTransforms().addAll(rotateX,
		 * rotateY, rotateZ, new Translate(0, 0, 0));
		 * middleWall.getTransforms().addAll(rotateX, rotateY, rotateZ, new
		 * Translate(0, 0, 0));
		 * 
		 * sphere.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
		 * box.setMaterial(new PhongMaterial(Color.KHAKI));
		 * floor.setMaterial(new PhongMaterial(Color.AZURE));
		 * leftWall.setMaterial(new PhongMaterial(Color.AZURE));
		 * rightWall.setMaterial(new PhongMaterial(Color.AZURE));
		 * middleWall.setMaterial(new PhongMaterial(Color.AZURE));
		 * 
		 * floor.setTranslateY(-50); leftWall.setTranslateX(-200);
		 * rightWall.setTranslateX(200); middleWall.setTranslateZ(50);
		 * box.setTranslateZ(-100); box.setTranslateX(-100);
		 * box.setTranslateY(floor.getTranslateY() - (floor.getHeight() / 2) -
		 * (box.getHeight() / 2)); sphere.setTranslateX(100);
		 * sphere.setTranslateZ(-150);
		 * sphere.setTranslateY(floor.getTranslateY() - (floor.getHeight() / 2)
		 * - (sphere.getRadius()));
		 */

		// dodanie obiektów do głównej grupy obiektów aplikacji
		/*
		 * group.getChildren().add(sphere); group.getChildren().add(box);
		 * group.getChildren().add(floor); group.getChildren().add(leftWall);
		 * group.getChildren().add(rightWall);
		 * group.getChildren().add(middleWall);
		 */

		for (MeshView m : meshViewTable) {
			m.setTranslateZ(-100);
			m.setTranslateX(-100);
			m.setTranslateY(floor.getTranslateY() - (floor.getHeight() / 2) - (box.getHeight() / 2));
			group.getChildren().add(m);

		}

		group.setDepthTest(DepthTest.ENABLE);
		group.setTranslateX(0);
		group.setTranslateY(0);
		group.setTranslateZ(0);

		// tworzenie sceny wraz z obiektami
		scene = new Scene(group, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);

		// ustawienia kamery, kąta widzenia, itd.
		camera = new PerspectiveCamera(true);
		camera.setVerticalFieldOfView(false);
		camera.setNearClip(0.1);
		camera.setFarClip(10000.0);
		camera.getTransforms().addAll(new Translate(0, 0, -1000));
		camera.setTranslateX(0);
		camera.setTranslateZ(0);
		camera.setTranslateY(0);
		camera.setFieldOfView(40);

		// ustawienie głownej kamery, wybór kolor tła sceny, ustawienie głownej
		// sceny
		scene.setCamera(camera);
		scene.setFill(Color.BLANCHEDALMOND);
		primaryStage.setScene(scene);
		primaryStage.show();

		// synchronizacja z LeapMotion
		/*scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					System.out.println("UP");
					break;
				case DOWN:
					System.out.println("DOWN");
					break;
				case LEFT:
					System.out.println("LEFT");
					break;
				case RIGHT:
					System.out.println("RIGHT");
					break;

				}
			}
		});*/
		synchronizeWithLeapMotion();
		LeapApp.getMotionRegistry().addPointMotionListener(this);
	}

	// metoda umożliwiająca działanie animacji, czyli płynna zmiana pozycji rąk
	// w czasie
	@SuppressWarnings("restriction")
	private void synchronizeWithLeapMotion() {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0 / 60.0), ea -> LeapApp.update()));
		timeline.play();
	}

	// metoda identyfikuje ręce oraz ciągle aktualizuje ich położenie,
	// przy czym sprawdza czy zaszła kolizja między rękami a obiektami
	@Override
	@SuppressWarnings("restriction")
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
			collisionDetector.checkCollisionWithObjects(hand, meshViewTable, scene);
			// collisionDetector.checkCollisionWithObject(hand, sphere, floor,
			// box);
			// collisionDetector.checkCollisionWithObject(hand, box, floor,
			// sphere);
			// collisionDetector.checkCollisionWithObjects(hand, meshView,
			// floor, box);
			// collisionDetector.checkCollisionWithBoxAndSphere(box, sphere);
			// collisionDetector.setObjectInsideRoom(box, floor, leftWall,
			// rightWall, middleWall);
			// collisionDetector.setObjectInsideRoom(sphere, floor, leftWall,
			// rightWall, middleWall);
			// collisionDetector.setObjectInsideRoom(meshView, floor, leftWall,
			// rightWall, middleWall);
		}
	}

	@Override
	public void pointDragged(PointEvent event) {
	}
}