package com.tripquiz.webapi.action;

import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.AsyncActionTask;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.ConvertJSON;


public class ActionAskQuestion extends AsyncAction {

	private String answer;

	public ActionAskQuestion(String token, IActionListener actionListener, ConvertJSON converter, EntityKey taskKey, String answer) {
		super(AsyncActionType.QUESTION, AsyncActionCategory.MODIFY_CHALLENGE, actionListener, converter, taskKey);
		this.answer = answer;
		urlBuilder.addUrlPath("api/action/" + super.getEntityKey().getKey() + "/");
		urlBuilder.addParameter("token", token);
		urlBuilder.addParameter("answer", answer, true);
	}

	@Override
	public void action() {
		if (answer == null || answer.trim().equals(""))
			throw new RuntimeException("answer must not be null");
		AsyncActionTask webRequestTask = new AsyncActionTask(this, actionListener);
		webRequestTask.execute();
	}

}
