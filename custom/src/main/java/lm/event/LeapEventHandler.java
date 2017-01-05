package lm.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;

import lm.LeapApp;
import lm.event.interfaces.LeapListener;

public final class LeapEventHandler extends Listener {
	private static final LeapEventHandler leapEventHandler = new LeapEventHandler();

	private List<LeapListener> leapListeners;
	private LeapEvent leapEvent;

	private LeapEventHandler() {
		leapListeners = new CopyOnWriteArrayList<LeapListener>();
		leapEvent = new LeapEvent();
	}

	public static LeapEventHandler getInstance() {
		return leapEventHandler;
	}

	public static void addLeapListener(LeapListener leapListener) {
		leapEventHandler.leapListeners.add(leapListener);
	}

	public static void removeLeapListener(LeapListener leapListener) {
		leapEventHandler.leapListeners.remove(leapListener);
	}

	public static void removeAllLeapListener() {
		leapEventHandler.leapListeners.clear();
	}

	public static void fireFrameUpdate() {
		leapEventHandler.onFrame(LeapApp.getController());
	}

	@Override
	public void onDisconnect(Controller controller) {
		leapEvent.setDisconnected(true);
		fireStatusChanged();
	}

	@Override
	public void onExit(Controller controller) {
		leapEvent.setExited(true);
		fireStatusChanged();
	}

	@Override
	public void onFocusLost(Controller controller) {
		leapEvent.setFocusLost(true);
		fireStatusChanged();
	}

	@Override
	public void onFrame(Controller controller) {
		if (controller.frame().hands().count() >= LeapApp.getMinimumHandNumber()) {
			leapEvent.setFrame(controller.frame());
			for (LeapListener leapListener : leapListeners) {
				leapListener.update(leapEvent);
			}
		}
	}

	private void fireStatusChanged() {
		for (LeapListener leapListener : leapListeners) {
			leapListener.statusChanged(leapEvent);
		}
	}

	public static void updateFrame() {
		leapEventHandler.onFrame(LeapApp.getController());
	}
}