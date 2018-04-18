package com.tripquiz.positionservice;

public interface IPositionListener {

	void positionUpdate(boolean enabled, IPositionService positionService);

	void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService);

	void positionAccuracyUpdate(int currentProgress, int maxProgress);

}