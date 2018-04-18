package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;


public class ActionGetUserChallenges extends AsyncAction {

	public ActionGetUserChallenges(String token, IActionListener actionListener, ConvertJSON converter) {
		super(AsyncActionType.ChallengesUser, AsyncActionCategory.READ_CHALLENGES, actionListener, converter);
		urlBuilder.addUrlPath("api/challenges/user");
		urlBuilder.addParameter("token", token);		
	}

	@Override
	public void action() {
		AsyncActionTask webRequestTask = new AsyncActionTask(this, actionListener);
		webRequestTask.execute();
	}

}
