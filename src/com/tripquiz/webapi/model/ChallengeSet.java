package com.tripquiz.webapi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tripquiz.persistance.SerializableRepo;

public class ChallengeSet implements SerializableRepo {

	private static final long serialVersionUID = -2134846803454501488L;

	private HashMap<String, Map<EntityKey, Challenge>> challengeCollection;
	private HashMap<EntityKey, Challenge> allChallengeMap;

	// private HashMap<Integer, Reward> rewards;

	public ChallengeSet(String categorie, Map<EntityKey, Challenge> challenges) {
		super();
		allChallengeMap = new HashMap<EntityKey, Challenge>();
		challengeCollection = new HashMap<String, Map<EntityKey, Challenge>>();
		challengeCollection.put(categorie, challenges);

		rebuildCache();
	}

	// public void addChallengeSet(ChallengeSet challengeSetToAdd) {
	// for (Map.Entry<String, Map<EntityKey, Challenge>> entrySet : challengeSetToAdd.challengeCollection.entrySet()) {
	// challengeCollection.put(entrySet.getKey(), entrySet.getValue());
	// }
	//
	// rebuildCache();
	// }

	public ChallengeSet() {
		super();
		allChallengeMap = new HashMap<EntityKey, Challenge>();
		challengeCollection = new HashMap<String, Map<EntityKey, Challenge>>();
	}

	public boolean hasChallenges() {
		return allChallengeMap != null && allChallengeMap.size() > 0;
	}

	public void setChallenge(String categorie, Map<EntityKey, Challenge> achments) {
		challengeCollection.put(categorie, achments);
		rebuildCache();
	}

	// public void clearChallenges(){
	// allChallengeMap.clear();
	// }

	public void clearChallenge(EntityKey challengeKey) {
		for (Entry<String, Map<EntityKey, Challenge>> mapSet : challengeCollection.entrySet()) {
			mapSet.getValue().remove(challengeKey);
		}
		allChallengeMap.remove(challengeKey);
	}

	public void setChallenge(Challenge challenge) {
		for (Entry<String, Map<EntityKey, Challenge>> mapSet : challengeCollection.entrySet()) {
			if (mapSet.getValue().containsKey(challenge.getKey()))
				mapSet.getValue().put(challenge.getKey(), challenge);
		}
		if (allChallengeMap.containsKey(challenge.getKey()))
			allChallengeMap.put(challenge.getKey(), challenge);
	}

	public Collection<Challenge> getAllChallenges() {
		return allChallengeMap.values();
	}

	public Collection<LocationDt> getAllLocations() {
		List<LocationDt> locations = new ArrayList<LocationDt>();
		for (Challenge challenge : allChallengeMap.values()) {
			locations.addAll(challenge.getLocations());
		}

		return locations;
	}

	public Collection<LocationDt> getAllLocationsActive() {
		List<LocationDt> locations = new ArrayList<LocationDt>();
		for (Challenge challenge : allChallengeMap.values()) {
			locations.addAll(challenge.getLocationsActive());
		}

		return locations;
	}

	public boolean hasLocation(EntityKey entityKey) {
		if (entityKey == null || entityKey.getKey() == null)
			return false;

		for (Entry<EntityKey, Challenge> challEntry : allChallengeMap.entrySet()) {
			if (challEntry.getValue().hasLocation(entityKey)) {
				return true;
			}
		}

		return false;
	}

	public LocationDt getLocation(EntityKey entityKey) {
		if (entityKey.getKey() == null) {
			throw new RuntimeException("The entity key must not be null");
		}

		for (Entry<EntityKey, Challenge> challEntry : allChallengeMap.entrySet()) {
			if (challEntry.getValue().hasLocation(entityKey)) {
				return challEntry.getValue().getLocation(entityKey);
			}
		}

		throw new RuntimeException("The entity key is not a key for a location");
	}

	public boolean hasChallenge(EntityKey challengeKey) {
		return allChallengeMap.containsKey(challengeKey);
	}

	public Challenge getChallenge(EntityKey challengeKey) {
		if (!allChallengeMap.containsKey(challengeKey)) {
			throw new RuntimeException("There is no challenge for this key " + challengeKey);
		}
		return allChallengeMap.get(challengeKey);
	}

	public void removeChallenge(String category, EntityKey entityKey) {
		if (allChallengeMap.containsKey(entityKey) && challengeCollection.containsKey(category)) {
			Map<EntityKey, Challenge> map = challengeCollection.get(category);
			if (map.containsKey(entityKey)) {
				map.remove(entityKey);
				rebuildCache();
			}
		}
	}

	public Challenge getChallengeByChild(EntityKey childKey) {
		if (allChallengeMap.containsKey(childKey)) {
			// is challenge itself
			return allChallengeMap.get(childKey);
		} else {
			// search all locations of all challenges
			for (Challenge challenge : allChallengeMap.values()) {
				for (LocationDt loc : challenge.getLocations()) {
					if (loc.getKey().equals(childKey)) {
						return challenge;
					} else {
						for (Task task : loc.getTasks()) {
							// search all tasks
							if (task.getKey().equals(childKey)) {
								return challenge;
							}
						}
					}
				}
			}
		}

		throw new RuntimeException("This entity key does not match any challenge, location or task: " + childKey);
	}

	public Challenge getChallengeByLocation(EntityKey locationKey) {
		if (locationKey.getKey() == null)
			throw new RuntimeException("The entity key must not be null");

		for (Entry<EntityKey, Challenge> challEntry : allChallengeMap.entrySet()) {
			if (challEntry.getValue().hasLocation(locationKey)) {
				return challEntry.getValue();
			}
		}

		throw new RuntimeException("The entity key is not a key for a location");
	}

	public Challenge[] getChallengeArrayByOrder() {
		return allChallengeMap.values().toArray(new Challenge[0]);
	}

	public Challenge[] getChallengeArrayByOrder(String category, Comparator<Challenge> comparator) {
		if (!challengeCollection.containsKey(category))
			return new Challenge[0];
		else {
			if (comparator != null) {
				ArrayList<Challenge> sortedList = new ArrayList<Challenge>(challengeCollection.get(category).values());
				Collections.sort(sortedList, comparator);
				return sortedList.toArray(new Challenge[0]);
			} else {
				return challengeCollection.get(category).values().toArray(new Challenge[0]);
			}
		}
	}

	private void rebuildCache() {
		allChallengeMap.clear();
		for (Entry<String, Map<EntityKey, Challenge>> mapSet : challengeCollection.entrySet()) {
			allChallengeMap.putAll(mapSet.getValue());
		}
	}

	public boolean hasChallenges(String category) {
		return challengeCollection.containsKey(category) && challengeCollection.get(category).size() > 0;
	}

	public boolean isEmpty() {
		return allChallengeMap.size() == 0;
	}

	public void setCategory(String categoryName, Collection<Challenge> challenges) {
		HashMap<EntityKey, Challenge> newCategory = new HashMap<EntityKey, Challenge>();
		for (Challenge challengeToAdd : challenges) {
			// add to new map
			newCategory.put(challengeToAdd.getKey(), challengeToAdd);
			// remove from old map
			for (Map.Entry<String, Map<EntityKey, Challenge>> entrySet : challengeCollection.entrySet()) {
				entrySet.getValue().remove(challengeToAdd.getKey());
			}
		}
		challengeCollection.put(categoryName, newCategory);
		rebuildCache();
	}

	public Map<String, String> getLocationKeyValuePair(String[] locationKeys) {
		Set<String> locationKeysSet = new HashSet<String>(Arrays.asList(locationKeys));
		Map<String, String> locKVPair = new HashMap<String, String>();

		// lookup all locations
		for (Challenge challenge : allChallengeMap.values()) {
			for (LocationDt loc : challenge.getLocations()) {
				if (locationKeysSet.contains(loc.getKey().getKey()))
					locKVPair.put(loc.getKey().getKey(), loc.getName());
			}
		}

		return locKVPair;
	}

	@Override
	public void afterLoad() {
		// nothing to do
	}

}
