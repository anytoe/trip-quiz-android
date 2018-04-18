package com.tripquiz.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tripquiz.R;
import com.tripquiz.controller.ILoginStarted;
import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webgeneric.ErrorCode;

public class LoginFragment extends AFragment {

	private ILoginStarted loginStartedListener;

	// UI references.
	private EditText identifierView;
	private EditText passwordView;

	private boolean isLoginRunning = false;
	private TripQuizContext challengeManager;

	private User user;

	public LoginFragment() {
		challengeManager = TripQuizContext.getTripQuizContext();
	}

	// public void setChallengeManager(ILoginStarted loginStartedListener, User user) {
	// this.user = user;
	// // this.challengeManager = challengeManager;
	// // super.setChallengeManager(challengeManager);
	// this.loginStartedListener = loginStartedListener;
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setFailureMessages(ErrorCode[] errorCodes) {
		for (ErrorCode code : errorCodes) {
			if (code == ErrorCode.IdentifierPasswordMismatch)
				passwordView.setError(activity.getString(R.string.errorcode_identifierpasswordmismatch));
		}
	}

	@Override
	protected void onResumeOnFinish() {
		super.onResumeOnFinish();

		// // enter key listener
		// OnKeyListener enterListener = new OnKeyListener() {
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
		// attemptLogin();
		// return true;
		// }
		// return false;
		// }
		// };
		loginStartedListener = (ILoginStarted) activity;

		// Set up the login form.
		identifierView = (EditText) activity.findViewById(R.id.login_email);
		// identifierView.setOnKeyListener(enterListener);
		passwordView = (EditText) activity.findViewById(R.id.login_password);
		// passwordView.setOnKeyListener(enterListener);

		if (user != null && !user.hasValidToken())
			identifierView.setText(user.getEmail());

		activity.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

	}

	@Override
	public String getHeader() {
		return "Login";
	}

	/**
	 * Attempts to sign in or register the account specified by the login form. If there are form errors (invalid email, missing fields, etc.), the errors are
	 * presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		if (isLoginRunning) {
			return;
		}
		// Reset errors.
		identifierView.setError(null);
		passwordView.setError(null);

		// // Store values at the time of the login attempt.
		String identifier = identifierView.getText().toString();
		String password = passwordView.getText().toString();

		// boolean cancel = false;
		// View focusView = null;

		// Validation validation = new Validation(activity.getString(R.string.username_regex), activity.getString(R.string.password_regex));

		// Check for a valid password.
		// if (TextUtils.isEmpty(password)) {
		// passwordView.setError(getString(R.string.error_fieldrequired));
		// focusView = passwordView;
		// cancel = true;
		// } else if (!validation.isValidPassword(password)) {
		// passwordView.setError(getString(R.string.errorcode_invalidpassword));
		// focusView = passwordView;
		// cancel = true;
		// }

		// Check for a valid email address.
		// if (TextUtils.isEmpty(identifier)) {
		// identifierView.setError(getString(R.string.error_fieldrequired));
		// focusView = identifierView;
		// cancel = true;
		// } else if (!validation.isValidUsername(identifier) && !validation.isValidEmailAddress(identifier)) {
		// identifierView.setError(getString(R.string.errorcode_invalidemailusername));
		// focusView = identifierView;
		// cancel = true;
		// }

		// if (cancel) {
		// There was an error; don't attempt login and focus the first
		// form field with an error.
		// focusView.requestFocus();
		// } else {
		// Show a progress spinner, and kick off a background task to
		// perform the user login attempt.
		User user = new User(identifier, null, password);
		loginStartedListener.loginProcessChanged(true, activity.getString(R.string.login_progress_signingin));
		challengeManager.checkCredentials(user);
		// }
	}

}
