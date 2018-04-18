package com.tripquiz.controller.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tripquiz.R;

public class SuccessDialog extends DialogFragment {

	private String text;

	public static SuccessDialog newInstance(String text) {
		SuccessDialog successDialog = new SuccessDialog();
		successDialog.setText(text);
		return successDialog;
	}

	private void setText(String text) {
		this.text = text;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void initUI() {
//		final ViewGroup container = (ViewGroup) getActivity().findViewById(R.id.successdialog_container);
//		container.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
		final TextView text = (TextView) getActivity().findViewById(R.id.successdialog_text);
		text.setText(this.text);
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
		return inflater.inflate(R.layout.dialog_success, container, false);
	}

}
