package lm.fx;

import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class CollisionDetector {
	private PhongMaterial phongMaterial = new PhongMaterial();

	// sprawdzenie czy zasz�a kolozja mi�dzy r�kami a kul�
	public void checkCollisionWithSphere(HandFX3D hand, Sphere sphere, Box box) {
		boolean collisionDetected = false;
		List<Double> handTranslatesX = hand.getTranslatesX();
		List<Double> handTranslatesY = hand.getTranslatesY();
		List<Double> handTranslatesZ = hand.getTranslatesZ();

		Double liczbaOdX = sphere.getTranslateX() - sphere.getRadius();
		Double liczbaDoX = sphere.getTranslateX() + sphere.getRadius();
		Double liczbaOdY = sphere.getTranslateY() - sphere.getRadius();
		Double liczbaDoY = sphere.getTranslateY() + sphere.getRadius();
		Double liczbaOdZ = sphere.getTranslateZ() - sphere.getRadius();
		Double liczbaDoZ = sphere.getTranslateZ() + sphere.getRadius();
		for (int k = 0; k < handTranslatesX.size(); k++) {
			if ((handTranslatesX.get(k) > liczbaOdX && handTranslatesX.get(k) < liczbaDoX)
					&& (handTranslatesY.get(k) > liczbaOdY && handTranslatesY.get(k) < liczbaDoY)
					&& (handTranslatesZ.get(k) > liczbaOdZ && handTranslatesZ.get(k) < liczbaDoZ)) {
				collisionDetected = true;
			}
		}
		if (collisionDetected) {
			phongMaterial.setSpecularColor(Color.color(rand(), rand(), rand()));
			phongMaterial.setDiffuseColor(Color.color(rand(), rand(), rand()));
			box.setMaterial(phongMaterial);
		}
	}

	public double rand() {
		Random random = new Random();
		return random.nextDouble();
	}
	// sprawdzenie czy zasz�a kolozja mi�dzy r�kami a sze�cianem
	public void checkCollisionWithBox(HandFX3D hand, Box box) {
		boolean collisionDetected = false;
		Double collisionPointX = 0.0;
		List<Double> handTranslatesX = hand.getTranslatesX();
		List<Double> handTranslatesY = hand.getTranslatesY();
		List<Double> handTranslatesZ = hand.getTranslatesZ();

		Double liczbaOdX = box.getTranslateX() - box.getWidth() / 2;
		Double liczbaDoX = box.getTranslateX() + box.getWidth() / 2;
		Double liczbaOdY = box.getTranslateY() - box.getWidth() / 2;
		Double liczbaDoY = box.getTranslateY() + box.getWidth() / 2;
		Double liczbaOdZ = box.getTranslateZ() - box.getWidth() / 2;
		Double liczbaDoZ = box.getTranslateZ() + box.getWidth() / 2;
		
		for (int j = 0; j < handTranslatesX.size(); j++) {
			if ((handTranslatesX.get(j) > liczbaOdX && handTranslatesX.get(j) < liczbaDoX)
					&& (handTranslatesY.get(j) > liczbaOdY && handTranslatesY.get(j) < liczbaDoY)
					&& (handTranslatesZ.get(j) > liczbaOdZ && handTranslatesZ.get(j) < liczbaDoZ)) {
				collisionDetected = true;
				collisionPointX = handTranslatesX.get(j);
			}
		}
		 
	/*Sphere palm = hand.getPalm();
			box.setTranslateX(hand.getPalm().getTranslateX());
			box.setTranslateY(hand.getPalm().getTranslateY()+box.getWidth()/2+hand.getPalm().getRadius());
			box.setTranslateZ(hand.getPalm().getTranslateZ());
			
			
			 final Rotate boxRotateX = new Rotate(palm.getRotationAxis().getX(), Rotate.X_AXIS);
			 final Rotate boxRotateY = new Rotate(palm.getRotationAxis().getY(), Rotate.Y_AXIS);
			 final Rotate boxRotateZ = new Rotate(palm.getRotationAxis().getZ(), Rotate.Z_AXIS);
			System.out.println("test 1  " +palm.getRotationAxis().getX());
			
			box.getTransforms().remove(0, box.getTransforms().size()-1);
			
			box.getTransforms().addAll(boxRotateX, boxRotateY, boxRotateZ, new Translate(0, 0, 0));*/
			//System.out.println("test 2  " +box.getTransforms() );
		if (collisionDetected) {
			if (collisionPointX < box.getTranslateX()) {
				box.setTranslateX(box.getTranslateX() + 0.3);
			} else {
				box.setTranslateX(box.getTranslateX() - 0.3);
			}
		}
	}
}