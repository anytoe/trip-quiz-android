package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;


public class ActionLoginUser extends AsyncAction {

	public ActionLoginUser(IActionListener actionListener, ConvertJSON converter, String identifier, String password) {
		super(AsyncActionType.USER, AsyncActionCategory.READ_USER, actionListener, converter, null);
		this.actionListener = actionListener;
		urlBuilder.addUrlPath("user/login/");
		urlBuilder.addParameter("identifier", identifier, true);
		urlBuilder.addParameter("password", password, true);
	}

	@Override
	public void action() {
		AsyncActionTask webRequestTask = new AsyncActionTask(this, actionListener);
		webRequestTask.execute();
	}

	@Override
	public EntityKey getEntityKey() {
		return null;
	}

	@Override
	public boolean hasEntityKey() {
		return false;
	}
}
