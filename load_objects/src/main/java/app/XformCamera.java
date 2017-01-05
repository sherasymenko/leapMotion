package app;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

@SuppressWarnings("restriction")
class XformCamera extends Group {
	Point3D px = new Point3D(1.0, 0.0, 0.0);
	Point3D py = new Point3D(0.0, 1.0, 0.0);
	Rotate r;
	Transform t = new Rotate();

	public XformCamera() {
		super();
	}

	public void rx(double angle) {
		r = new Rotate(angle, px);
		this.t = t.createConcatenation(r);
		this.getTransforms().clear();
		this.getTransforms().addAll(t);
	}

	public void ry(double angle) {
		r = new Rotate(angle, py);
		this.t = t.createConcatenation(r);
		this.getTransforms().clear();
		this.getTransforms().addAll(t);
	}
}