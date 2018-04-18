package com.tripquiz.controller.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tripquiz.R;

public class ImageToast extends Toast {

	private View toastLayout;
	private TextView toastText;

	public ImageToast(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		// (ViewGroup) context.findViewById(R.id.toast_layout);
		toastLayout = inflater.inflate(R.layout.comp_toastmessage, null);
		toastText = (TextView) toastLayout.findViewById(R.id.toast_text_1);

		super.setView(toastLayout);
	}

	@Override
	public void setText(CharSequence s) {
		toastText.setText(s);
	}

	@Override
	public void setText(int resId) {
		// toastText.setText(Resources.getSystem().getString(resId));
		throw new RuntimeException("Not implemented yet");
	}
}
