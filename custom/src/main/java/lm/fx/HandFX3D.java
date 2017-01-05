package lm.fx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.shape.Shape3D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

public class HandFX3D extends Group {
	
	// poszczegďż˝lne czďż˝ci rďż˝ki
	private Sphere palm;
	private Sphere metacarpal;
	private Sphere[] fingers = new Sphere[5];
	private Sphere[] distal = new Sphere[5];
	private Sphere[] proximal = new Sphere[5];
	private Sphere[] intermediate = new Sphere[5];
	private Cylinder invisibleBone;
	private Shape3D object;
	private Rotate handRotate;
	List<Sphere> sphereList = new ArrayList<Sphere>();

	private List<JointFX3D> joints = new ArrayList<JointFX3D>();

	// tworzenie rďż˝ki: na miejscu kostek sďż˝ tworzone kule,
	// ktďż˝re sďż˝ poďż˝ďż˝czone cylindrami,
	// co upodabnia wyglďż˝d konstrukcji do rďż˝ki
	public HandFX3D(int handId) {
		palm = createSphere();
		metacarpal = createSphere();
		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = createSphere();
		//	fingers[i].setMaterial(new PhongMaterial(Color.RED));
			distal[i] = createSphere();
			//distal[i].setMaterial(new PhongMaterial(Color.GREEN));
			intermediate[i] = createSphere();
			//intermediate[i].setMaterial(new PhongMaterial(Color.BLACK));
			proximal[i] = createSphere();
			//proximal[i].setMaterial(new PhongMaterial(Color.YELLOW));
			getChildren().addAll(fingers[i], distal[i], proximal[i], intermediate[i]);
		}
		sphereList.add(palm);
		sphereList.add(metacarpal);
		getChildren().addAll(palm, metacarpal);
		for (int i = 0; i < fingers.length; i++) {
			connectSpheres(fingers[i], distal[i], true);
			connectSpheres(distal[i], intermediate[i], true);
			connectSpheres(intermediate[i], proximal[i], true);
			sphereList.add(fingers[i]);
			sphereList.add(distal[i]);
			sphereList.add(intermediate[i]);

		}fingers[0].setMaterial(new PhongMaterial(Color.WHITE));
		connectSpheres(proximal[1], proximal[2], true);
		connectSpheres(proximal[2], proximal[3], true);
		connectSpheres(proximal[3], proximal[4], true);
		connectSpheres(proximal[0], proximal[1], true);
		connectSpheres(proximal[0], metacarpal, true);
		connectSpheres(metacarpal, proximal[4], true);
		//connectSpheres(metacarpal, palm, true);
		
		connectSpheres(fingers[0], palm, false);
	}

	private Sphere createSphere() {
		Sphere sphere = new Sphere(4);
		PhongMaterial material = new PhongMaterial();
		material.setSpecularColor(Color.BLUE);
		material.setDiffuseColor(Color.BLACK);
		sphere.setMaterial(material);
		return sphere;
	}

	private void connectSpheres(Sphere fromSphere, Sphere toSphere, boolean visible) {
		
		
		JointFX3D jointFX3D = new JointFX3D(fromSphere, toSphere,visible,object);
		joints.add(jointFX3D);
		getChildren().add(jointFX3D.getBone());
		
	}

	// metoda aktualizuje poďż˝oďż˝enie rďż˝ki
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
			joint.update();
			if(joint.getInvisibleBone()!=null){
				setHandRotate(joint.getJoint());
				setInvisibleBone(joint.getInvisibleBone());
			}
		}
	}

	private static void transform(Node node, Vector vector) {
		node.setTranslateX(vector.getX());
		node.setTranslateY(-vector.getY());
		node.setTranslateZ(-vector.getZ());
	}

	// metody getTranslates... zwracajďż˝ listy spďż˝rzďż˝dnych kaďż˝dej czďż˝ci rďż˝ki
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

	// laczenie czesci
	private class JointFX3D {
		private Sphere fromSphere;
		private Sphere toSphere;
		private Cylinder bone;
		private Shape3D object;
		private Rotate joint;
		Boolean visible;
		
		private Cylinder invisibleBone;

		public JointFX3D(Sphere fromSphere, Sphere toSphere,Boolean visible, Shape3D object) {
			this.fromSphere = fromSphere;
			this.toSphere = toSphere;
			this.joint = new Rotate();
			this.bone = createBone(joint);
			this.visible = visible;
			this.object = object;
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
		
		

		public void update() {
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
			bone.setRotate(joint.determinant());
			   bone.setVisible(visible);
			   
			   if(!visible){
				   setInvisibleBone(bone);
				  
			   }
			
		}

		public Sphere getFromSphere() {
			return fromSphere;
		}

		public void setFromSphere(Sphere fromSphere) {
			this.fromSphere = fromSphere;
		}

		public Sphere getToSphere() {
			return toSphere;
		}

		public void setToSphere(Sphere toSphere) {
			this.toSphere = toSphere;
		}

		public Cylinder getBone() {
			return bone;
		}

		public void setBone(Cylinder bone) {
			this.bone = bone;
		}

		public Rotate getJoint() {
			return joint;
		}

		public void setJoint(Rotate joint) {
			this.joint = joint;
		}

		public Boolean getVisible() {
			return visible;
		}

		public void setVisible(Boolean visible) {
			this.visible = visible;
		}

		public Cylinder getInvisibleBone() {
			return invisibleBone;
		}

		public void setInvisibleBone(Cylinder invisibleBone) {
			this.invisibleBone = invisibleBone;
		}

		
	}

	public Sphere getPalm() {
		return palm;
	}

	public void setPalm(Sphere palm) {
		this.palm = palm;
	}

	public Sphere getMetacarpal() {
		return metacarpal;
	}

	public void setMetacarpal(Sphere metacarpal) {
		this.metacarpal = metacarpal;
	}

	public Sphere[] getFingers() {
		return fingers;
	}

	public void setFingers(Sphere[] fingers) {
		this.fingers = fingers;
	}

	public Sphere[] intermediate() {
		return distal;
	}

	public void setDistal(Sphere[] distal) {
		this.distal = distal;
	}
	public Sphere[] getDistal() {
		return distal;
	}
	public Sphere[] getProximal() {
		return proximal;
	}

	public void setProximal(Sphere[] proximal) {
		this.proximal = proximal;
	}

	public Sphere[] getIntermediate() {
		return intermediate;
	}

	public void setIntermediate(Sphere[] intermediate) {
		this.intermediate = intermediate;
	}

	public List<JointFX3D> getJoints() {
		return joints;
	}

	public void setJoints(List<JointFX3D> joints) {
		this.joints = joints;
	}

	public Cylinder getInvisibleBone() {
		return invisibleBone;
	}

	public void setInvisibleBone(Cylinder invisibleBone) {
		this.invisibleBone = invisibleBone;
	}

	public Shape3D getObject() {
		return object;
	}

	public void setObject(Shape3D object) {
		this.object = object;
	}

	public Rotate getHandRotate() {
		return handRotate;
	}

	public void setHandRotate(Rotate handRotate) {
		this.handRotate = handRotate;
	}
	
}