package com.tripquiz.controller.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.positionservice.IPositionListener;
import com.tripquiz.positionservice.IPositionService;

public abstract class AFragment extends Fragment implements INamedFragment, IPositionListener {

	protected Activity activity;
	private boolean isOnResume = false;
	protected boolean isVisible;

	protected IPositionService positionService;
	protected TripQuizContext challengeManager;

	public AFragment() {
		challengeManager = TripQuizContext.getTripQuizContext();
	}

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		this.positionService = positionService;
	}

	@Override
	public void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService) {
		// nothing to do
	}
	
	public void positionAccuracyUpdate(int currentProgress, int maxProgress){
		// nothing to do
	}

	protected void disablePositionService() {
		challengeManager.getPositionServiceAccess().removeListener(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		if (isFragmentInitialised())
			onResumeOnFinish();
	}

	@Override
	public void onResume() {
		super.onResume();
		isOnResume = true;
		if (isFragmentInitialised())
			onResumeOnFinish();
	}

	protected void onResumeOnFinish() {
		isVisible = true;
		challengeManager.getPositionServiceAccess().addListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		isVisible = false;
		if (challengeManager != null) {
			challengeManager.getPositionServiceAccess().removeListener(this);
		}
		isOnResume = false;
	}

	protected boolean isFragmentInitialised() {
		return challengeManager != null && activity != null && isOnResume;
	}

}
