package com.tripquiz.positionservice;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.tripquiz.generic.Observer;
import com.tripquiz.positionservice.LocationService.LocalBinder;
import com.tripquiz.webapi.model.LocationDt;

public class PositionServiceAccess extends Observer<IPositionListener> implements ServiceConnection { // extends BroadcastReceiver

	boolean serviceBound = false;
	private Context context;
	private PositionNotificationManager positionNotificationManager;
	private LocationService locationService;
	private Collection<LocationData> locationsToObserve = null;
	private boolean enableNotification;
	private boolean sendActionBarMessage;
	private boolean vibrate;
	private NotificationConfiguration configuration;
	private PositionManager positionManager;

	public PositionServiceAccess(Context context, NotificationConfiguration configuration) {
		super();
		this.context = context;
		this.configuration = configuration;
	}

	@Override
	public boolean addListener(IPositionListener listener) {
		boolean isAdded = super.addListener(listener);
		if (positionNotificationManager != null)
			positionNotificationManager.addListener(listener);
		return isAdded;
	}

	@Override
	public boolean removeListener(IPositionListener listener) {
		boolean isRemoved = super.removeListener(listener);
		if (positionNotificationManager != null)
			positionNotificationManager.removeListener(listener);
		return isRemoved;
	}

	@Override
	public void removeListenerAll() {
		super.removeListenerAll();
		if (positionNotificationManager != null)
			positionNotificationManager.removeListenerAll();
	}

	public void startBinding() {
		if (!serviceBound) {
			// Bind to LocalService
			Intent intent = new Intent(context, LocationService.class);
			context.bindService(intent, this, Context.BIND_AUTO_CREATE);
		}
	}

	public void stopBinding() {
		if (serviceBound) {
			// Unbind from the service
			context.unbindService(this);
			serviceBound = false;
		}
	}

	public void setProcessRunBackground(boolean runInBackground) {
		if (locationService != null) {
			locationService.setRunInBackground(runInBackground);
		}
	}

	public boolean isProcessRunInBackground() {
		if (locationService != null) {
			return locationService.isBackgroundServiceEnabled();
		}
		return false;
	}

	public void setNotificationSettings(boolean enableNotification, boolean sendActionBarMessage, boolean vibrate) {
		this.enableNotification = enableNotification;
		this.sendActionBarMessage = sendActionBarMessage;
		this.vibrate = vibrate;
		if (positionNotificationManager != null)
			positionNotificationManager.setNotificationSettings(enableNotification, sendActionBarMessage, vibrate);
	}

	public void setLocationforService(Collection<LocationDt> locations) {
		if (locationService != null && positionNotificationManager != null) {
			Collection<LocationData> locData = new ArrayList<LocationData>();
			for (LocationDt loc : locations) {
				LocationData locationData = new LocationData(loc);
				locData.add(locationData);
			}
			if (positionNotificationManager != null) {
				// set straight away
				positionNotificationManager.setLocations(locData);
			}
			// cache
			this.locationsToObserve = locData;
		}
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		// We've bound to LocalService, cast the IBinder and get LocalService instance
		LocalBinder binder = (LocalBinder) service;
		locationService = binder.getService();

		serviceBound = true;
		Intent intent = new Intent(context, LocationService.class);
		context.startService(intent);

		// notify listener, that service is online
		if (positionManager == null) {
			positionNotificationManager = new PositionNotificationManager(locationService, configuration, listeners);
			positionManager = new PositionManager(positionNotificationManager);
		}
		locationService.init(positionManager);
		if (locationsToObserve != null) {
			// set from cache
			positionNotificationManager.setNotificationSettings(enableNotification, sendActionBarMessage, vibrate);
			positionNotificationManager.setLocations(locationsToObserve);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		serviceBound = false;
		positionNotificationManager = null;
		locationService = null;
	}


	public enum PositioningAccuracy {
		GPS, WIFI, BOTH, NONE
	}

}