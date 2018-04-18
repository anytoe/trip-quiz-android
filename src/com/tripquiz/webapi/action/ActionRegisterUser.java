package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;

public class ActionRegisterUser extends AsyncAction {

	public ActionRegisterUser(IActionListener actionListener, ConvertJSON converter, User user) {
		super(AsyncActionType.USER, AsyncActionCategory.READ_USER, actionListener, converter, null);
		this.actionListener = actionListener;
		urlBuilder.addUrlPath("user/register/");
		urlBuilder.addParameter("email", user.getEmail(), true);
		urlBuilder.addParameter("username", user.getUserName(), true);
		urlBuilder.addParameter("password", user.getPassword(), true);
		// TODO implement
		urlBuilder.addParameter("time", "xxx");
		urlBuilder.addParameter("secret", "xxx");
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
