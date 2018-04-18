package com.tripquiz.controller.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tripquiz.R;

public class TaskButtonAdapter {

	private ProgressBar bar;
	private ViewGroup linLayout;
	private ImageView image;

	private int taskIconImageId;
	private int successImageId;
	private int activeBackgroundColor;
	private boolean enableAnimation;

	public TaskButtonAdapter(Context context, ViewGroup parentContainer, int taskIconImageId, int successImageId, int activeBackgroundColor, boolean enableAnimation) {

		this.taskIconImageId = taskIconImageId;
		this.successImageId = successImageId;
		this.activeBackgroundColor = activeBackgroundColor;
		this.enableAnimation = enableAnimation;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View compContainer = inflater.inflate(R.layout.comp_taskbutton, parentContainer);

		linLayout = (ViewGroup) compContainer.findViewById(R.id.loadbutton_container);
		image = (ImageView) linLayout.findViewById(R.id.loadbutton_image);
		bar = (ProgressBar) linLayout.findViewById(R.id.loadbutton_progress);

		hide();
	}

	public void hide() {
		bar.setVisibility(View.INVISIBLE);
		image.setVisibility(View.INVISIBLE);
		image.clearAnimation();
		setEnabled(false);
	}

	public void showLoading() {
		bar.setVisibility(View.VISIBLE);
		image.setVisibility(View.INVISIBLE);
		image.clearAnimation();
		setEnabled(false);
	}

	public void showButton(boolean enableBackground) {
		bar.setVisibility(View.INVISIBLE);
		image.setVisibility(View.VISIBLE);
		image.setImageResource(taskIconImageId);
		setEnabled(enableBackground);
	}

	public void showFinished() {
		bar.setVisibility(View.INVISIBLE);
		image.setVisibility(View.VISIBLE);
		image.setImageResource(successImageId);
		image.clearAnimation();
		setEnabled(false);
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			image.setBackgroundResource(activeBackgroundColor);
			if (enableAnimation) {
				final Animation animation = new AlphaAnimation(1, 0.75f); // Change alpha from fully visible to invisible
				animation.setDuration(500); // duration - half a second
				animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
				animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
				animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
				image.startAnimation(animation);
			}
		} else {
			image.setBackgroundResource(android.R.color.transparent);
		}
	}

}
