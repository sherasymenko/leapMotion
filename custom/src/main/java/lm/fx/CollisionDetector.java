package lm.fx;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

@SuppressWarnings("restriction")
public class CollisionDetector {
	private boolean boxUnderSphere = false;
	private boolean sphereUnderBox = false;
	private MeshView meshView = null;
	private int index = -1;

	// sprawdza czy obiekt nie przekracza ścian, jesli tak to położenie obiektu
	// jest ustawiane koło ściany
	public void setObjectInsideRoom(Shape3D object, Box floor, Box leftWall, Box rightWall, Box middleWall) {
		double height = 0;
		double width = 0;
		// sprawdzenie czy obiekt jest sześcianem czy kulą, są pobierane
		// wysokość i szerokość obiektu
		if (object instanceof Box) {
			height = ((Box) object).getHeight();
			width = ((Box) object).getWidth();
		} else if (object instanceof Sphere) {
			height = ((Sphere) object).getRadius() * 2;
			width = ((Sphere) object).getRadius() * 2;
		}
		// sprawdzenie czy punkty obiektu przecinają się z "podłogą" lub
		// "ścianami", jeżeli tak - położenie obiektu jest ustawiane koło
		// "ściany" i na "podłodze"
		if (object.getBoundsInParent().intersects(floor.getBoundsInParent())) {
			object.setTranslateY(floor.getTranslateY() - (floor.getHeight() / 2) - (height / 2));
		}

		if (object.getBoundsInParent().intersects(leftWall.getBoundsInParent())) {
			object.setTranslateX(leftWall.getTranslateX() + (leftWall.getWidth() / 2) + (width / 2));
		}

		if (object.getBoundsInParent().intersects(rightWall.getBoundsInParent())) {
			object.setTranslateX(rightWall.getTranslateX() - (rightWall.getWidth() / 2) - (width / 2));
		}

		if (object.getBoundsInParent().intersects(middleWall.getBoundsInParent())) {
			object.setTranslateZ(leftWall.getTranslateZ() + (leftWall.getDepth() / 2) + (width / 2));
		}
	}

	// sprawdzenie czy zaszła kolozja między ręką a obiektem
	@SuppressWarnings("restriction")
	public void checkCollisionWithObject(HandFX3D hand, Shape3D object1, Box floor, Shape3D object2) {
		double objectHeight = 0;
		// sprawdzenie czy obiekt jest sześcianem czy kulą, są pobierane
		// wysokość obiektu
		if (object1 instanceof Box) {
			objectHeight = ((Box) object1).getHeight();
		} else if (object1 instanceof Sphere) {
			objectHeight = ((Sphere) object1).getRadius() * 2;
		}
		// sprawdzenie czy zaszła kolizja z ręką oraz czy ręka nie jest
		// wyprostowana
		if ((object1.getBoundsInParent().intersects(hand.getBoundsInParent())
				|| (object1.getBoundsInParent().intersects(hand.getFingers()[0].getBoundsInParent())
						&& object1.getBoundsInParent().intersects(hand.getFingers()[1].getBoundsInParent()))
				|| object1.getBoundsInParent().intersects(hand.getInvisibleBone().getBoundsInParent()))
				&& !handIsStraight(hand)) {
			// jeżeli zaszła kolizja z ręką i reką nie była przy tym
			// wyprostowana oznacza to że obiekt został uchwycony, położenie
			// obiektu jest ustawiane na takie, jakie ma środek dłoni ręki
			object1.setTranslateX(hand.getPalm().getTranslateX());
			object1.setTranslateY(hand.getPalm().getTranslateY());
			object1.setTranslateZ(hand.getPalm().getTranslateZ());
		} else if (object1.getTranslateY() != (floor.getTranslateY() - (floor.getHeight() / 2) - (objectHeight / 2))) {
			// jeżeli obiekt nie jest uchwycony, sprawdza się czy znajduje się
			// on na "podłodze", jeśli tak nie jest obiekt spada do dołu, aż do
			// momentu dotkniecia "podłogi" lub innego obiektu
			double objectHeightFinal = objectHeight;
			Timeline timeline = new Timeline();
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0 / 60.0), ea -> {
				Box box;
				Sphere sphere;
				if (object1 instanceof Box) {
					box = (Box) object1;
					sphere = (Sphere) object2;

				} else {
					box = (Box) object2;
					sphere = (Sphere) object1;
				}

				if (object1.getBoundsInParent().intersects(floor.getBoundsInParent())) {
					object1.setTranslateY(floor.getTranslateY() - (floor.getHeight() / 2) - (objectHeightFinal / 2));
				} else if (checkCollisionWithBoxAndSphere(box, sphere)) {
					boxUnderSphere = false;
					sphereUnderBox = false;
				} else {
					object1.setTranslateY(object1.getTranslateY() + 0.05);
				}
			}));
			timeline.play();
		}
	}

	// sprawdzenie czy zaszła kolizja pomiędzy sześcianem a kulą
	public boolean checkCollisionWithBoxAndSphere(Box box, Sphere sphere) {
		boolean collision = false;
		// sprawdzenie czy sześcian i kula się przecinają, jeżeli tak - obiekty
		// ustawiają sie jeden na drugim, w zależności od tego który był wyżej w
		// momencie wykrycia kolizji
		if (box.getBoundsInParent().intersects(sphere.getBoundsInParent())) {
			if (box.getTranslateY() < sphere.getTranslateY()) {
				box.setTranslateY(sphere.getTranslateY() - (sphere.getRadius()) - (box.getHeight() / 2) - 0.01);
				boxUnderSphere = true;
			} else if (box.getTranslateY() > sphere.getTranslateY()) {
				sphere.setTranslateY(box.getTranslateY() - (box.getHeight() / 2) - (sphere.getRadius()) - 0.01);
				sphereUnderBox = true;
			} else {
				collision = false;
				boxUnderSphere = false;
				sphereUnderBox = false;
			}
			collision = true;
		}
		return collision;
	}

	// sprawdzenie czy zaszła kolizja pomiędzy ręką a wgranymi obiektami
	public void checkCollisionWithLoadObjects(HandFX3D hand, List<MeshView> meshList, Scene s) {
		for (int i = 0; i < meshList.size(); i++) {
			MeshView m = meshList.get(i);
			// poszukiwanie obiektu, którego dotyka ręka, przy czym ręka nie
			// może być wyprostowana
			if (index == -1 || index == i) {
				if ((m.getBoundsInParent().intersects(hand.getBoundsInParent())
						|| (m.getBoundsInParent().intersects(hand.getFingers()[0].getBoundsInParent())
								&& m.getBoundsInParent().intersects(hand.getFingers()[1].getBoundsInParent()))
						|| m.getBoundsInParent().intersects(hand.getInvisibleBone().getBoundsInParent()))
						&& !handIsStraight(hand)) {
					// jeżeli taki obiekt się znalazł, jest on ustawiany
					meshView = m;
					index = i;
					break;
				} else {
					// jeżeli obiekt się nie znalazł, ustawiamy wartość null
					meshView = null;
					index = -1;
				}
			}
		}
		// sterowanie obiektem
		controlObject(s, hand, meshView);
	}

	// sterowanie obiektem
	public void controlObject(Scene scene, HandFX3D hand, MeshView meshView) {
		if (meshView != null) {
			// jeżeli ręka trzyma jakiś obiekt to jego położenie jest ustawiane
			// na takie, jakie ma środek dłoni ręki, obiekt się przemieszcza
			// razem z ręką
			meshView.setTranslateX(hand.getPalm().getTranslateX());
			meshView.setTranslateY(hand.getPalm().getTranslateY());
			meshView.setTranslateZ(hand.getPalm().getTranslateZ());
			final MeshView meshViewFinal = meshView;
			// trzymając obiekt można go obracać w trzech osiach za pomocą
			// przycisków ↑,↓,←,→,<,>
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@SuppressWarnings("incomplete-switch")
				@Override
				public void handle(KeyEvent event) {
					Rotate rx = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
					Rotate ry = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
					Rotate rz = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
					rx.setAngle(0);
					ry.setAngle(0);
					rz.setAngle(0);
					switch (event.getCode()) {
					case UP:
						rx.setAngle(3);
						break;
					case DOWN:
						rx.setAngle(-3);
						break;
					case LEFT:
						ry.setAngle(3);
						break;
					case RIGHT:
						ry.setAngle(-3);
						break;
					case COMMA:
						rz.setAngle(3);
						break;
					case PERIOD:
						rz.setAngle(-3);
						break;
					}
					meshViewFinal.getTransforms().addAll(rx, ry, rz);
				}
			});
		}
	}

	// sprawdzenie czy ręka jest wyprostowana
	public boolean handIsStraight(HandFX3D hand) {
		boolean isStraight = false;
		// sprawdzenie dla każdego palca
		for (int i = 0; i < 5; i++) {
			// pobierane są 3 kostki każdego palca
			Sphere[] proximal = hand.getProximal();
			Sphere[] fingers = hand.getFingers();
			Sphere[] distal = hand.getDistal();
			// sprawdzenie czy te 3 punkty lezą na jednej osi
			if (isBetween(proximal[i].getTranslateX(), fingers[i].getTranslateX(), distal[i].getTranslateX())
					&& isBetween(proximal[i].getTranslateY(), fingers[i].getTranslateY(), distal[i].getTranslateY())
					&& isBetween(proximal[i].getTranslateZ(), fingers[i].getTranslateZ(), distal[i].getTranslateZ())) {
				isStraight = true;
			}
		}

		return isStraight;
	}

	public boolean isBetween(Double start, Double end, Double middle) {
		return distance(start, middle) + distance(middle, end) == distance(start, end);
	}

	public Double distance(Double start, Double end) {
		if (start > end) {
			return start - end;
		} else {
			return end - start;
		}
	}
}