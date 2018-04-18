package com.tripquiz.controller.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tripquiz.R;

public class AlertMessageDialog extends DialogFragment {

	private String titleText, messageText;

	public static AlertMessageDialog newInstance(String titleText, String messageText) {
		AlertMessageDialog alertMessageDialog = new AlertMessageDialog();
		alertMessageDialog.init(titleText, messageText);
		return alertMessageDialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void init(String titleText, String messageText) {
		this.titleText = titleText;
		this.messageText = messageText;
	}

	private void initUI() {
		final TextView title = (TextView) getActivity().findViewById(R.id.dialog_alertmessage_title);
		title.setText(titleText);
		final TextView message = (TextView) getActivity().findViewById(R.id.dialog_alertmessage_message);
		message.setText(messageText);

		final Button okButton = (Button) getActivity().findViewById(R.id.dialog_alertmessage_button);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
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
		return inflater.inflate(R.layout.dialog_alertmessage, container, false);
	}

}
