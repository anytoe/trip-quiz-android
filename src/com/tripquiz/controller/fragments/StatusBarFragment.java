package com.tripquiz.controller.fragments;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.commands.CheckinCommand;
import com.tripquiz.controller.customview.TaskButtonAdapter;
import com.tripquiz.positionservice.IPositionService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webgeneric.Event;

public class StatusBarFragment extends AFragment {

	private TaskButtonAdapter checkInController;
	private View fragmentContainer;
	private TextView textLeft;
	private TextView textRight;
	private ViewGroup iconContainerRight;
	private ActionListenerAdapter actionListener;
	private ViewGroup containerRight;

	private String[] locationsInRange;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentContainer = inflater.inflate(R.layout.fragment_statusbar, null);
		textLeft = (TextView) fragmentContainer.findViewById(R.id.statusbar_textLeft);
		textRight = (TextView) fragmentContainer.findViewById(R.id.statusbar_textRight);
		containerRight = (ViewGroup) fragmentContainer.findViewById(R.id.statusbar_container_right);
		iconContainerRight = (ViewGroup) fragmentContainer.findViewById(R.id.statusbar_iconRight);
		checkInController = new TaskButtonAdapter(activity, iconContainerRight, R.drawable.taskimage_pin, -1, R.color.red_logo, true);
		return fragmentContainer;
	}

	@Override
	protected void onResumeOnFinish() {
		super.onResumeOnFinish();
		actionListener = new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStarted(AsyncAction action) {
				if (action.getAppActionType() == AsyncActionType.CHECKIN) {
					final WebserviceModel model = challengeManager.getWebserviceModel();
					setLeftText();
					checkInController.showLoading();
					textRight.setText(model.getChallengeSet().getLocation(action.getEntityKey()).getName());
				}
			}

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.getAppActionType() == AsyncActionType.CHECKIN) {
					setLeftText();
					checkInController.hide();
				} else if (AsyncActionType.ChallengesNew == action.getAppActionType() || AsyncActionType.ChallengesUser == action.getAppActionType()) {
					positionUpdate(true, positionService);
				}
			}

			@Override
			public void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory) {
				if (actionType == AsyncActionType.CHECKIN) {
					setLeftText();
					setCheckinButton(locationsInRange);
				}
			}

			@Override
			public void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory) {
				setLeftText();
				setCheckinButton(locationsInRange);
			}
		};
		challengeManager.getActionFactory().addListenerAction(AsyncActionType.CHECKIN, actionListener);
		challengeManager.getActionFactory().addListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
		setVisibility();
  
		if (challengeManager.getWebserviceModel().isInitialised())
			setLeftText();
	}

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		super.positionUpdate(enabled, positionService);
	}

	@Override
	public void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService) {
		this.locationsInRange = locationsInRange;
		super.locationInRangeUpdate(locationsInRange, positionService);
		setCheckinButton(locationsInRange);
		setVisibility();
	}

	private void setCheckinButton(String[] locationsInRange) {
		final WebserviceModel appState = challengeManager.getWebserviceModel();
		if (appState.isInitialised() && !appState.isExecuting(AsyncActionType.CHECKIN)) {
			if (locationsInRange.length > 0) {

				if (locationsInRange.length == 1) {
					// just one location, show and checkin straight away
					final EntityKey closestLocKey = new EntityKey(locationsInRange[0], LocationDt.class);
					LocationDt closestLoc = appState.getChallengeSet().getLocation(closestLocKey);
					textRight.setText(closestLoc.getName());
					containerRight.setOnClickListener(new CheckinCommand(closestLoc, challengeManager));
					containerRight.setClickable(true);
				} else if (locationsInRange.length > 1) {
					// several locations, show a list to pick a location
					textRight.setText(activity.getString(R.string.statusbar_severalplaces));
					final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setTitle(activity.getString(R.string.statusbar_chooseplace));
					final Map<String, String> locationKeyValuePair = appState.getChallengeSet().getLocationKeyValuePair(locationsInRange);
					final String[] names = new String[locationKeyValuePair.size()];
					final String[] keys = new String[locationKeyValuePair.size()];
					int counter = 0;
					for (Map.Entry<String, String> entrySet : locationKeyValuePair.entrySet()) {
						names[counter] = entrySet.getValue();
						keys[counter++] = entrySet.getKey();
					}

					builder.setItems(names, new CheckinCommand(keys, challengeManager));
					containerRight.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							builder.show();
						}
					});
					containerRight.setClickable(true);
				}
				checkInController.showButton(true);
			} else {
				checkInController.hide();
				textRight.setText("");
				containerRight.setOnClickListener(null);
				containerRight.setClickable(false);
			}
		}
	}

	private void setVisibility() {
		WebserviceModel appState = challengeManager.getWebserviceModel();
		if (appState.isCheckedIn() || appState.isExecuting(AsyncActionType.CHECKIN) || (locationsInRange != null && locationsInRange.length > 0)){
			fragmentContainer.setVisibility(View.VISIBLE);
		}
		else{
			fragmentContainer.setVisibility(View.GONE);
		}
	}

	private void setLeftText() {
		setVisibility();

		WebserviceModel appState = challengeManager.getWebserviceModel();
		String text = "";
		if (appState.isCheckedIn()) {
			LocationDt checkedInLocation = appState.getCheckedInLocation();
			text = getTimeFormatted() + " " + checkedInLocation.getName();
			textLeft.setCompoundDrawables(null, null, null, null);
		}
		textLeft.setText(text);
	}

	@Override
	public String getHeader() {
		return "Status Bar";
	}

	@Override
	public void onPause() {
		if (challengeManager != null) {
			challengeManager.getActionFactory().removeListenerAction(AsyncActionType.CHECKIN, actionListener);
		}
		super.onPause();
	}

	private String getTimeFormatted() {
		WebserviceModel appState = challengeManager.getWebserviceModel();
		long ms = appState.getCheckedInMillisLeft();
		short minutes = (short) TimeUnit.MILLISECONDS.toMinutes(ms);
		String seconds = "0" + TimeUnit.MILLISECONDS.toSeconds(ms - 1000 * 60 * minutes) + "";
		String minutesString = "0" + minutes + "";
		return minutesString.substring(minutesString.length() - 2, minutesString.length()) + ":" + seconds.substring(seconds.length() - 2, seconds.length());
	}
}
