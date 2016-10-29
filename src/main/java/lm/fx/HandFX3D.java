package lm.fx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

public class HandFX3D extends Group {
	boolean test1 = false;
	//poszczeg�lne cz�ci r�ki
	private Sphere palm;
	private Sphere metacarpal;
	private Sphere[] fingers = new Sphere[5];
	private Sphere[] distal = new Sphere[5];
	private Sphere[] proximal = new Sphere[5];
	private Sphere[] intermediate = new Sphere[5];

	List<Sphere> sphereList = new ArrayList<Sphere>();

	private List<JointFX3D> joints = new ArrayList<JointFX3D>();

	// tworzenie r�ki: na miejscu kostek s� tworzone kule,
	// kt�re s� po��czone cylindrami, 
	// co upodabnia wygl�d konstrukcji do r�ki
	public HandFX3D(int handId) {
		palm = createSphere();
		metacarpal = createSphere();
		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = createSphere();
			distal[i] = createSphere();
			intermediate[i] = createSphere();
			proximal[i] = createSphere();
			getChildren().addAll(fingers[i], distal[i], proximal[i], intermediate[i]);
		}
		sphereList.add(palm);
		sphereList.add(metacarpal);
		getChildren().addAll(palm, metacarpal);
		for (int i = 0; i < fingers.length; i++) {
			connectSpheres(fingers[i], distal[i], false);
			connectSpheres(distal[i], intermediate[i], false);
			connectSpheres(intermediate[i], proximal[i], false);
			sphereList.add(fingers[i]);
			sphereList.add(distal[i]);
			sphereList.add(intermediate[i]);

		}
		connectSpheres(proximal[1], proximal[2], false);
		connectSpheres(proximal[2], proximal[3], false);
		connectSpheres(proximal[3], proximal[4], false);
		connectSpheres(proximal[0], proximal[1], false);
		connectSpheres(proximal[0], metacarpal, false);
		connectSpheres(metacarpal, proximal[4], false);
		connectSpheres(metacarpal, palm, true);
	}

	private Sphere createSphere() {
		Sphere sphere = new Sphere(4);
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.BLUE);
		material.setDiffuseColor(Color.BLACK);
		sphere.setMaterial(material);
		return sphere;
	}

	private void connectSpheres(Sphere fromSphere, Sphere toSphere, boolean test) {
		if(test){
			test1 = true;}else{test1 = false;}
		JointFX3D jointFX3D = new JointFX3D(fromSphere, toSphere);
		joints.add(jointFX3D);
		getChildren().add(jointFX3D.getBone());
	}

	// metoda aktualizuje po�o�enie r�ki
	public void update(Hand hand) {
		transform(palm, hand.palmPosition());
		Iterator<Finger> itFinger = hand.fingers().iterator();
		Finger finger = null;
		for (int i = 0; i < fingers.length; i++) {
			finger = itFinger.next();
			transform(fingers[i], finger.tipPosition());
			transform(distal[i], finger.bone(Type.TYPE_DISTAL).prevJoint());
			transform(intermediate[i], finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			transform(proximal[i], finger.bone(Type.TYPE_PROXIMAL).prevJoint());
		}
		transform(metacarpal, finger.bone(Type.TYPE_METACARPAL).prevJoint());
		for (JointFX3D joint : joints) {
			joint.update(test1);
		}
	}

	private static void transform(Node node, Vector vector) {
		node.setTranslateX(vector.getX());
		node.setTranslateY(-vector.getY());
		node.setTranslateZ(-vector.getZ());
	}

	// metody getTranslates... zwracaj� listy sp�rz�dnych ka�dej cz�ci r�ki
	public List<Double> getTranslatesX() {
		List<Double> translatesList = new ArrayList<Double>();
		for (Iterator i = sphereList.iterator(); i.hasNext();) {
			Sphere sphere = (Sphere) i.next();
			translatesList.add(sphere.getTranslateX());
		}
		return translatesList;
	}

	public List<Double> getTranslatesY() {
		List<Double> translatesList = new ArrayList<Double>();
		for (Iterator i = sphereList.iterator(); i.hasNext();) {
			Sphere sphere = (Sphere) i.next();
			translatesList.add(sphere.getTranslateY());
		}
		return translatesList;
	}

	public List<Double> getTranslatesZ() {
		List<Double> translatesList = new ArrayList<Double>();
		for (Iterator i = sphereList.iterator(); i.hasNext();) {
			Sphere sphere = (Sphere) i.next();
			translatesList.add(sphere.getTranslateZ());
		}
		return translatesList;
	}

	// ��czenie cz�sci r�k
	private class JointFX3D {
		private Sphere fromSphere;
		private Sphere toSphere;
		private Cylinder bone;
		private Rotate joint;

		public JointFX3D(Sphere fromSphere, Sphere toSphere) {
			this.fromSphere = fromSphere;
			this.toSphere = toSphere;
			this.joint = new Rotate();
			this.bone = createBone(joint);
		}

		private Cylinder createBone(Rotate joint) {
			PhongMaterial material = new PhongMaterial();
			material.setSpecularColor(Color.WHITE);
			material.setDiffuseColor(Color.GREY);
			Cylinder cylinder = new Cylinder();
			cylinder.setRadius(2);
			cylinder.setMaterial(material);
			cylinder.getTransforms().add(joint);
			return cylinder;
		}

		public void update(boolean test2) {
			double dx = (float) (fromSphere.getTranslateX() - toSphere.getTranslateX());
			double dy = (float) (fromSphere.getTranslateY() - toSphere.getTranslateY());
			double dz = (float) (fromSphere.getTranslateZ() - toSphere.getTranslateZ());
			bone.setHeight(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2)));
			bone.setTranslateX(fromSphere.getTranslateX());
			bone.setTranslateY(fromSphere.getTranslateY() - bone.getHeight() / 2);
			bone.setTranslateZ(fromSphere.getTranslateZ());
			joint.setPivotY(bone.getHeight() / 2);
			joint.setAxis(new Point3D(dz, 0, -dx));
			joint.setAngle(180 - new Point3D(dx, -dy, dz).angle(Rotate.Y_AXIS));
			/*if(test2)
			palm.setRotationAxis(joint.getAxis());*/
		}

		public Cylinder getBone() {
			return bone;
		}
	}

	public Sphere getPalm() {
		return palm;
	}

	public void setPalm(Sphere palm) {
		this.palm = palm;
	}
	
}