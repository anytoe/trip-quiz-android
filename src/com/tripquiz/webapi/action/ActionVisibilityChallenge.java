package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;

public class ActionVisibilityChallenge extends AsyncAction {

	public ActionVisibilityChallenge(String token, IActionListener actionListener, ConvertJSON converter, EntityKey challengeKey, boolean isVisible) {
		super(AsyncActionType.CHALLENGE_ADD, AsyncActionCategory.MODIFY_CHALLENGES, actionListener, converter, challengeKey);
		urlBuilder.addUrlPath("api/visibility/" + challengeKey.getKey() + "/");
		urlBuilder.addParameter("token", token);
		urlBuilder.addParameter("isvisible", isVisible + "");
	}

	@Override
	public void action() {
		AsyncActionTask webRequestTask = new AsyncActionTask(this, actionListener);
		webRequestTask.execute();
	}

}
