
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
	boolean boxUnderSphere = false;
	boolean sphereUnderBox = false;

	// sprawdza czy objekt nie przekracza ścian, jesli tak to położenie objektu
	// jest ustawiane koło ściany
	@SuppressWarnings("restriction")
	public void setObjectInsideRoom(Shape3D object, Box floor, Box leftWall, Box rightWall, Box middleWall) {
		double height = 0;
		double width = 0;
		if (object instanceof Box) {
			height = ((Box) object).getHeight();
			width = ((Box) object).getWidth();
		} else if (object instanceof Sphere) {
			height = ((Sphere) object).getRadius() * 2;
			width = ((Sphere) object).getRadius() * 2;
		}
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

	MeshView meshView = null;
	int index = -1;

	@SuppressWarnings("restriction")
	public void checkCollisionWithObjects(HandFX3D hand, List<MeshView> meshViewTable, Scene scene, Group group) {
		for (int i = 0; i < meshViewTable.size(); i++) {
			MeshView m = meshViewTable.get(i);
			if (index == -1 || index == i) {
				if ((m.getBoundsInParent().intersects(hand.getBoundsInParent())
						|| (m.getBoundsInParent().intersects(hand.getFingers()[0].getBoundsInParent())
								&& m.getBoundsInParent().intersects(hand.getFingers()[1].getBoundsInParent()))
						|| m.getBoundsInParent().intersects(hand.getInvisibleBone().getBoundsInParent()))
						&& !handIsStraight(hand)) {
					meshView = m;
					index = i;
					break;
				} else {
					meshView = null;
					index = -1;
				}
			}
		}

		if (meshView != null) {
			meshView.setTranslateX(hand.getPalm().getTranslateX());
			meshView.setTranslateY(hand.getPalm().getTranslateY());
			meshView.setTranslateZ(hand.getPalm().getTranslateZ());
			final MeshView meshViewFinal = meshView;
			final Group groupFinal = group;
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
					Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
					Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
					rxBox.setAngle(0);
					ryBox.setAngle(0);
					rzBox.setAngle(0);
					switch (event.getCode()) {
					case UP:
						rxBox.setAngle(3);
						System.out.println("UP");
						break;
					case DOWN:
						rxBox.setAngle(-3);
						System.out.println("DOWN");
						break;
					case LEFT:
						ryBox.setAngle(3);
						System.out.println("LEFT");
						break;
					case RIGHT:
						ryBox.setAngle(-3);
						System.out.println("RIGHT");
						break;
					case COMMA:
						rzBox.setAngle(3);
						System.out.println("COMMA");
						break;
					case PERIOD:
						rzBox.setAngle(-3);
						System.out.println("PERIOD");
						break;
					}
					meshViewFinal.getTransforms().addAll(rxBox, ryBox, rzBox);
				}
			});
		}
	}

	// sprawdzenie czy zaszła kolozja między rękami a objektem
	@SuppressWarnings("restriction")
	public void checkCollisionWithObject(HandFX3D hand, Shape3D object1, Box floor, Shape3D object2) {
		double objectHeight = 0;
		if (object1 instanceof Box) {
			objectHeight = ((Box) object1).getHeight();
		} else if (object1 instanceof Sphere) {
			objectHeight = ((Sphere) object1).getRadius() * 2;
		}
		if ((object1.getBoundsInParent().intersects(hand.getBoundsInParent())
				|| (object1.getBoundsInParent().intersects(hand.getFingers()[0].getBoundsInParent())
						&& object1.getBoundsInParent().intersects(hand.getFingers()[1].getBoundsInParent()))
				|| object1.getBoundsInParent().intersects(hand.getInvisibleBone().getBoundsInParent()))
				&& !handIsStraight(hand)) {
			object1.setTranslateX(hand.getPalm().getTranslateX());
			object1.setTranslateY(hand.getPalm().getTranslateY());
			object1.setTranslateZ(hand.getPalm().getTranslateZ());
		} else if (object1.getTranslateY() != (floor.getTranslateY() - (floor.getHeight() / 2) - (objectHeight / 2))) {
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

	@SuppressWarnings("restriction")
	// sprawdza czy zaszła kolizja pomiędzy sześcianem a kulą
	public boolean checkCollisionWithBoxAndSphere(Box box, Sphere sphere) {
		boolean collision = false;
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

	// sprawdza czy ręka jest wyprostowana
	@SuppressWarnings("restriction")
	public boolean handIsStraight(HandFX3D hand) {
		boolean isStraight = false;
		for (int i = 0; i < 5; i++) {
			Sphere[] proximal = hand.getProximal();
			Sphere[] fingers = hand.getFingers();
			Sphere[] distal = hand.getDistal();
			if (isBetween(proximal[i].getTranslateX(), fingers[i].getTranslateX(), distal[i].getTranslateX())
					&& isBetween(proximal[i].getTranslateY(), fingers[i].getTranslateY(), distal[i].getTranslateY())
					&& isBetween(proximal[i].getTranslateZ(), fingers[i].getTranslateZ(), distal[i].getTranslateZ())) {
				isStraight = true;
			}
		}

		return isStraight;
	}

	public Double distance(Double start, Double end) {
		if (start > end) {
			return start - end;
		} else {
			return end - start;
		}
	}

	public boolean isBetween(Double start, Double end, Double middle) {
		return distance(start, middle) + distance(middle, end) == distance(start, end);
	}
}