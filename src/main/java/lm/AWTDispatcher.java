package lm;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import lm.event.PointEvent;
import lm.event.PointEvent.Zone;
import lm.event.interfaces.PointListener;
import lm.event.interfaces.PointMotionListener;

public class AWTDispatcher extends WindowAdapter implements PointListener, PointMotionListener {
	private Window window;

	private void dispatchMouseEvent(PointEvent event, int id) {
		dispatchMouseEvent(event, id, 0, 0);
	}

	private void dispatchMouseEvent(PointEvent event, int id, int modifiers, int button) {
		if (window != null) {
			Component component = window.findComponentAt((int) event.getX(), (int) event.getY());
			if (component != null) {
				window.dispatchEvent(new MouseEvent(component, id, System.currentTimeMillis(), modifiers,
						(int) event.getX(), (int) event.getY(), 0, false, button));
			}
		}
	}

	public void pointMoved(PointEvent event) {
		dispatchMouseEvent(event, MouseEvent.MOUSE_MOVED);
	}

	public void pointDragged(PointEvent event) {
		dispatchMouseEvent(event, MouseEvent.MOUSE_DRAGGED);
	}

	public void zoneChanged(PointEvent event) {
		if (event.isInZone(Zone.BACK)) {
			dispatchMouseEvent(event, MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);
		} else if ((event.leftViewPort() && event.wasInClickZone())
				|| (event.wasInClickZone() && !event.isInClickZone())) {
			dispatchMouseEvent(event, MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);
			dispatchMouseEvent(event, MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		window = e.getWindow();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		LeapApp.destroy();
	}
}