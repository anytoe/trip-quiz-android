package com.tripquiz.controller.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.TextView;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.positionservice.IPositionListener;
import com.tripquiz.positionservice.IPositionService;

public abstract class AListFragment extends ListFragment implements INamedFragment, IPositionListener {

	protected Activity activity;
	private TextView emptyTextView;
	private int resId = -1;

	private boolean isOnResume = false;
	protected boolean isVisible;

	protected TripQuizContext challengeManager;
	protected IChallengeClickListener challengeClick;
	protected IPositionService positionService;

	public AListFragment() {
		challengeManager = TripQuizContext.getTripQuizContext();
	}

	// public void setChallengeManager(TripQuizContext challengeManager, IChallengeClickListener challengeClick) {
	// this.challengeManager = challengeManager;
	// this.challengeClick = challengeClick;
	// this.challengeManager.getPositionServiceAccess().addListener(this);
	// }

	protected boolean isFragmentInitialised() {
		return challengeManager != null && activity != null && isOnResume;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		super.getListView().setScrollingCacheEnabled(false);
		emptyTextView = (TextView) view.findViewById(android.R.id.empty);
	}

	protected void setEmptyText(int resId) {
		if (emptyTextView != null)
			emptyTextView.setText(getString(resId));
		else
			this.resId = resId;
	}

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		this.positionService = positionService;
	}

	@Override
	public void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService) {
		// nothing to do
	}

	@Override
	public void positionAccuracyUpdate(int currentProgress, int maxProgress) {
		// nothing to do
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		challengeClick = (IChallengeClickListener) activity;
		if (isFragmentInitialised())
			onResumeOnFinish();
	}

	@Override
	public void onResume() {
		super.onResume();
		isOnResume = true;
		if (resId != -1)
			setEmptyText(resId);
		if (isFragmentInitialised())
			onResumeOnFinish();
	}

	protected void onResumeOnFinish() {
		isVisible = true;
	}

	@Override
	public void onPause() {
		isOnResume = false;
		if (challengeManager != null) {
			challengeManager.getPositionServiceAccess().removeListener(this);
		}
		super.onPause();
	}

}
