package lm;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;

import com.leapmotion.leap.Controller;

import lm.event.LeapEventHandler;

public final class LeapApp {
	public enum Mode {
		DYNAMIC_ONE_SIDE, INTERACTION_BOX;
	}

	private static LeapApp leapApp;
	private MotionRegistry motionRegistry;
	private AWTDispatcher awtDispatcher;
	private Controller controller;
	private Mode mode;
	private int displayWidth;
	private int displayHeight;
	private int maximumHandNumber;
	private int minimumHandNumber;

	private LeapApp(boolean activatePolling) {
		NativeLibrary.loadSystem("native");
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode displayMode = device.getDisplayMode();
		displayWidth = displayMode.getWidth();
		displayHeight = displayMode.getHeight();
		controller = new Controller();
		if (!activatePolling) {
			controller.addListener(LeapEventHandler.getInstance());
		}
		mode = Mode.DYNAMIC_ONE_SIDE;
		maximumHandNumber = Integer.MAX_VALUE;
		motionRegistry = new MotionRegistry();
		LeapEventHandler.addLeapListener(motionRegistry);
	}

	public static void init(boolean activePolling) {
		if (leapApp == null) {
			leapApp = new LeapApp(activePolling);
		}
	}

	public static LeapApp getInstance() {
		return leapApp;
	}

	public static MotionRegistry getMotionRegistry() {
		return leapApp.motionRegistry;
	}

	public static int getDisplayWidth() {
		return leapApp.displayWidth;
	}

	public static void setDisplayWidth(int displayWidth) {
		leapApp.displayWidth = displayWidth;
	}

	public static int getDisplayHeight() {
		return leapApp.displayHeight;
	}

	public static void setDisplayHeight(int displayHeight) {
		leapApp.displayHeight = displayHeight;
	}

	public void setMotionRegistry(MotionRegistry motionRegistry) {
		if (motionRegistry != null) {
			LeapEventHandler.removeLeapListener(this.motionRegistry);
			this.motionRegistry = motionRegistry;
			LeapEventHandler.addLeapListener(motionRegistry);
		}
	}

	public static WindowAdapter getAndSetupAWTMouseListener() {
		if (leapApp.awtDispatcher == null) {
			leapApp.awtDispatcher = new AWTDispatcher();
			leapApp.motionRegistry.setAWTDispatcher(leapApp.awtDispatcher);
		}
		return leapApp.awtDispatcher;
	}

	public static void setMinimumHandNumber(int minimumHandNumer) {
		leapApp.minimumHandNumber = minimumHandNumer;
	}

	public static int getMinimumHandNumber() {
		return leapApp.minimumHandNumber;
	}

	public static void setMaximumHandNumber(int maximumHandNumer) {
		leapApp.maximumHandNumber = maximumHandNumer;
	}

	public static int getMaximumHandNumber() {
		return leapApp.maximumHandNumber;
	}

	public static void setMode(Mode mode) {
		leapApp.mode = mode;
	}

	public static Mode getMode() {
		return leapApp.mode;
	}

	public static Controller getController() {
		return leapApp.controller;
	}

	public static void destroy() {
		LeapEventHandler.removeAllLeapListener();
		leapApp.controller.delete();
		try {
			leapApp.finalize();
		} catch (Throwable t) {
		} finally {
			leapApp = null;
			System.exit(0);
		}
	}

	public static void update() {
		LeapEventHandler.updateFrame();
	}
}