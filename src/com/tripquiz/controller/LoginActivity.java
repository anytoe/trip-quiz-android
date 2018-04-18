package com.tripquiz.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tripquiz.R;
import com.tripquiz.controller.adapter.SectionsPagerAdapter;
import com.tripquiz.controller.fragments.LoginFragment;
import com.tripquiz.controller.fragments.RegisterFragment;
import com.tripquiz.persistance.Repository;
import com.tripquiz.webapi.AChallengeMeService;
import com.tripquiz.webapi.AsyncAction;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webapi.model.WebserviceModel;
import com.tripquiz.webapi.types.ActionListenerAdapter;
import com.tripquiz.webapi.types.AsyncActionCategory;
import com.tripquiz.webapi.types.AsyncActionType;
import com.tripquiz.webapi.types.ICredentialsValidator;
import com.tripquiz.webgeneric.ErrorCode;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends FragmentActivity implements ILoginStarted, ICredentialsValidator {

	private TripQuizContext challengeManager;

	private LoginFragment loginFragment;
	private RegisterFragment registerFragment;
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewLoginPager;

	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private ActionListenerAdapter actionListener;

	private Repository<User> userRepository;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		challengeManager = TripQuizContext.getTripQuizContext();
		challengeManager.init(this, this);

		// loading bar and fragment container
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mViewLoginPager = (ViewPager) findViewById(R.id.login_pager);

		userRepository = new Repository<User>(this, "User");
		if (userRepository.hasObject()) {
			user = userRepository.loadObject();
		}

		actionListener = new ActionListenerAdapter() {

			// just the user challenges are being loaded at startup
			private int challengeCategoriesToLoad = 1;

			@Override
			public void notifyOnError(AsyncAction action, Exception exception) {
			}

			public void notifyListenerActionUpdate(AsyncActionType actionType, AsyncActionCategory actionCategory) {
			}

			@Override
			public void notifyListenerActionStarted(AsyncAction action) {
				if (action.getAsyncActionCategory() == AsyncActionCategory.READ_CHALLENGES) {
					String loadText = "Loading ";
					if (action.getAppActionType() == AsyncActionType.ChallengesNew)
						mLoginStatusMessageView.setText(loadText + "New Challenges ...");
					else if (action.getAppActionType() == AsyncActionType.ChallengesUser)
						mLoginStatusMessageView.setText(loadText + "Your Challenges ...");
				}
			}

			@Override
			public void notifyListenerActionStopped(AsyncAction action) {
				if (action.getAsyncActionCategory() == AsyncActionCategory.READ_CHALLENGES) {
					--challengeCategoriesToLoad;
					String loadText = "Loaded ";
					if (action.getAppActionType() == AsyncActionType.ChallengesNew)
						mLoginStatusMessageView.setText(loadText + "new Challenges");
					else if (action.getAppActionType() == AsyncActionType.ChallengesUser)
						mLoginStatusMessageView.setText(loadText + "your Challenges");
				}

				if (challengeCategoriesToLoad == 0) {
					challengeManager.getActionFactory().removeListenerAction(AsyncActionType.ChallengesNew, actionListener);
					challengeManager.getActionFactory().removeListenerAction(AsyncActionType.ChallengesUser, actionListener);
					mLoginStatusMessageView.setText("Ready to go!");
					Intent detailIntent = new Intent(LoginActivity.this, MainActivity.class);
					if (getIntent() != null && getIntent().getExtras() != null)
						detailIntent.putExtras(getIntent().getExtras());
					startActivity(detailIntent);
					LoginActivity.this.finish();
				}
			}
		};
		challengeManager.getActionFactory().addListenerAction(AsyncActionType.ChallengesNew, actionListener);
		challengeManager.getActionFactory().addListenerAction(AsyncActionType.ChallengesUser, actionListener);

		findViewById(R.id.login_container).setVisibility(View.VISIBLE);

		if (user != null) {
			loginProcessChanged(true, getString(R.string.login_progress_signingin));
			challengeManager.setWebserviceInitialisedListener(this);
			challengeManager.checkCredentials(user);
		} else {
			loginProcessChanged(false, "");
			initAccessFragments();
		}
	};

	private void initAccessFragments() {
		loginFragment = new LoginFragment();
//		loginFragment.setChallengeManager(challengeManager, this, user);
		registerFragment = new RegisterFragment();
//		registerFragment.setChallengeManager(challengeManager, this);
		mSectionsPagerAdapter = new SectionsPagerAdapter(challengeManager, getSupportFragmentManager());
		mSectionsPagerAdapter.addFragment(LoginFragment.class.toString(), loginFragment);
		mSectionsPagerAdapter.addFragment(RegisterFragment.class.toString(), registerFragment);

		// Set up the ViewPager with the sections adapter.
		mViewLoginPager.setAdapter(mSectionsPagerAdapter);

		mViewLoginPager.setCurrentItem(mSectionsPagerAdapter.getPositionForKey(LoginFragment.class.toString()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login_expandable, menu);
		return true;
	}

	@Override
	public void loginProcessChanged(boolean started, String text) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		mLoginStatusMessageView.setText(text);
		showProgress(started);
	}

	@Override
	public void initialised(User user, AChallengeMeService challengeMeService) {
		userRepository.saveObject(user);
		mLoginStatusMessageView.setText("Credentials validated");
		challengeManager.getActionFactory().loadUserChallenges();
		// challengeManager.getActionFactory().loadNewChallenges("");
	}

	@Override
	public void initialisedFailure(final ErrorCode[] errorCodes) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (loginFragment == null || registerFragment == null)
					initAccessFragments();
				loginFragment.setFailureMessages(errorCodes);
				registerFragment.setFailureMessages(errorCodes);
				mLoginStatusMessageView.setText("");
				showProgress(false);
			}
		});
	}

	@Override
	public void initialised(WebserviceModel webserviceModel) {
		// nothing to do
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			mViewLoginPager.setVisibility(View.VISIBLE);
			mViewLoginPager.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mViewLoginPager.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mViewLoginPager.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	protected void onPause() {
		if (challengeManager != null) {
			challengeManager.stop();
		}
		super.onPause();
	}

}
