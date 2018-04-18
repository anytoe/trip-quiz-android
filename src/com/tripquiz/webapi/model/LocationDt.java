package com.tripquiz.webapi.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.tripquiz.webapi.model.Challenge.State;

public class LocationDt implements Serializable {

	private static final long serialVersionUID = -1806882921886891243L;

	private EntityKey key;
	private String name;
	private double latitude;
	private double longitude;
	private State locationState;
	private Map<EntityKey, Task> tasks;
	private Map<Integer, Task> tasksOrdered;

	private int countTasks;
	private short fullfilledActivities;
	private short possibleActivities;

	private long solvedDate;

	private ImageUrl imageUrl;

	public LocationDt(EntityKey key, String name, double latitude, double longitude, State locationState, long solvedDate, Map<EntityKey, Task> tasks,
			ImageUrl imageUrl) {
		super();
		this.key = key;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationState = locationState;
		this.solvedDate = solvedDate;
		this.tasks = tasks;
		this.imageUrl = imageUrl;

		// Order by current order
		tasksOrdered = new HashMap<Integer, Task>();
		int counter = 0;
		for (Entry<EntityKey, Task> task : tasks.entrySet()) {
			tasksOrdered.put(counter++, task.getValue());
		}
		countTasks = counter;
		calculateProgressTask();
	}

	public EntityKey getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public Task getTask(int position) {
		return tasksOrdered.get(position);
	}

	Task getTask(EntityKey key) {
		return tasks.get(key);
	}

	public Collection<Task> getTasks() {
		return tasks.values();
	}

	public boolean hasTask(EntityKey key) {
		return tasks.containsKey(key);
	}

	public int countTasks() {
		return countTasks;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public boolean hasCheckedInBefore() {
		return locationState == State.SOLVED;
	}

	public double getProgress() {
		double percentage = fullfilledActivities;
		return percentage / possibleActivities;
	}

	public boolean isFinished() {
		return fullfilledActivities == possibleActivities;
	}

	public ImageUrl getImageUrl() {
		return imageUrl;
	}

	public boolean hasImage() {
		return imageUrl != null;
	}

	private void calculateProgressTask() {
		fullfilledActivities = locationState == State.SOLVED ? (short) 1 : (short) 0;
		possibleActivities = 1;
		for (Entry<EntityKey, Task> task : tasks.entrySet()) {
			possibleActivities += task.getValue().getQuantityMax();
			fullfilledActivities += task.getValue().getQuantityCount();
		}
	}

	@Override
	public String toString() {
		return name + " (" + (fullfilledActivities + "/" + possibleActivities) + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + countTasks;
		result = prime * result + fullfilledActivities;
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((locationState == null) ? 0 : locationState.hashCode());
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + possibleActivities;
		result = prime * result + (int) (solvedDate ^ (solvedDate >>> 32));
		result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
		result = prime * result + ((tasksOrdered == null) ? 0 : tasksOrdered.hashCode());
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
		LocationDt other = (LocationDt) obj;
		if (countTasks != other.countTasks)
			return false;
		if (fullfilledActivities != other.fullfilledActivities)
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (locationState != other.locationState)
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (possibleActivities != other.possibleActivities)
			return false;
		if (solvedDate != other.solvedDate)
			return false;
		if (tasks == null) {
			if (other.tasks != null)
				return false;
		} else if (!tasks.equals(other.tasks))
			return false;
		if (tasksOrdered == null) {
			if (other.tasksOrdered != null)
				return false;
		} else if (!tasksOrdered.equals(other.tasksOrdered))
			return false;
		return true;
	}

}
