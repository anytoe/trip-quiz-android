package com.tripquiz.positionservice;

import android.location.Location;

import com.tripquiz.persistance.SerializableRepo;
import com.tripquiz.webapi.model.LocationDt;

public class LocationData implements SerializableRepo {

	private static final long serialVersionUID = 1142055984794104590L;
	private String key;
	private String name;
	private double latitude;
	private double longitude;
	private transient float currentDistance = -1;
	private long nextNotifyTime = -1;

	public LocationData(LocationDt location) {
		key = location.getKey().getKey();
		name = location.getName();
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public boolean hasDistance() {
		return currentDistance != -1;
	}

	public float getDistance() {
		return currentDistance;
	}

	public void setPosition(double latitude, double longitude) {
		float[] result = new float[1];
		Location.distanceBetween(latitude, longitude, this.latitude, this.longitude, result);
		currentDistance = result[0];
	}

	public boolean isNotificationDue() {
		long currentTimeMillis = System.currentTimeMillis();
		boolean isDue = nextNotifyTime == -1 || currentTimeMillis > nextNotifyTime;
		return isDue;
	}

	public void setNextNotifyTime(long nextNotifyTime) {
		this.nextNotifyTime = System.currentTimeMillis() + nextNotifyTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(currentDistance);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (nextNotifyTime ^ (nextNotifyTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationData other = (LocationData) obj;
		if (Float.floatToIntBits(currentDistance) != Float.floatToIntBits(other.currentDistance))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nextNotifyTime != other.nextNotifyTime)
			return false;
		return true;
	}

	@Override
	public void afterLoad() {
		// nothing to do
	}

	@Override
	public String toString() {
		return name + " (" + currentDistance + "m)";
	}

}