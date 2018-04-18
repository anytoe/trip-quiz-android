package com.tripquiz.positionservice;

import java.util.ArrayList;

import com.tripquiz.generic.Coordinates;

public class CoordinateHistory {

	private static final double R = 63728000; // In meters

	private ArrayList<Coordinates> coordinatesCurrentLocation;

	private short minSize = 5;
	private short maxSize = 10;
	private int minAccuracy = 50; // meters
	private int minAverageDistance = 5; // meters

	private int maxAllowedDistanceToAverage = 150;

	private Coordinates averageCoordinate;
	private double averageDistance;

	public CoordinateHistory() {
		coordinatesCurrentLocation = new ArrayList<Coordinates>();
	}

	public void addLocation(double lat, double lng, double accuracy, long timestamp) {
		if (accuracy <= minAccuracy) {
			Coordinates coordinates = new Coordinates(lat, lng, accuracy, timestamp);

			// check if the next coordinate is within the allowed distance
			double distance = 0;
			if (averageCoordinate != null) {
				distance = haversine(coordinates, averageCoordinate);
			}

			// remove last coordinate if in range and dont use the current one
			// No recalculation is taking place
			if (distance > maxAllowedDistanceToAverage) {
				if (coordinatesCurrentLocation.size() > 0) {
					coordinatesCurrentLocation.remove(0);
					calculateAverageCoordinate();
				}
			} else {
				if (coordinatesCurrentLocation.size() >= maxSize)
					coordinatesCurrentLocation.remove(0); // remove oldest

				coordinatesCurrentLocation.add(coordinates);
				calculateAverageCoordinate();

				if (coordinatesCurrentLocation.size() >= minSize && averageDistance <= minAverageDistance) {
					// invalid coordinates
					if (coordinatesCurrentLocation.size() > 0)
						coordinatesCurrentLocation.remove(0);
					if (coordinatesCurrentLocation.size() > 0)
						coordinatesCurrentLocation.remove(0);
					calculateAverageCoordinate();
				}
			}
		} else {
			if (coordinatesCurrentLocation.size() > 0)
				coordinatesCurrentLocation.remove(0);
		}
	}

	public boolean hasMinimumVaration() {
		return coordinatesCurrentLocation.size() >= minSize && averageDistance >= minAverageDistance;
	}

	public Coordinates getCoordinates() {
		if (!hasMinimumVaration() || coordinatesCurrentLocation.size() <= 0)
			throw new RuntimeException("No coordinate is being provided, if there's not the given accuracy");

		return averageCoordinate;
	}

	private void calculateAverageCoordinate() {
		double avgLat = 0;
		double avgLng = 0;
		double avgAcc = 0;
		long timestamp = 0;

		for (Coordinates coord : coordinatesCurrentLocation) {
			avgLat += coord.getLatitude();
			avgLng += coord.getLongitude();
			avgAcc += coord.getAccuracy();
			timestamp = coord.getTimeStamp();
		}

		int size = coordinatesCurrentLocation.size();
		avgLat = avgLat / size;
		avgLng = avgLng / size;
		avgAcc = avgAcc / size;

		averageCoordinate = new Coordinates(avgLat, avgLng, avgAcc, timestamp);

		// recalculate the average distance
		averageDistance = 0;
		for (Coordinates coord : coordinatesCurrentLocation) {
			averageDistance += haversine(coord, averageCoordinate);
		}
		averageDistance = averageDistance / size;
	}

	private double haversine(Coordinates coord1, Coordinates coord2) {
		double dLat = Math.toRadians(coord2.getLatitude() - coord1.getLatitude());
		double dLon = Math.toRadians(coord2.getLongitude() - coord1.getLongitude());
		double lat1 = Math.toRadians(coord1.getLatitude());
		double lat2 = Math.toRadians(coord2.getLatitude());

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	public int getCurrentProgress() {
		int size = coordinatesCurrentLocation.size();
		return size < getMaxProgress() ? size : getMaxProgress();
	}

	public int getMaxProgress() {
		return minSize;
	}

}
