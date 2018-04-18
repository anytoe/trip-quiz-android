package com.tripquiz.webapi.converter;

import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.User;
import com.tripquiz.webgeneric.ConvertJSON;
import com.tripquiz.webgeneric.ReturnText;


public class UserConverter extends ConvertJSON {

	public User getConvertedTask() {
		return (User)conversionResult;
	}

	public UserConverter(User user) {
		this.conversionResult = user;
	}

	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {
		if (returnText == ReturnText.CredentialsCreated || returnText == ReturnText.CredentialsSuccess) {
			// only set values when successful
			String email = jsonObject.getString("email");
			String username = jsonObject.getString("username");
			String key = jsonObject.getString("key");
			String token = jsonObject.getString("token");
			long expiryDateMs = jsonObject.getLong("expiryDate");

			User user = new User(email, username, ((User)conversionResult).getPassword());
			user.updateCredentials(key, token, expiryDateMs);
			conversionResult = user;
		}
		return (User)conversionResult;
	}
}
