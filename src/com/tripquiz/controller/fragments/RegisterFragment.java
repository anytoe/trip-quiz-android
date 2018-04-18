package com.tripquiz.controller.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tripquiz.R;
import com.tripquiz.controller.ILoginStarted;
import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.controller.adapter.TextWatcherAdapter;
import com.tripquiz.generic.Validation;
import com.tripquiz.webapi.model.User;
import com.tripquiz.webgeneric.ErrorCode;

public class RegisterFragment extends AFragment {

	// UI references.
	private EditText mEmailView;
	private EditText mUsernameView;
	private EditText mPasswordView;

	private boolean isRegisterRunning = false;
	private TripQuizContext challengeManager;
	private ILoginStarted loginStartedListener;

	private boolean hasEditedUsername;
	private boolean isManualEdit = true;

	public RegisterFragment() {
		challengeManager = TripQuizContext.getTripQuizContext();
	}

//	public void setChallengeManager(TripQuizContext challengeManager, ILoginStarted loginStartedListener) {
//		this.challengeManager = challengeManager;
////		super.setChallengeManager(challengeManager);
//		this.loginStartedListener = loginStartedListener;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_register, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setFailureMessages(ErrorCode[] errorCodes) {
		for (ErrorCode code : errorCodes) {
			if (code == ErrorCode.DuplicateEmail)
				mEmailView.setError(activity.getString(R.string.errorcode_duplicateemail));
			if (code == ErrorCode.DuplicateUserName)
				mUsernameView.setError(activity.getString(R.string.errorcode_duplicateusername));
			if (code == ErrorCode.InvalidPassword)
				mPasswordView.setError(activity.getString(R.string.errorcode_invalidpassword));
		}
	}

	@Override
	protected void onResumeOnFinish() {
		super.onResumeOnFinish();

		loginStartedListener = (ILoginStarted) activity;
		
		// Set up the login form.
		mEmailView = (EditText) activity.findViewById(R.id.register_email);
		mUsernameView = (EditText) activity.findViewById(R.id.register_username);
		mPasswordView = (EditText) activity.findViewById(R.id.register_password);

		mUsernameView.addTextChangedListener(new TextWatcherAdapter() {

			@Override
			public void afterTextChanged(Editable s) {
				if (isManualEdit)
					hasEditedUsername = true;
			}

		});

		mEmailView.addTextChangedListener(new TextWatcherAdapter() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals("")) {
					hasEditedUsername = false;
				}

				if (!hasEditedUsername) {
					isManualEdit = false;
					String username = s.toString().split("@")[0];
					mUsernameView.setText(username);
					isManualEdit = true;
				}
			}

		});

		activity.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptRegister();
			}
		});
	}

	@Override
	public String getHeader() {
		return "Register";
	}

	/**
	 * Attempts to sign in or register the account specified by the login form. If there are form errors (invalid email, missing fields, etc.), the errors are
	 * presented and no actual login attempt is made.
	 */
	private void attemptRegister() {
		if (isRegisterRunning) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// // Store values at the time of the login attempt.
		String mEmail = mEmailView.getText().toString();
		String mUsername = mUsernameView.getText().toString();
		String mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// TODO apply same standards as .NET

		Validation validation = new Validation(activity.getString(R.string.username_regex), activity.getString(R.string.password_regex));

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_fieldrequired));
			focusView = mPasswordView;
			cancel = true;
		} else if (!validation.isValidPassword(mPassword)) {
			mPasswordView.setError(getString(R.string.errorcode_invalidpassword));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_fieldrequired));
			focusView = mEmailView;
			cancel = true;
		} else if (!validation.isValidEmailAddress(mEmail)) {
			mEmailView.setError(getString(R.string.errorcode_invalidemail));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_fieldrequired));
			focusView = mUsernameView;
			cancel = true;
		} else if (!validation.isValidUsername(mUsername)) {
			mUsernameView.setError(getString(R.string.errorcode_invalidusername));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			User user = new User(mEmail, mUsername, mPassword);
			loginStartedListener.loginProcessChanged(true, activity.getString(R.string.register_progress_registering));
			challengeManager.register(user);
		}
	}

}
