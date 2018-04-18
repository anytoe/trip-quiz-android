package com.tripquiz.positionservice;

public class PositionListenerAdapter implements IPositionListener {

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		// nothing to do
	}

	@Override
	public void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService) {
		// nothing to do
	}

	@Override
	public void positionAccuracyUpdate(int currentProgress, int maxProgress) {
		// nothing to do
	}

}
