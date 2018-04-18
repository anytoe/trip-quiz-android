package com.tripquiz.positionservice;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class PositionManager implements LocationListener {

	private boolean isGpsEnabled = false;
	public Location previousBestLocation = null;

	public static final String BROADCAST_ACTION = "com.tripquiz.android.POSITION_SERVICE";
	public static String POSITION_INFO = "com.tripquiz.controller.backgroundtask.POSITION_INFO";
	public static String POS_LAT = "Lat";
	public static String POS_LNG = "Lng";
	public static String STATUS_INFO = "com.tripquiz.controller.backgroundtask.STATUS_INFO";
	public static String GPS = "Gps";
	public static String NETWORK = "Network";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private PositionNotificationManager notificationManager;
	private CoordinateHistory coordHistory;

	public PositionManager(PositionNotificationManager notificationManager) {
		this.notificationManager = notificationManager;
		this.coordHistory = new CoordinateHistory();
	}

	@Override
	public void onLocationChanged(final Location loc) {
		if (isGpsEnabled) {

			coordHistory.addLocation(loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), loc.getTime());
			notificationManager.setPositionAccuracyUpdate(coordHistory.getCurrentProgress(), coordHistory.getMaxProgress());
			if (coordHistory.hasMinimumVaration()) {
				notificationManager.setPositionUpdate(coordHistory.getCoordinates());
			} else {
				notificationManager.invalidPositionUpdate();
			}
		}
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	//
	// Location Listener Interface implementation
	//
	public void onProviderDisabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			isGpsEnabled = false;
			notificationManager.setServiceUpdate(isGpsEnabled, false);
		}
	}

	public void onProviderEnabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			isGpsEnabled = true;
			notificationManager.setServiceUpdate(isGpsEnabled, false);
		}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void setGpsEnabled(boolean isGpsEnabled) {
		this.isGpsEnabled = isGpsEnabled;
		notificationManager.setServiceUpdate(isGpsEnabled, false);
	}

}
