package com.tripquiz.positionservice;

import com.tripquiz.generic.Coordinates;

public interface IPositionService {

	boolean isGpsEnabled();

	boolean hasAccurateDistance();

	boolean hasLocationInRange(String key);

	boolean hasLocations();

	String getClosestLocation();

	String[] getAllLocationsInRange();

	boolean hasLocationInRange();

	int countLocationsInRange();

	boolean hasDistanceTo(String key);

	float getDistanceTo(String key);

	boolean hasLastCoordinates();

	Coordinates getLastCoordinates();

}
