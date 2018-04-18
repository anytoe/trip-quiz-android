package com.tripquiz.generic;

import java.io.Serializable;

public class Coordinates implements Serializable {

	private static final long serialVersionUID = 110255320270153098L;
	private double longitude = -1;
	private double latitude = -1;
	private double accuracy = -1;
	private long timestamp = 0;

	public Coordinates(double latitude, double longitude, double accuracy, long timestamp) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}

	public Coordinates() {
		super();
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public boolean hasCoordinates() {
		return longitude != -1 && longitude != -1;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public boolean hasAccuracy() {
		return accuracy > 0;
	}

	public long getTimeStamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(accuracy);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		Coordinates other = (Coordinates) obj;
		if (Double.doubleToLongBits(accuracy) != Double.doubleToLongBits(other.accuracy))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return longitude + "/" + latitude + "-" + accuracy;
	}

}
