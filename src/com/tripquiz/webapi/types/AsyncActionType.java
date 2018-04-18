package com.tripquiz.webapi.types;

public enum AsyncActionType {
	// modify challenge
	QUESTION, CHECKIN,
	// read challenge
	CHALLENGE_DETAILS,
	// read challenges
	ChallengesNew, ChallengesUser,
	// modify challenge
	CHALLENGE_ADD,
	// add/login user
	USER
}
