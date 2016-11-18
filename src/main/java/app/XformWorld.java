package app;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

class XformWorld extends Group {
	final Translate t = new Translate(0.0, 0.0, 0.0);
	final Rotate rx = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
	final Rotate ry = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
	final Rotate rz = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);

	public XformWorld() {
		super();
		this.getTransforms().addAll(t, rx, ry, rz);
	}
}