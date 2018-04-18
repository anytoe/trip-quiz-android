package com.tripquiz.positionservice;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.tripquiz.R;
import com.tripquiz.controller.LoginActivity;
import com.tripquiz.controller.MainActivity;
import com.tripquiz.generic.Coordinates;
import com.tripquiz.generic.Observer;
import com.tripquiz.persistance.Repository;
import com.tripquiz.webapi.model.WebserviceModel;

public class PositionNotificationManager extends Observer<IPositionListener> implements IPositionService {

	private Repository<Map<String, LocationData>> dataAccess;
	private Repository<Coordinates> coordinatesAccess;
	private boolean enableNotification;

	// information about locations
	private Map<String, LocationData> currentLocations;
	private transient Set<String> inRangeLocations; // reload??

	private boolean hasAccurateDistance = false;

	private Context context;
	private SortedSet<LocationData> sortedLocations;
	private boolean isGpsEnabled;
	private boolean sendActionBarMessage;
	private boolean vibrate;
	private NotificationConfiguration configuration;
	private boolean forceRefresh;

	public PositionNotificationManager(Context context, NotificationConfiguration configuration, List<IPositionListener> listeners) {
		super(listeners);
		this.context = context;
		this.configuration = configuration;
		dataAccess = new Repository<Map<String, LocationData>>(context, "LocationData");
		coordinatesAccess = new Repository<Coordinates>(context, "LastCoordinates");

		currentLocations = dataAccess.loadObject(new HashMap<String, LocationData>());
		inRangeLocations = new HashSet<String>();
	}

	@Override
	public boolean addListener(IPositionListener listener) {
		boolean isAdded = super.addListener(listener);
		listener.positionUpdate(isGpsEnabled, this);
		listener.locationInRangeUpdate(inRangeLocations.toArray(new String[0]), this);
		return isAdded;
	}

	@Override
	public boolean removeListener(IPositionListener listener) {
		boolean isRemoved = super.removeListener(listener);
		return isRemoved;
	}

	public void setNotificationSettings(boolean enableNotification, boolean sendActionBarMessage, boolean vibrate) {
		this.enableNotification = enableNotification;
		this.sendActionBarMessage = sendActionBarMessage;
		this.vibrate = vibrate;
	}

	public void invalidPositionUpdate() {
		boolean hasInRangeChanged = hasAccurateDistance;
		inRangeLocations = new HashSet<String>();
		hasAccurateDistance = false;
		
		for (IPositionListener listener : listeners) {
			listener.positionUpdate(true, this);
			if (hasInRangeChanged) {
				listener.locationInRangeUpdate(inRangeLocations.toArray(new String[0]), this);
			}
		}
	}

	public void setPositionUpdate(Coordinates coordinate) {
		coordinatesAccess.saveObject(coordinate);

		hasAccurateDistance = true;

		// update all location distance and sort them
		Set<String> updatedInRangeLocations = rebuildSortedLocations(coordinate.getLatitude(), coordinate.getLongitude());

		int notificationCounter = 0;
		LocationData locDataToNotify = null;
		for (String key : inRangeLocations) {
			LocationData locData = currentLocations.get(key);
			if (enableNotification && locData.isNotificationDue()) {
				notificationCounter += 1;
				locDataToNotify = locData;
				if (sendActionBarMessage || vibrate)
					locData.setNextNotifyTime(configuration.notificationWaitingTime);
			}
		}

		if (notificationCounter == 1) {
			String title = context.getString(R.string.actionbar_locationnearby_title);
			String text = context.getString(R.string.actionbar_locationnearby_text) + " " + locDataToNotify.getName();
			// notify and set next alarm
			if (sendActionBarMessage)
				sendToNotificationBar(locDataToNotify.getKey(), title, text);
			if (vibrate)
				vibrate();
		} else if (notificationCounter > 1) {
			String title = context.getString(R.string.actionbar_severallocationsnearby_title);
			String text = context.getString(R.string.actionbar_severallocationsnearby_text);
			// notify and set next alarm
			if (sendActionBarMessage)
				sendToNotificationBar(locDataToNotify.getKey(), title, text);
			if (vibrate)
				vibrate();
		}

		boolean hasInRangeChanged = forceRefresh || (isGpsEnabled && !updatedInRangeLocations.equals(inRangeLocations));
		inRangeLocations = updatedInRangeLocations;
		forceRefresh = false;
		// save changes
		dataAccess.saveObject(currentLocations);

		// notify listeners about position update and close locations
		for (IPositionListener listener : listeners) {
			listener.positionUpdate(true, this);
			if (hasInRangeChanged) {
				listener.locationInRangeUpdate(inRangeLocations.toArray(new String[0]), this);
			}
		}
	}

	private Set<String> rebuildSortedLocations(double latitude, double longitude) {
		Comparator<LocationData> locationComparator = new LocationDataComparator();
		sortedLocations = new TreeSet<LocationData>(locationComparator);
		Set<String> updatedInRangeLocations = new HashSet<String>();
		for (Entry<String, LocationData> locEntry : currentLocations.entrySet()) {
			// sort the locations
			locEntry.getValue().setPosition(latitude, longitude);
			sortedLocations.add(locEntry.getValue());

			// determine close locations
			if (locEntry.getValue().hasDistance() && locEntry.getValue().getDistance() <= configuration.locationIsCloseRange) {
				updatedInRangeLocations.add(locEntry.getValue().getKey());
			}
		}

		return updatedInRangeLocations;

	}

	public void setServiceUpdate(boolean isGpsEnabled, boolean wifiEnabled) {
		if (this.isGpsEnabled != isGpsEnabled) {
			this.isGpsEnabled = isGpsEnabled;
			if (!isGpsEnabled) {
				inRangeLocations.clear();
			}
			for (IPositionListener listener : listeners) {
				listener.positionUpdate(isGpsEnabled, this);
				if (!isGpsEnabled) {
					listener.locationInRangeUpdate(inRangeLocations.toArray(new String[0]), this);
				}
			}
		}
	}

	public void setPositionAccuracyUpdate(int currentProgress, int maxProgress) {
		for (IPositionListener listener : listeners) {
			listener.positionAccuracyUpdate(currentProgress, maxProgress);
		}
	}

	//
	// Interface implementation
	//
	@Override
	public boolean hasAccurateDistance() {
		return hasAccurateDistance && isGpsEnabled;
	}

	@Override
	public boolean hasDistanceTo(String key) {
		return hasAccurateDistance() && currentLocations.containsKey(key) && currentLocations.get(key).hasDistance();
	}

	@Override
	public float getDistanceTo(String key) {
		return currentLocations.get(key).getDistance();
	}

	@Override
	public boolean hasLocationInRange(String key) {
		return inRangeLocations.contains(key);
	}

	@Override
	public boolean hasLocations() {
		return currentLocations.size() > 0;
	}

	@Override
	public String getClosestLocation() {
		return sortedLocations.first().getKey();
	}

	@Override
	public String[] getAllLocationsInRange() {
		return inRangeLocations.toArray(new String[0]);
	}

	@Override
	public int countLocationsInRange() {
		return inRangeLocations.size();
	}

	public void setLocations(Collection<LocationData> locationsToObserve) {
		Map<String, LocationData> mergedLocations = new HashMap<String, LocationData>();

		// take all new ones
		for (LocationData loc : locationsToObserve) {
			if (currentLocations.containsKey(loc.getKey())) {
				LocationData existingLocData = currentLocations.get(loc.getKey());
				mergedLocations.put(existingLocData.getKey(), existingLocData);
			} else {
				mergedLocations.put(loc.getKey(), loc);
			}
		}

		this.currentLocations = mergedLocations;
		dataAccess.saveObject(this.currentLocations);

		if (sortedLocations != null)
			sortedLocations.clear();
		if (inRangeLocations != null)
			inRangeLocations.clear();

		forceRefresh = true;
	}

	@Override
	public boolean hasLocationInRange() {
		// TODO consider checkin locations
		return inRangeLocations.size() > 0;
	}

	@Override
	public boolean hasLastCoordinates() {
		return coordinatesAccess.hasObject();
	}

	@Override
	public Coordinates getLastCoordinates() {
		return coordinatesAccess.loadObject();
	}

	private void sendToNotificationBar(String locationID, String title, String text) {
		if (context != null) {
			// create the statusbar notification
			Intent nIntent = new Intent(context, MainActivity.class);
			nIntent.setClass(context, LoginActivity.class);
			nIntent.setAction("actionstring" + System.currentTimeMillis());
			nIntent.putExtra(WebserviceModel.LOCATION_ID, locationID);
			nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			PendingIntent pIntent = PendingIntent.getActivity(context, 0, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			int mNotificationId = Integer.MAX_VALUE;

			Notification.Builder builder = new Notification.Builder(context);
			builder.setAutoCancel(true).setSmallIcon(R.drawable.navigationbar_bell_enabled).setContentTitle(title).setContentText(text)
					.setContentIntent(pIntent);

			NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotifyMgr.notify(mNotificationId, builder.build());

		}
	}

	// private boolean isNotificationVisible(int notificationId) {
	// Intent notificationIntent = new Intent(context, MainActivity.class);
	// PendingIntent test = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_NO_CREATE);
	// return test != null;
	// }

	private void vibrate() {
		// notify
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(1000);
	}

	@Override
	public boolean isGpsEnabled() {
		return isGpsEnabled;
	}

}
