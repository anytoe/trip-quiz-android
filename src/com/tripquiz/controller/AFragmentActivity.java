package com.tripquiz.controller;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

import com.tripquiz.controller.fragments.StatusBarFragment;
import com.tripquiz.positionservice.IPositionListener;
import com.tripquiz.positionservice.IPositionService;
import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.types.ICredentialsValidator;
import com.tripquiz.webgeneric.ErrorCode;

public abstract class AFragmentActivity extends FragmentActivity implements IPositionListener, ICredentialsValidator {

	protected Menu menu;

	protected StatusBarFragment statusBarFragment;
	protected IPositionService positionService;
	protected float positionAccuracy = 0;

	protected TripQuizContext challengeManager;
	protected boolean isInitialised = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		challengeManager = TripQuizContext.getTripQuizContext();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();

		challengeManager.init(this, this);
		challengeManager.checkCredentials();
		challengeManager.getPositionServiceAccess().addListener(this);
		updateMenu();
		setVisibilityStatusBar();
	}

	protected abstract void updateMenu();

	@Override
	public void positionUpdate(boolean enabled, IPositionService positionService) {
		this.positionService = positionService;
		updateMenu();
		setVisibilityStatusBar();
	}

	@Override
	public void locationInRangeUpdate(String[] locationsInRange, IPositionService positionService) {
		// nothing to do
	}

	public void positionAccuracyUpdate(int currentProgress, int maxProgress) {
		positionAccuracy = (float) currentProgress / maxProgress;
		updateMenu();
		setVisibilityStatusBar();
	}

	protected void setVisibilityStatusBar() {
		// changed
		if (isInitialised && statusBarFragment != null && positionService != null) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			if (positionService.hasAccurateDistance()) {
				transaction.show(statusBarFragment).commitAllowingStateLoss();
			} else {
				transaction.hide(statusBarFragment).commitAllowingStateLoss();
			}
		}
	}

	@Override
	public void initialised(User user, AChallengeMeService challengeMeService) {
		// challengeMeService.addListenerAllActions(new ActionResultNotifier(this, challengeManager.getWebserviceModel()));
	}

	@Override
	public void initialisedFailure(ErrorCode[] errorCodes) {
		for (ErrorCode errorCode : errorCodes)
			if (errorCode == ErrorCode.TokenNotRenewable) {
				challengeManager.logoutUser();
				Intent loginIntent = new Intent(this, LoginActivity.class);
				startActivity(loginIntent);
				finish();
				return;
			}
	}

	@Override
	protected void onPause() {
		if (challengeManager != null) {
			challengeManager.getPositionServiceAccess().removeListener(this);
			challengeManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
