package com.tripquiz.controller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripquiz.R;

public class TextFragment extends Fragment {

	private View rootView;
	private TextView textView;
	private String text;
	private IHeaderClickListener headerClick;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_text, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		textView = (TextView) rootView.findViewById(R.id.text);
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerClick.notifyOnHeaderClick(null);
			}
		});
		textView.setText(text);
	}

	public void initFragment(String text, IHeaderClickListener headerClick) {
		this.text = text;
		this.headerClick = headerClick;
	}

}
