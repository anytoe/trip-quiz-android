package com.tripquiz.controller.commands;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.controller.dialog.QuestionairDialog;
import com.tripquiz.webapi.model.Task;

public class QuestionCommand implements OnClickListener {
	
	private final Task task;
	private final TripQuizContext challengeManager;
	private final FragmentManager fragmentManager;

	public QuestionCommand(Task currentTask, TripQuizContext challengeManager, FragmentManager fragmentManager) {
		this.task = currentTask;
		this.challengeManager = challengeManager;
		this.fragmentManager = fragmentManager;
	}

	@Override
	public void onClick(View v) {
		// create dialog to answer question
		if (!challengeManager.getUIState().isActionDialogOpened()) {
			challengeManager.getUIState().setActionDialogOpened(true);
			QuestionairDialog questionair = QuestionairDialog.newInstance(task, challengeManager);
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			transaction.add(android.R.id.content, questionair).addToBackStack(null).commit();
		}
	}
}