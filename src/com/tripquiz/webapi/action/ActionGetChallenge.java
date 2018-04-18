package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;


public class ActionGetChallenge extends AsyncAction {

	public ActionGetChallenge(String token, IActionListener actionListener, ConvertJSON converter, EntityKey challengeKey) {
		super(AsyncActionType.CHALLENGE_DETAILS, AsyncActionCategory.READ_CHALLENGE, actionListener, converter, challengeKey);
		urlBuilder.addUrlPath("api/" + challengeKey.getKey() + "/");
		urlBuilder.addParameter("token", token);
	}

	@Override
	public void action() {
		AsyncActionTask webRequestTask = new AsyncActionTask(this, actionListener);
		webRequestTask.execute();
	}

}
