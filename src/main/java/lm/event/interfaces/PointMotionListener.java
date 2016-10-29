package lm.event.interfaces;

import lm.event.PointEvent;

public interface PointMotionListener {
	public void pointMoved(PointEvent event);

	public void pointDragged(PointEvent event);
}