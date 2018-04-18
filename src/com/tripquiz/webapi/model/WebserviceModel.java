package com.tripquiz.webapi.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.tripquiz.persistance.SerializableRepo;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;

public class WebserviceModel implements SerializableRepo {

	private static final long serialVersionUID = -5466284952850196033L;
	public static final String CHALLENGE_ID = "challenge_id";
	public static final String LOCATION_ID = "location_id";
	public static final String FRAGMENT_NAME = "location_name";
	public static final String FRAGMENT_OBSERVER_ID = "fragment_observer_id";

	// challenges
	private ChallengeSet challengeSet;

	// check in state
	private EntityKey checkedInLocation = null;
	private long checkedInTimestamp = 0;

	// running action
	private transient HashSet<EntityKey> runningActionKey = null;
	private transient HashMap<AsyncActionType, Integer> runningActionType = null;

	private boolean userChallengesLoaded = false;

	public transient static final long MAX_LOGGEDIN_TIMESPAN = 1000 * 60 * 60; // 60 minutes

	public WebserviceModel() {
		runningActionKey = new HashSet<EntityKey>();
		runningActionType = new HashMap<AsyncActionType, Integer>();
		challengeSet = new ChallengeSet();
	}

	public boolean isInitialised() {
		return userChallengesLoaded; // && newChallengesLoaded;
	}

	public boolean isInitialising() {
		return runningActionType.containsKey(AsyncActionType.ChallengesUser) && !userChallengesLoaded;
	}

	// @Override
	public void notifyListenerActionStarted(AsyncAction action) {
		// unique entity key
		if (action.hasEntityKey())
			runningActionKey.add(action.getEntityKey());

		// increment count
		int counter = 0;
		if (runningActionType.containsKey(action.getAppActionType())) {
			counter = runningActionType.get(action.getAppActionType()) + 1;
		} else {
			counter = 1;
		}
		runningActionType.put(action.getAppActionType(), counter);
	}

	// @Override
	@SuppressWarnings("unchecked")
	public void notifyListenerActionStopped(AsyncAction action) {

		if (action.getAppActionType() == AsyncActionType.CHECKIN) {
			checkedInLocation = action.getEntityKey();
			checkedInTimestamp = (new Date()).getTime();
		}

		if (action.getAsyncActionCategory() == AsyncActionCategory.READ_CHALLENGES && action.isSuccess()) {
			Collection<Challenge> challenges = (Collection<Challenge>) action.getConversionResult();
			challengeSet.setCategory(action.getAppActionType().toString(), challenges);
			if (!challengeSet.hasLocation(checkedInLocation))
				checkout();
		}

		if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE && action.isSuccess()) {
			challengeSet.setChallenge((Challenge) action.getConversionResult());
		}

		// remove from storage
		if (action.hasEntityKey())
			runningActionKey.remove(action.getEntityKey());
		int counter = 0;
		if (runningActionType.containsKey(action.getAppActionType())) {
			counter = runningActionType.get(action.getAppActionType()) - 1;
		}
		runningActionType.put(action.getAppActionType(), counter);

		// if (action.getAppActionType() == AsyncActionType.ChallengesNew && action.isSuccess())
		// newChallengesLoaded = true;
		if (action.getAppActionType() == AsyncActionType.ChallengesUser && action.isSuccess())
			userChallengesLoaded = true;
	}

	public ChallengeSet getChallengeSet() {
		// if (!isInitialised())
		// throw new RuntimeException("Challenge model has not been initialised yet");
		return challengeSet;
	}

	public LocationDt getCheckedInLocation() {
		// if (checkedInLocation == null)
		// throw new RuntimeException("Checkedin location may never be null if called get");
		return challengeSet.getLocation(checkedInLocation);
	}

	public boolean isCheckedIn(EntityKey locationKey) {
		return isCheckedIn() && checkedInLocation.equals(locationKey);
	}

	public boolean isCheckedIn() {
		return checkedInLocation != null && ((new Date()).getTime() - checkedInTimestamp < MAX_LOGGEDIN_TIMESPAN);
	}

	public void checkout() {
		checkedInLocation = null;
		checkedInTimestamp = 0;
	}

	public long getCheckedInMillisLeft() {
		if (checkedInLocation == null)
			throw new RuntimeException("Checkedin location may never be null if called");
		return MAX_LOGGEDIN_TIMESPAN + checkedInTimestamp - (new Date()).getTime();
	}

	public boolean isExecuting(AsyncActionType asyncActionType) {
		return runningActionType.containsKey(asyncActionType) && runningActionType.get(asyncActionType) > 0;
	}

	public boolean isExecuting(EntityKey entityKey) {
		return runningActionKey.contains(entityKey);
	}

	@Override
	public void afterLoad() {
		if (challengeSet == null)
			challengeSet = new ChallengeSet();

		if (runningActionKey == null)
			runningActionKey = new HashSet<EntityKey>();

		if (runningActionType == null)
			runningActionType = new HashMap<AsyncActionType, Integer>();

	}

}
