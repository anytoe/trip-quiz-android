package com.tripquiz.controller.commands;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.webapi.model.Task;

public class ScanCommand  implements OnClickListener {

	private final Task task;
	private final TripQuizContext challengeManager;
	private Activity activity;

	public ScanCommand(Task task, TripQuizContext challengeManager, Activity activity) {
		this.task = task;
		this.challengeManager = challengeManager;
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		// open scanner to scan a code
		if (!challengeManager.getUIState().isActionDialogOpened()) {
			challengeManager.getUIState().setActionDialogOpened(true);
			String intentKey = "com.google.zxing.client.android.SCAN";
			Intent intent = new Intent(intentKey);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intent.putExtra("TASK_ID", task.getKey().toString());
			activity.startActivityForResult(intent, 0);
		} 
	}

}
