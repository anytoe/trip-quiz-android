package com.tripquiz.generic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Validation {

	private String userNameRegex;
	private String passwordRegex;

	public Validation(String userNameRegex, String passwordRegex) {
		super();
		this.userNameRegex = userNameRegex;
		this.passwordRegex = passwordRegex;
	}

	public boolean isValidEmailAddress(String email) {

		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			return false;
		}

		return true;
	}

	public boolean isValidPassword(String password) {
		return applyRegex(passwordRegex, password);
	}

	public boolean isValidUsername(String username) {
		return applyRegex(userNameRegex, username);
	}

	private boolean applyRegex(String regex, String textToValidate) {
		Pattern pat = Pattern.compile(regex);
		Matcher match = pat.matcher(textToValidate);
		return match.find();
	}
}
