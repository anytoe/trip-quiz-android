package com.tripquiz.webgeneric;

public enum ErrorCode {
	
	InvalidUserName, InvalidPassword, InvalidEmail, 
	DuplicateUserName, DuplicateEmail, UserRejected,
	IdentifierPasswordMismatch,
	TokenNotRenewable // Token expired and saved credentials not working
	
}
