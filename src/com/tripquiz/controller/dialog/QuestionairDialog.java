package com.tripquiz.controller.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.webapi.model.Task;
import com.tripquiz.webapi.model.Task.QuestionType;

public class QuestionairDialog extends DialogFragment {

	public static final String QUESTION_KEY = "QUESTION_KEY";
	private Task task;
	private TripQuizContext challengeManager;

	public static QuestionairDialog newInstance(Task task, TripQuizContext challengeManager) {
		QuestionairDialog questionairDialog = new QuestionairDialog();
		questionairDialog.setTask(task, challengeManager);
		return questionairDialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void setTask(Task task, TripQuizContext challengeManager) {
		this.task = task;
		this.challengeManager = challengeManager;
	}

	private String getHintText() {
		switch (task.getSolutionType()) {
		case Date:
			return challengeManager.getString(R.string.answerhints_date).replace("{0}", task.getSolutionHint())  ;
		case DateDayMonth:
			return challengeManager.getString(R.string.answerhints_datedaymonth).replace("{0}", task.getSolutionHint())  ;
		case DateMonthYear:
			return challengeManager.getString(R.string.answerhints_datemonthyear).replace("{0}", task.getSolutionHint())  ;
		case Natural:
			return challengeManager.getString(R.string.answerhints_natural);
		case Real:
			return challengeManager.getString(R.string.answerhints_real);
		case Text:
			int wordCount = Integer.parseInt(task.getSolutionHint());
			String hint = challengeManager.getString(R.string.answerhints_text1);
			if (wordCount == 2)
				hint = challengeManager.getString(R.string.answerhints_text2);
			else if (wordCount == 3)
				hint = challengeManager.getString(R.string.answerhints_text3);
			else if (wordCount == 4)
				hint = challengeManager.getString(R.string.answerhints_text4);
			return hint;
		default:
			throw new RuntimeException("The enum " + QuestionType.class.toString() + " has been extended or no type has been found. Please adjust this method or disallow more than 4 words");
		}
	}

	private void initUI() {
		final TextView questionText = (TextView) getActivity().findViewById(R.id.dialog_questionair_questiontext);
		questionText.setText(task.getDescription());
		final TextView hintText = (TextView) getActivity().findViewById(R.id.dialog_questionair_questionhint);
		hintText.setText(getHintText());
		final EditText answerText = (EditText) getActivity().findViewById(R.id.dialog_questionair_answertext);

		final Button cancelButton = (Button) getActivity().findViewById(R.id.dialog_questionary_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				challengeManager.getUIState().setActionDialogOpened(false);
			}
		});

		final Button okButton = (Button) getActivity().findViewById(R.id.dialog_questionary_ok);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (answerText.getText() != null && !answerText.getText().toString().equals("")) {
					dismiss();
					challengeManager.getActionFactory().answerQuestion(task.getKey(), answerText.getText().toString());
					InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					challengeManager.getUIState().setActionDialogOpened(false);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		initUI();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_questionair, container, false);
	}

}
