package app;

import java.util.HashMap;
import java.util.Map;

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

public class MainClass extends Application implements PointMotionListener {

	private final Rotate boxRotateX = new Rotate(20, Rotate.X_AXIS);
	private final Rotate boxRotateY = new Rotate(20, Rotate.Y_AXIS);
	private final Rotate boxRotateZ = new Rotate(0, Rotate.Z_AXIS);
	private final Rotate floorRotateX = new Rotate(20, Rotate.X_AXIS);
	private final Rotate floorRotateY = new Rotate(20, Rotate.Y_AXIS);
	private final Rotate floorRotateZ = new Rotate(0, Rotate.Z_AXIS);

	private final Group group = new Group();
	private PerspectiveCamera camera;
	private Scene scene;
	private Map<Integer, HandFX3D> hands = new HashMap<Integer, HandFX3D>();
	private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private final double sceneWidth = 800;
	private final double sceneHeight = 600;
	private Sphere sphere = new Sphere(30);
	private Box box = new Box(100, 100, 100);
	private Box floor = new Box(1000, 10, 1000);
	private CollisionDetector collisionDetector = new CollisionDetector();

	public static void main(String[] args) {
		LeapApp.init(true);
		LeapApp.setMode(Mode.INTERACTION_BOX);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// 3D objekty aplikacji: kula, sześcian, podłoga
		sphere.setTranslateX(0);
		sphere.setTranslateY(-150);
		sphere.setTranslateZ(50);
		sphere.setMaterial(new PhongMaterial(Color.AQUA));
		box.getTransforms().addAll(boxRotateX, boxRotateY, boxRotateZ, new Translate(0, 0, 0));
		floor.getTransforms().addAll(floorRotateX, floorRotateY, floorRotateZ, new Translate(0, 0, 0));
		floor.setMaterial(new PhongMaterial(Color.CORNSILK));
		floor.setTranslateY(150);

		// dodanie obiektów do głównej grupy obiektów aplikacji
		group.getChildren().add(box);
		group.getChildren().add(sphere);
		group.getChildren().add(floor);
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
		camera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, -650));
		camera.setTranslateX(0);
		camera.setTranslateZ(0);
		camera.setTranslateY(0);
		camera.setFieldOfView(40);

		// ustawienie głownej kameery, wybór kolor tła sceny, ustawienie głownej
		// sceny
		scene.setCamera(camera);
		scene.setFill(Color.BLANCHEDALMOND);
		primaryStage.setScene(scene);
		primaryStage.show();

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
			group.getChildren().add(hand);
		}
		if (hand != null) {
			hand.update(LeapApp.getController().frame().hand(handId));
			collisionDetector.checkCollisionWithSphere(hand, sphere, box);
			collisionDetector.checkCollisionWithBox(hand, box);
		}
	}

	@Override
	public void pointDragged(PointEvent event) {
	}
}