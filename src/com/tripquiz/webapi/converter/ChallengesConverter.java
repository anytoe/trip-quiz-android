package com.tripquiz.webapi.converter;

import java.util.Collection;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webgeneric.ConvertJSON;


public class ChallengesConverter extends ConvertJSON {

	private Collection<Challenge> convertedChallenges;

	public Collection<Challenge> getConvertedChallenges() {
		return convertedChallenges;
	}

	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {
		JSONArray challengeArray = jsonObject.getJSONArray("challengeSet");

		// read challenges
		convertedChallenges = new HashSet<Challenge>();
		ChallengeConverter challengeConv = new ChallengeConverter();
		for (int index = 0; index < challengeArray.length(); index++) {
			JSONObject challengeObject = (JSONObject) challengeArray.get(index);
			challengeConv.convertObject(challengeObject);
			Challenge convertedChallenge = challengeConv.getConvertedChallenge();
			convertedChallenges.add(convertedChallenge);
		}

		conversionResult = convertedChallenges;
		return convertedChallenges;
	}
}
