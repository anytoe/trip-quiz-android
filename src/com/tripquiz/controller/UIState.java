package com.tripquiz.controller;

import java.io.Serializable;

public class UIState implements Serializable {

	private static final long serialVersionUID = -4711090098032771694L;
	private transient boolean actionDialogOpened = false; // save
	private transient boolean isAppMinimized = false;
	private String challengeSearchTerm;

	public UIState() {
		super();
	}

	public boolean isActionDialogOpened() {
		return actionDialogOpened;
	}

	public void setActionDialogOpened(boolean isOpened) {
		actionDialogOpened = isOpened;
	}

	public boolean isAppMinimized() {
		return isAppMinimized;
	}

	public void setAppMinimized(boolean isAppMinimized) {
		this.isAppMinimized = isAppMinimized;
	}

	public String getChallengeSearchTerm() {
		return challengeSearchTerm;
	}

	public void setChallengeSearchTerm(String challengeSearchTerm) {
		this.challengeSearchTerm = challengeSearchTerm;
	}
	
}
