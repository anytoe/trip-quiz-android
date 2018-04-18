package com.tripquiz.positionservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import com.tripquiz.R;
import com.tripquiz.controller.MainActivity;

public class LocationService extends Service {

	public LocationManager locationManager;

	private final IBinder mBinder = new LocalBinder();
	private PositionManager positionManager;
	private boolean isBackgroundServiceEnabled;

	public class LocalBinder extends Binder {
		public LocationService getService() {
			// Return this instance of LocalService so clients can call public methods
			return LocationService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
	}

	private void init() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		positionManager.setGpsEnabled(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
		// listener.setNetworkEnabled(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
		locationManager.removeUpdates(positionManager);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, positionManager);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, positionManager);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationManager != null)
			locationManager.removeUpdates(positionManager);
	}

	public void init(PositionManager positionManager) {
		this.positionManager = positionManager;
		setRunInBackground(isBackgroundServiceEnabled);
		init();
	}

	public boolean isBackgroundServiceEnabled() {
		return isBackgroundServiceEnabled;
	}

	public void setRunInBackground(boolean isBackgroundServiceEnabled) {
		this.isBackgroundServiceEnabled = isBackgroundServiceEnabled;
		if (isBackgroundServiceEnabled) {
			Notification.Builder builder = new Notification.Builder(getBaseContext());
			Intent notificationIntent = new Intent(getBaseContext(), MainActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setSmallIcon(R.drawable.app_logo_small).setContentTitle(getString(R.string.actionbar_servicerunning))
					.setContentText(getString(R.string.actionbar_servicerunning_exlanation)).setContentIntent(pendingIntent);
			startForeground(123, builder.build());
		} else {
			stopForeground(true);
		}
	}

}