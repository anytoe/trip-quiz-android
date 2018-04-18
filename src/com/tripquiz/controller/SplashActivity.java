package com.tripquiz.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tripquiz.R;
import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ICredentialsValidator;
import com.tripquiz.webgeneric.ErrorCode;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// outputScreenType();
		challengeManager = TripQuizContext.getTripQuizContext();
		challengeManager.init(this, new ICredentialsValidator() {

			@Override
			public void initialisedFailure(ErrorCode[] errorCodes) {
				openLoginActivity();
			}

			@Override
			public void initialised(User user, AChallengeMeService challengeMeService) {
				proceedToMainScreen();
			}

			@Override
			public void initialised(WebserviceModel webserviceModel) {
				// TODO Auto-generated method stub
			}
		});
		challengeManager.checkCredentials();
	}

//	private void proceedToLoginScreen() {
//		Handler handlerUI = new Handler();
//		handlerUI.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				openLoginActivity();
//			}
//		}, 2000);
//	}

	private void openLoginActivity() {
		Intent detailIntent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(detailIntent);
		finish();
	}

	private void proceedToMainScreen() {
		Handler handlerUI = new Handler();
		handlerUI.postDelayed(new Runnable() {

			@Override
			public void run() {
//				activityStarted = true;
				openLoginActivity();
//				splashScreenTimeOver = true;
			}
		}, 2000);
	}

//	private boolean challengesUserLoaded = false;
//	private boolean challengesNewLoaded = false;
//	private boolean splashScreenTimeOver = false;
//	private boolean challengesPrecached = false;
	private TripQuizContext challengeManager;
	// TODO remove this fix if possible!!!!!
	// onResume is being called twice, any settings changed which trigger that?
//	private boolean activityStarted = false;

	@Override
	protected void onPause() {
		challengeManager.stop();
		super.onPause();
	}

//	private void outputScreenType() {
//
//		// Determine screen size
//		if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
//			Toast.makeText(this, "Large screen", Toast.LENGTH_LONG).show();
//
//		} else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
//			Toast.makeText(this, "Normal sized screen", Toast.LENGTH_LONG).show();
//
//		} else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
//			Toast.makeText(this, "Small sized screen", Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(this, "Screen size is neither large, normal or small", Toast.LENGTH_LONG).show();
//		}
//
//		// Determine density
//		DisplayMetrics metrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		int density = metrics.densityDpi;
//
//		if (density == DisplayMetrics.DENSITY_HIGH) {
//			Toast.makeText(this, "DENSITY_HIGH... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
//		} else if (density == DisplayMetrics.DENSITY_MEDIUM) {
//			Toast.makeText(this, "DENSITY_MEDIUM... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
//		} else if (density == DisplayMetrics.DENSITY_LOW) {
//			Toast.makeText(this, "DENSITY_LOW... Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(this, "Density is neither HIGH, MEDIUM OR LOW.  Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
//		}
//	}
}
