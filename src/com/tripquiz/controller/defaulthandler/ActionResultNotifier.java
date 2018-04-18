package com.tripquiz.controller.defaulthandler;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.Toast;

import com.tripquiz.R;
import com.tripquiz.controller.UIState;
import com.tripquiz.controller.customview.ImageToast;
import com.tripquiz.controller.dialog.SuccessDialog;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.IActionListener;
import com.tripquiz.webgeneric.Event;
import com.tripquiz.webgeneric.RemoteError;

public class ActionResultNotifier implements IActionListener {

	private Activity activity;
	private WebserviceModel model;
	private UIState uiState;

	public ActionResultNotifier(Activity baseContext, UIState uiState, WebserviceModel model) {
		super();
		this.activity = baseContext;
		this.uiState = uiState;
		this.model = model;
	}

	@Override
	public void notifyListenerActionStarted(AsyncAction action) {
		// if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE)
		// if (!uiState.isAppMinimized())
		// activity.runOnUiThread(new Runnable() {
		// public void run() {
		// ImageToast imageToast = new ImageToast(activity.getBaseContext());
		// imageToast.setText(activity.getText(R.string.toastmessages_actionstarted));
		// imageToast.setDuration(Toast.LENGTH_SHORT);
		// imageToast.show();
		// }
		// });
	}

	// TODO runOnUI Thread can be removed as the callback is not firing from the onProgress anymore

	@Override
	public void notifyListenerActionStopped(final AsyncAction action) {
		if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE) {
			if (action.getEvent() == Event.Complete) {
				// activity.runOnUiThread(new Runnable() {
				// public void run() {
				Challenge finishedChallenge = model.getChallengeSet().getChallengeByChild(action.getEntityKey());
				String text;
				if (finishedChallenge.hasCompetitorProgress()) {
					if (finishedChallenge.getCompetitorProgress().isUserFirst())
						text = activity.getString(R.string.challenges_user_youwon_dialog).replace("{0}", "'" + finishedChallenge.getName() + "'");
					else
						text = activity.getString(R.string.challenges_user_otherwon_dialog)
								.replace("{0}", "'" + finishedChallenge.getCompetitorProgress().getLeaderUsername() + "'")
								.replace("{1}", finishedChallenge.getCompetitorProgress().getUserRanking() + "");
				} else
					text = activity.getString(R.string.challenges_user_single_dialog).replace("{0}", finishedChallenge.getName());

				uiState.setActionDialogOpened(true);
				SuccessDialog questionair = SuccessDialog.newInstance(text);
				FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction.add(android.R.id.content, questionair).addToBackStack(null);
				transaction.commit();
				// }
				// });
			} else {
				String toastMessage = null;
				if (action.getEvent() == Event.Progress)
					toastMessage = activity.getString(R.string.toastmessages_actionprogress);
				else if (action.getEvent() == Event.PartialComplete)
					toastMessage = activity.getString(R.string.toastmessages_locationfinished);
				else if (action.getEvent() == Event.Error)
					toastMessage = activity.getString(R.string.toastmessages_actionfailure);

				if (toastMessage != null) {
					final String displayMessage = toastMessage;
					if (!uiState.isAppMinimized()) {
						// activity.runOnUiThread(new Runnable() {
						// public void run() {
						ImageToast imageToast = new ImageToast(activity.getBaseContext());
						imageToast.setText(displayMessage);
						imageToast.setDuration(Toast.LENGTH_SHORT);
						imageToast.show();
						// }
						// });
					}
				}
			}
		}
	}

	@Override
	public void notifyOnError(final AsyncAction action, final Exception exception) {
		if (!uiState.isAppMinimized()) {
			// activity.runOnUiThread(new Runnable() {
			// public void run() {
			ImageToast imageToast = new ImageToast(activity.getBaseContext());
			imageToast.setDuration(Toast.LENGTH_SHORT);
			if (exception.getMessage() != null && exception.getMessage().equals(RemoteError.NO_INTERNET_CONNECTION)) {
				imageToast.setText(activity.getString(R.string.error_nointernet));
			} else if (exception.getMessage() != null) {
				imageToast.setText(activity.getString(R.string.error_generic) + " " + exception.getMessage());
			} else {
				imageToast.setText(activity.getString(R.string.error_generic));
			}

			imageToast.show();
			// }
			// });
		}
	}

	@Override
	public void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory) {

	}

	@Override
	public void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory) {
		if (actionType == AsyncActionType.CHECKIN) {
			if (!uiState.isAppMinimized() && event != Event.Complete && event != Event.PartialComplete) {
				// activity.runOnUiThread(new Runnable() {
				// public void run() {
				ImageToast imageToast = new ImageToast(activity.getBaseContext());
				imageToast.setText(activity.getString(R.string.task_checkedout));
				imageToast.setDuration(Toast.LENGTH_SHORT);
				imageToast.show();
				// }
				// });
			}
		}
	}

}
