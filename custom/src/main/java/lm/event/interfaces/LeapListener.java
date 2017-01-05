package lm.event.interfaces;

import lm.event.LeapEvent;

public interface LeapListener {
	public void update(LeapEvent event);

	public void statusChanged(LeapEvent event);
}