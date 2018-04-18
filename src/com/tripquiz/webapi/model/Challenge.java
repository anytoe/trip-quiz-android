package com.tripquiz.webapi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Color;

public class Challenge implements Serializable {

	private static final long serialVersionUID = 2052023473108908348L;

	private EntityKey key;
	private String name;
	private String description;
	private boolean isRestricted;
	private double percentageFinished;
	private CompetitorProgress competitorProgress;

	private Map<EntityKey, LocationDt> locations;
	private Map<Integer, LocationDt> locationsOrdered;
	private Map<EntityKey, EntityKey> taskByLocation;

	private ImageUrl imageUrl;

	public Challenge(EntityKey key, String name, String description, double percentageFinished, Map<EntityKey, LocationDt> locations,
			CompetitorProgress competitorProgress, boolean isRestricted, ImageUrl imageUrl) {
		super();
		this.key = key;
		this.name = name;
		this.description = description;
		this.percentageFinished = percentageFinished;
		this.locations = locations;
		this.competitorProgress = competitorProgress;
		this.isRestricted = isRestricted;
		this.imageUrl = imageUrl;

		locationsOrdered = new HashMap<Integer, LocationDt>();
		taskByLocation = new HashMap<EntityKey, EntityKey>();

		int counter = 0;
		for (Entry<EntityKey, LocationDt> location : locations.entrySet()) {
			locationsOrdered.put(counter++, location.getValue());
			for (Task task : location.getValue().getTasks()) {
				taskByLocation.put(task.getKey(), location.getKey());
			}
		}
	}

	public double getProgress() {
		return percentageFinished;
	}

	public EntityKey getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public LocationDt getLocation(EntityKey id) {
		if (id == null)
			throw new RuntimeException("id must not be null");
		return locations.get(id);
	}

	public Collection<LocationDt> getLocations() {
		return locations.values();
	}

	public boolean hasLocation(EntityKey entityKey) {
		return locations.containsKey(entityKey);
	}

	public boolean isRestricted() {
		return isRestricted;
	}

	public ImageUrl getImageUrl() {
		return imageUrl;
	}

	public boolean hasImage() {
		return imageUrl != null;
	}

	public LocationDt getFirstActiveLocation() {
		LocationDt fallback = null;
		for (Entry<EntityKey, LocationDt> location : locations.entrySet()) {
			if (!location.getValue().isFinished()) {
				return location.getValue();
			} else {
				// in case there is no active one
				fallback = location.getValue();
			}
		}
		return fallback;
	}

	public Collection<LocationDt> getLocationsActive() {
		Collection<LocationDt> activeLocations = new ArrayList<LocationDt>();
		for (Entry<EntityKey, LocationDt> location : locations.entrySet()) {
			if (!location.getValue().isFinished()) {
				activeLocations.add(location.getValue());
			}
		}
		return activeLocations;
	}

	public Task getTask(EntityKey taskKey) {
		if (taskKey == null)
			throw new RuntimeException("taskKey must not be null");
		return locations.get(taskByLocation.get(taskKey)).getTask(taskKey);
	}

	public Collection<Task> getTasks(EntityKey TaskKey) {
		if (TaskKey == null)
			throw new RuntimeException("TaskKey must not be null");
		return locations.get(TaskKey).getTasks();
	}

	public boolean hasCompetitorProgress() {
		return competitorProgress != null;
	}

	public CompetitorProgress getCompetitorProgress() {
		return competitorProgress;
	}

	public int getColor() {
		String substring = key.getKey().replace("-", "").substring(0, 2);
		return Color.parseColor("#" + substring + substring + substring);
	}

	public enum State {
		OPEN, TOUCHED, INPROGRESS, PENDING, SOLVED
	}

	public enum TaskType {
		QUESTION, SCAN
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((competitorProgress == null) ? 0 : competitorProgress.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + (isRestricted ? 1231 : 1237);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((locations == null) ? 0 : locations.hashCode());
		result = prime * result + ((locationsOrdered == null) ? 0 : locationsOrdered.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(percentageFinished);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((taskByLocation == null) ? 0 : taskByLocation.hashCode());
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
		Challenge other = (Challenge) obj;
		if (competitorProgress == null) {
			if (other.competitorProgress != null)
				return false;
		} else if (!competitorProgress.equals(other.competitorProgress))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (isRestricted != other.isRestricted)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (locations == null) {
			if (other.locations != null)
				return false;
		} else if (!locations.equals(other.locations))
			return false;
		if (locationsOrdered == null) {
			if (other.locationsOrdered != null)
				return false;
		} else if (!locationsOrdered.equals(other.locationsOrdered))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(percentageFinished) != Double.doubleToLongBits(other.percentageFinished))
			return false;
		if (taskByLocation == null) {
			if (other.taskByLocation != null)
				return false;
		} else if (!taskByLocation.equals(other.taskByLocation))
			return false;
		return true;
	}

}
