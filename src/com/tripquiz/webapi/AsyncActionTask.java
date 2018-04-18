package com.tripquiz.webapi;

import android.os.AsyncTask;

import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.WebClientJson;

public class AsyncActionTask extends AsyncTask<Void, Void, Long> {

	private boolean postBack = false;
	protected IActionListener actionListener;
	private AsyncAction action;

	private Exception exception;

	public AsyncActionTask(AsyncAction action, boolean postBack, IActionListener actionListener) {
		super();
		if (action == null)
			throw new RuntimeException("action listener may not be null");
		if (actionListener == null)
			throw new RuntimeException("there must be an action listener");
		this.action = action;
		this.postBack = postBack;
		this.actionListener = actionListener;
	}

	public AsyncActionTask(AsyncAction action, IActionListener actionListener) {
		this(action, false, actionListener);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		actionListener.notifyListenerActionStarted(action);
	}

	protected Long doInBackground(Void... params) {
		try {
			WebClientJson webClientJson = new WebClientJson();
			String stringFromServer = webClientJson.getStringFromServer(action.getUrl(), postBack);
			action.setResult(stringFromServer);
		} catch (Exception exception) {
			this.exception = exception;
		}
		return 0L;
	}

	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		if (exception == null) {
			actionListener.notifyListenerActionStopped(action);
		} else {
			actionListener.notifyOnError(action, exception);
		}
	}
}