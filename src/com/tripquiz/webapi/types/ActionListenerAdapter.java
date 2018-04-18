package com.tripquiz.webapi.types;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webgeneric.Event;

public abstract class ActionListenerAdapter implements IActionListener {

	@Override
	public void notifyListenerActionStarted(AsyncAction action) {
		// nothing to do
	}

	@Override
	public void notifyListenerActionStopped(AsyncAction action) {
		// nothing to do
	}

	@Override
	public void notifyOnError(AsyncAction action, Exception exception) {
		// nothing to do
	}

	@Override
	public void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory) {
		// nothing to do
	}

	@Override
	public void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory) {
		// nothing to do
	}

}
