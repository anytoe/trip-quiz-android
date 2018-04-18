package com.tripquiz.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.TaskAdapter.TaskListItem;
import com.tripquiz.controller.customview.TaskButtonAdapter;
import com.tripquiz.webapi.model.WebserviceModel;

public class TaskAdapter extends ArrayAdapter<TaskListItem> {

	private final TaskListItem[] tasks;
	private final Context context;

	public TaskAdapter(Context context, final WebserviceModel appState, TaskListItem[] tasks) {
		super(context, R.layout.detail_task, tasks);
		this.context = context;
		this.tasks = tasks;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// get task
		final TaskListItem currentTask = tasks[position];

		// inflate layout
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.detail_task, null);
		}

		final TextView taskText = (TextView) convertView.findViewById(R.id.detail_tasktext);
		taskText.setText(currentTask.description);

		// create Button
		ViewGroup buttonContainer = (ViewGroup) convertView.findViewById(R.id.loadbutton_container);
		final ViewGroup container = (ViewGroup) convertView.findViewById(R.id.detail_task_status);
		if (buttonContainer != null) {
			container.removeView(buttonContainer);
		}
		TaskButtonAdapter taskButton = new TaskButtonAdapter(context, container, currentTask.iconId, R.drawable.task_check, R.color.red_logo, false);

		// set state
		if (currentTask.isSolved) {
			taskButton.showFinished();
		} else if (currentTask.isExecuting) {
			taskButton.showLoading();
		} else {
			taskButton.showButton(currentTask.active);
		}

		convertView.setClickable(!currentTask.active);

		return convertView;
	}

	public static class TaskListItem {

		private String description;
		private boolean isSolved;
		private boolean isExecuting;
		private boolean active;
		private int iconId;
		private OnClickListener action;

		public TaskListItem(String description, boolean isSolved, boolean isExecuting, boolean active, int iconId, OnClickListener action) {
			super();
			this.description = description;
			this.isSolved = isSolved;
			this.isExecuting = isExecuting;
			this.active = active;
			this.iconId = iconId;
			this.action = action;
		}

		public void click(View v) {
			if (active)
				action.onClick(v);
		}
	}
}
