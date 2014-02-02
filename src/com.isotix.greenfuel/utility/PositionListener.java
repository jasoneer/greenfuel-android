package com.isotix.greenfuel.utility;

public interface PositionListener
{
	int STARTED = 0;
	int STOPPED = 1;
	int FIRST_FIX = 2;

	void onPositionFix(double latitude, double longitude);
	void onStatusEvent(int event);
}
