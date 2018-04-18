package com.tripquiz.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.TaskAdapter;
import com.tripquiz.controller.adapter.TaskAdapter.TaskListItem;
import com.tripquiz.controller.commands.CheckinCommand;
import com.tripquiz.controller.commands.QuestionCommand;
import com.tripquiz.controller.commands.ScanCommand;
import com.tripquiz.positionservice.IPositionService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.Challenge.TaskType;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.model.Task;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webgeneric.Event;
import com.tripquiz.webgeneric.ImageDownloader;

public class TaskFragment extends AFragment {

	private EntityKey locationKey;
	private LocationDt location;

	// UI Container
	private ViewGroup fragmentContainer;
	private TextView distanceTextView;
	private ListView taskList;
	private TaskAdapter adapter;
	private ActionListenerAdapter actionListener;
	private String[] locationsInRange = new String[0];
	private ViewGroup checkinOnlyContainer;
	private TextView textcheckinOnly;
	private ViewGroup checkinonlyImageoverlay;
	private View progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentContainer = (ViewGroup) inflater.inflate(R.layout.fragment_task, null);
		fragmentContainer.setVisibility(View.INVISIBLE);

		taskList = (ListView) fragmentContainer.findViewById(R.id.detail_tasklist);
		checkinOnlyContainer = (ViewGroup) fragmentContainer.findViewById(R.id.detail_checkinonly_container);
		textcheckinOnly = (TextView) checkinOnlyContainer.findViewById(R.id.detail_checkinonly_text);
		progressBar = fragmentContainer.findViewById(R.id.detail_checkinonly_progressbar);
		checkinonlyImageoverlay = (ViewGroup) fragmentContainer.findViewById(R.id.detail_checkinonly_imageoverlay);

		return fragmentContainer;
	}

	@Override
	protected void onResumeOnFinish() {
		// read arguments
		this.locationKey = new EntityKey(getArguments().getString(WebserviceModel.LOCATION_ID), LocationDt.class);
		this.location = challengeManager.getWebserviceModel().getChallengeSet().getLocation(locationKey);

		super.onResumeOnFinish();
		actionListener = new ActionListenerAdapter() {

			@Override
			public void notifyListenerActionStarted(AsyncAction action) {
				if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE) {
					if (isFragmentInitialised())
						refreshLocation();
				}
			}

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.getAsyncActionCategory() == AsyncActionCategory.MODIFY_CHALLENGE) {
					if (isFragmentInitialised())
						refreshLocation();
				}
			}

			@Override
			public void notifyListenerActionFinished(Event event, AsyncActionType actionType, AsyncActionCategory actionCategory) {
				if (actionType == AsyncActionType.CHECKIN)
					refreshLocation();
			}
		};
		challengeManager.getActionFactory().addListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
		refreshLocation();
		positionUpdate(positionService != null, positionService);

		// load image
		if (location.hasImage()) {
			ImageView imageView = (ImageView) fragmentContainer.findViewById(R.id.detail_image);
			ImageDownloader imageDownloader = challengeManager.getImageDownloader();
			imageDownloader.init(location.getImageUrl().getUrlGoldenRatioByWidth(activity.getResources().getInteger(R.integer.imagewidth_pixel_large)),
					imageView, null);
			imageDownloader.execute();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		super.positionUpdate(enabled, positionService);
		if (enabled && !location.isFinished()) {
			fragmentContainer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService) {
		this.locationsInRange = locationsInRange;
		refreshLocation();
		super.locationInRangeUpdate(locationsInRange, positionService);
	}

	public void refreshLocation() {
		this.location = challengeManager.getWebserviceModel().getChallengeSet().getLocation(locationKey);
		WebserviceModel appState = challengeManager.getWebserviceModel();
		fragmentContainer.setVisibility(View.VISIBLE);

		if (challengeManager.getWebserviceModel().isCheckedIn(location.getKey()) || location.isFinished()) {
			taskList.setVisibility(View.VISIBLE);
			checkinOnlyContainer.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			adapter = new TaskAdapter(activity, appState, getTaskListItems(location, locationsInRange));
			taskList.setAdapter(adapter);
			taskList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					adapter.getItem(position).click(view);
				}

			});

			if (distanceTextView != null && location.isFinished()) {
				distanceTextView.setText(activity.getString(R.string.details_done));
				disablePositionService();
			}
		} else if (challengeManager.getWebserviceModel().isExecuting(locationKey)) {
			taskList.setVisibility(View.GONE);
			checkinOnlyContainer.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			taskList.setVisibility(View.GONE);
			checkinOnlyContainer.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			if (isLocationInRange(locationsInRange)) {
				textcheckinOnly.setText(activity.getString(R.string.task_checkin) + " " + location.getName());
				checkinonlyImageoverlay.setVisibility(View.INVISIBLE);
				checkinOnlyContainer.setEnabled(true);
				checkinOnlyContainer.setOnClickListener(new CheckinCommand(location, challengeManager));
			} else {
				textcheckinOnly.setText(activity.getString(R.string.details_notcheckedin));
				checkinonlyImageoverlay.setVisibility(View.VISIBLE);
				checkinOnlyContainer.setEnabled(false);
				checkinOnlyContainer.setOnClickListener(null);
			}
		}
	}

	private boolean isLocationInRange(String[] locationsInRange) {
		for (String key : locationsInRange) {
			if (location.getKey().getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}

	private TaskListItem[] getTaskListItems(LocationDt location, String[] locationsInRange) {
		short tasks = 1;
		if (location.hasCheckedInBefore())
			tasks += location.countTasks();

		TaskListItem[] items = new TaskListItem[tasks];
		{
			String locationTaskText = activity.getString(R.string.task_checkin) + " " + location.getName();
			boolean isExecuting = challengeManager.getWebserviceModel().isExecuting(location.getKey());
			OnClickListener action = new CheckinCommand(location, challengeManager);
			boolean isInRange = isLocationInRange(locationsInRange);
			items[0] = new TaskListItem(locationTaskText, location.isFinished(), isExecuting, isInRange && !location.isFinished(), R.drawable.taskimage_pin,
					action);
		}

		if (location.hasCheckedInBefore()) {
			boolean active = challengeManager.getWebserviceModel().isCheckedIn(location.getKey());
			int counter = 1;
			for (Task task : location.getTasks()) {
				// int tasksLeft = task.getQuantityMax() - task.getQuantityCount();
				boolean isExecuting = challengeManager.getWebserviceModel().isExecuting(task.getKey());
				OnClickListener action;
				int iconId;
				if (task.getTaskType() == TaskType.QUESTION) {
					action = new QuestionCommand(task, challengeManager, activity.getFragmentManager());
					iconId = R.drawable.taskimage_qmark;
				} else if (task.getTaskType() == TaskType.SCAN) {
					action = new ScanCommand(task, challengeManager, activity);
					iconId = R.drawable.taskimage_qrcode;
				} else {
					throw new RuntimeException("Unknown Type, cannot pick an action");
				}
				items[counter++] = new TaskListItem(task.getDescription(), task.isSolved(), isExecuting, active, iconId, action);
			}
		}
		return items;
	}

	@Override
	public void onPause() {
		if (challengeManager != null)
			challengeManager.getActionFactory().removeListenerAction(AsyncActionCategory.MODIFY_CHALLENGE, actionListener);
		super.onPause();
	}

	@Override
	public String getHeader() {
		return getArguments().getString(WebserviceModel.FRAGMENT_NAME);
	}

}
