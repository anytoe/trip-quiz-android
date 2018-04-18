package com.tripquiz.webapi.action;

import com.tripquiz.generic.Coordinates;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;

public class ActionGetNewChallenges extends AsyncAction {

	public ActionGetNewChallenges(String token, AsyncActionType actionType, IActionListener actionListener, ConvertJSON converter, String anytext,
			Coordinates coordinate) {
		super(actionType, AsyncActionCategory.READ_CHALLENGES, actionListener, converter);

		urlBuilder.addUrlPath("api/challenges/new");
		urlBuilder.addParameter("token", token);

		if (anytext != null && !anytext.equals(""))
			urlBuilder.addParameter("anytext", anytext, true);
		else if (coordinate != null && coordinate.hasCoordinates()) {
			urlBuilder.addParameter("lng", coordinate.getLongitude() + "");
			urlBuilder.addParameter("lat", coordinate.getLatitude() + "");
		}
	}

	@Override
	public void action() {
		AsyncActionTask webRequestTask = new AsyncActionTask(this, actionListener);
		webRequestTask.execute();
	}

}
