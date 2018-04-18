package com.tripquiz.webgeneric;

public enum ReturnText {
	Success,
	// authentification
	CredentialsSuccess, TokenInvalid, CredentialFailure, CredentialsCreated, CredentialCreationFailure,
	// system errors
	ResourceNotFound, ServerException,
	// user
	SecretFailure;

	public static boolean isSuccess(ReturnText returnText) {
		return returnText == Success || returnText == CredentialsSuccess || returnText == CredentialsCreated;
	}
}