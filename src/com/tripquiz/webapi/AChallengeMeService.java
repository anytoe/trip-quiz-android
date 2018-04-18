package com.tripquiz.webapi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.Event;

public abstract class AChallengeMeService {

	protected Set<IActionListener> allActionListeners;
	protected Map<AsyncActionType, Set<IActionListener>> actionTypeListeners;
	protected Map<AsyncActionCategory, Set<IActionListener>> actionCategoryListeners;

	public AChallengeMeService() {
		allActionListeners = new HashSet<IActionListener>();
		actionTypeListeners = new HashMap<AsyncActionType, Set<IActionListener>>();
		actionCategoryListeners = new HashMap<AsyncActionCategory, Set<IActionListener>>();
	}

	public void addListenerAllActions(IActionListener actionListener) {
		synchronized (allActionListeners) {
			allActionListeners.add(actionListener);
		}
	}

	public void addListenerAction(AsyncActionType asyncActionType, IActionListener actionListener) {
		synchronized (actionTypeListeners) {
			if (!actionTypeListeners.containsKey(asyncActionType)) {
				HashSet<IActionListener> listenerSet = new HashSet<IActionListener>();
				listenerSet.add(actionListener);
				actionTypeListeners.put(asyncActionType, listenerSet);
			} else {
				Set<IActionListener> listenerSet = actionTypeListeners.get(asyncActionType);
				listenerSet.add(actionListener);
				actionTypeListeners.put(asyncActionType, listenerSet);
			}
		}
	}

	public void addListenerAction(AsyncActionCategory actionCategory, IActionListener actionListener) {
		synchronized (actionCategoryListeners) {
			if (!actionCategoryListeners.containsKey(actionCategory)) {
				HashSet<IActionListener> listenerSet = new HashSet<IActionListener>();
				listenerSet.add(actionListener);
				actionCategoryListeners.put(actionCategory, listenerSet);
			} else {
				Set<IActionListener> listenerSet = actionCategoryListeners.get(actionCategory);
				listenerSet.add(actionListener);
				actionCategoryListeners.put(actionCategory, listenerSet);
			}
		}
	}

	public void removeListenerAction(AsyncActionType asyncActionType, IActionListener actionListener) {
		synchronized (actionTypeListeners) {
			if (actionTypeListeners.containsKey(asyncActionType))
				actionTypeListeners.get(asyncActionType).remove(actionListener);
		}
	}

	public void removeListenerAction(AsyncActionCategory actionCategory, IActionListener actionListener) {
		synchronized (actionCategoryListeners) {
			if (actionTypeListeners.containsKey(actionCategory))
				actionCategoryListeners.get(actionCategory).remove(actionListener);
		}
	}

	public void removeListenerActionAll(IActionListener actionListener) {
		synchronized (allActionListeners) {
			allActionListeners.remove(actionListener);
		}
	}

	public void removeListenerAll() {
		synchronized (allActionListeners) {
			allActionListeners = new HashSet<IActionListener>();
		}
		synchronized (actionTypeListeners) {
			actionTypeListeners = new HashMap<AsyncActionType, Set<IActionListener>>();
		}
		synchronized (actionCategoryListeners) {
			actionCategoryListeners = new HashMap<AsyncActionCategory, Set<IActionListener>>();
		}
	}

	protected void notifyListenersStarted(AsyncAction action) {
		synchronized (allActionListeners) {
			for (IActionListener listener : allActionListeners)
				listener.notifyListenerActionStarted(action);
		}

		synchronized (actionTypeListeners) {
			if (actionTypeListeners.containsKey(action.getAppActionType())) {
				for (IActionListener listener : actionTypeListeners.get(action.getAppActionType()))
					listener.notifyListenerActionStarted(action);
			}
		}

		synchronized (actionCategoryListeners) {
			if (actionCategoryListeners.containsKey(action.getAsyncActionCategory())) {
				for (IActionListener listener : actionCategoryListeners.get(action.getAsyncActionCategory()))
					listener.notifyListenerActionStarted(action);
			}
		}
	}

	protected void notifyListenersStopped(AsyncAction action) {
		synchronized (allActionListeners) {
			for (IActionListener listener : allActionListeners)
				listener.notifyListenerActionStopped(action);
		}

		synchronized (actionTypeListeners) {
			if (actionTypeListeners.containsKey(action.getAppActionType())) {
				for (IActionListener listener : actionTypeListeners.get(action.getAppActionType()))
					listener.notifyListenerActionStopped(action);
			}
		}

		synchronized (actionCategoryListeners) {
			if (actionCategoryListeners.containsKey(action.getAsyncActionCategory())) {
				for (IActionListener listener : actionCategoryListeners.get(action.getAsyncActionCategory()))
					listener.notifyListenerActionStopped(action);
			}
		}
	}

	protected void notifyOnError(AsyncAction action, Exception exception) {
		synchronized (allActionListeners) {
			for (IActionListener listener : allActionListeners)
				listener.notifyOnError(action, exception);
		}

		synchronized (actionTypeListeners) {
			if (actionTypeListeners.containsKey(action.getAppActionType())) {
				for (IActionListener listener : actionTypeListeners.get(action.getAppActionType()))
					listener.notifyOnError(action, exception);
			}
		}

		synchronized (actionCategoryListeners) {
			if (actionCategoryListeners.containsKey(action.getAsyncActionCategory())) {
				for (IActionListener listener : actionCategoryListeners.get(action.getAsyncActionCategory()))
					listener.notifyOnError(action, exception);
			}
		}
	}

	public void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory) {
		synchronized (allActionListeners) {
			for (IActionListener listener : allActionListeners)
				listener.notifyListenerActionUpdate(actionType, actionCategory);
		}

		synchronized (actionTypeListeners) {
			if (actionTypeListeners.containsKey(actionType)) {
				for (IActionListener listener : actionTypeListeners.get(actionType))
					listener.notifyListenerActionUpdate(actionType, actionCategory);
			}
		}

		synchronized (actionCategoryListeners) {
			if (actionCategoryListeners.containsKey(actionCategory)) {
				for (IActionListener listener : actionCategoryListeners.get(actionCategory))
					listener.notifyListenerActionUpdate(actionType, actionCategory);
			}
		}
	}

	public void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory) {
		synchronized (allActionListeners) {
			for (IActionListener listener : allActionListeners)
				listener.notifyListenerActionFinished(event, actionType, actionCategory);
		}

		synchronized (actionTypeListeners) {
			if (actionTypeListeners.containsKey(actionType)) {
				for (IActionListener listener : actionTypeListeners.get(actionType))
					listener.notifyListenerActionFinished(event, actionType, actionCategory);
			}
		}

		synchronized (actionCategoryListeners) {
			if (actionCategoryListeners.containsKey(actionCategory)) {
				for (IActionListener listener : actionCategoryListeners.get(actionCategory))
					listener.notifyListenerActionFinished(event, actionType, actionCategory);
			}
		}
	}

	public abstract void checkin(EntityKey locationKey);

	public abstract void answerQuestion(EntityKey task, String answer);

	public abstract void loadChallenge(EntityKey challengeKey);

	public abstract void loadUserChallenges();

	public abstract void loadNewChallenges(String searchtext);

	public abstract void addChallenge(EntityKey challengeKey, boolean started);

	public abstract void changeVisibilityOfChallenge(EntityKey challengeKey, boolean isVisible);

}