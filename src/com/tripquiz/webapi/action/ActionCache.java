package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.UrlBuilder;


public class ActionCache extends AsyncAction {

	private Object result;

	public ActionCache(AsyncActionType asyncActionType, AsyncActionCategory actionCategory, IActionListener actionListener, Object result) {
		super(asyncActionType, actionCategory, actionListener, null);
		this.result = result;
	}

	@Override
	public void action() {
		// do nothing
	}

	@Override
	public Object getConversionResult() {
		return result;
	}

	@Override
	public UrlBuilder getUrl() {
		return null;
	}

}
