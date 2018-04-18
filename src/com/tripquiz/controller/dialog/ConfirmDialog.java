package com.tripquiz.controller.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.tripquiz.R;
import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.webapi.model.Challenge;

public class ConfirmDialog extends DialogFragment implements OnClickListener {

	private TripQuizContext tripQuizContext;
	private String text;
	private Challenge challenge;

	public static ConfirmDialog newInstance(TripQuizContext tripQuizContext, Challenge challenge, String text) {
		ConfirmDialog successDialog = new ConfirmDialog();
		successDialog.init(tripQuizContext, challenge, text);
		return successDialog;
	}

	private void init(TripQuizContext tripQuizContext, Challenge challenge, String text) {
		this.tripQuizContext = tripQuizContext;
		this.challenge = challenge;
		this.text = text;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(tripQuizContext.getContext());
		builder.setMessage(text).setPositiveButton(tripQuizContext.getString(R.string.button_ok), this)
				.setNegativeButton(tripQuizContext.getString(R.string.button_cancel), this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (DialogInterface.BUTTON_POSITIVE == which)
			tripQuizContext.getActionFactory().changeVisibilityOfChallenge(challenge.getKey(), false);
//		tripQuizContext.getUIState().setActionDialogOpened(false);
	}
}
