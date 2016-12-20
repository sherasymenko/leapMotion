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
public class TestClass extends Application {

	private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

	private final Group group = new Group();
	private PerspectiveCamera camera;
	private Scene scene;
	private Map<Integer, HandFX3D> hands = new HashMap<Integer, HandFX3D>();
	private final double sceneWidth = 800;
	private final double sceneHeight = 600;

	private Box box = new Box(50, 50, 50);

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

	/*	StlMeshImporter stlImporter123 = new StlMeshImporter();
		try {
			stlImporter123.read(this.getClass().getResource("/stl/skorupa1.stl"));
		} catch (ImportException e) {
			e.printStackTrace();
			return;
		}
		TriangleMesh mesh = stlImporter123.getImport();

		MeshView meshView = new MeshView(mesh);
		meshView.getTransforms().addAll(rotateX, rotateY, rotateZ, new Translate(0, 0, 0));
		stlImporter123.close();
		meshView.setTranslateZ(-100);
		meshView.setTranslateX(-100);

		group.getChildren().add(meshView);*/

		box.getTransforms().addAll(rotateX, rotateY, rotateZ, new Translate(0, 0, 0));
		box.setMaterial(new PhongMaterial(Color.KHAKI));
		box.setTranslateZ(-100);
		box.setTranslateX(-100);
		group.getChildren().add(box);

		group.setDepthTest(DepthTest.ENABLE);
		group.setTranslateX(0);
		group.setTranslateY(0);
		group.setTranslateZ(0);

		// tworzenie sceny wraz z obiektami
		scene = new Scene(group, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);

		// ustawienia kamery, kąta widzenia, itd.
		camera = new PerspectiveCamera(true);
		// camera.setVerticalFieldOfView(false);
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
		Box testFinal = box;
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				Rotate rotateX = new Rotate(testFinal.getRotationAxis().getX(), Rotate.X_AXIS);
				Rotate rotateY = new Rotate(testFinal.getRotationAxis().getY(), Rotate.Y_AXIS);
				Rotate rotateZ = new Rotate(testFinal.getRotationAxis().getZ(), Rotate.Z_AXIS);
				switch (event.getCode()) {
				case UP:
					rotateX = new Rotate(testFinal.getRotationAxis().getX() + 1);
					//rotateY = new Rotate(testFinal.getRotationAxis().getY() + 1);
					System.out.println("UP");
					break;
				case DOWN:
					rotateX = new Rotate(testFinal.getRotationAxis().getX() - 1);
					//rotateY = new Rotate(testFinal.getRotationAxis().getY() - 1);
					System.out.println("DOWN");
					break;
				case LEFT:
					rotateY = new Rotate(testFinal.getRotationAxis().getY() + 1, Rotate.Y_AXIS);
					System.out.println("LEFT");
					break;
				case RIGHT:
					rotateY = new Rotate(testFinal.getRotationAxis().getY() - 1, Rotate.Y_AXIS);
					System.out.println("RIGHT");
					break;
				case X:
					rotateX = new Rotate(testFinal.getRotationAxis().getX() + 1, Rotate.X_AXIS);
					System.out.println("X");
					break;
				case Y:
					rotateY = new Rotate(testFinal.getRotationAxis().getY() + 1, Rotate.Y_AXIS);
					System.out.println("Y");
					break;
				case Z:
					rotateZ = new Rotate(testFinal.getRotationAxis().getZ() + 1, Rotate.Z_AXIS);
					System.out.println("Z");
					break;
				}
				testFinal.getTransforms().addAll(rotateX, rotateY, rotateZ, new Translate(0, 0, 0));
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}