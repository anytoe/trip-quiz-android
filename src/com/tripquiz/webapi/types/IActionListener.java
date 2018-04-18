package com.tripquiz.webapi.types;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webgeneric.Event;

public interface IActionListener {

	void notifyListenerActionStarted(AsyncAction action);

	void notifyListenerActionStopped(AsyncAction action);

	void notifyOnError(AsyncAction action, Exception exception);

	void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory);

	void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory);

}
