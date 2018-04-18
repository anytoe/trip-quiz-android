package com.tripquiz.webapi.converter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.Challenge;
import com.tripquiz.webapi.model.CompetitorProgress;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.ImageUrl;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webgeneric.ConvertJSON;

public class ChallengeConverter extends ConvertJSON {

	private Challenge convertedChallenge;

	public Challenge getConvertedChallenge() {
		return convertedChallenge;
	}

	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {
		EntityKey key = new EntityKey(jsonObject.getString("key"), Challenge.class);
		String name = jsonObject.getString("name");

		ImageUrlConverter imageUrlConverter = new ImageUrlConverter();
		ImageUrl imageUrl = (ImageUrl) imageUrlConverter.convertObject(jsonObject);

		boolean isRestricted = false;
		if (jsonObject.has("isRestricted"))
			isRestricted = jsonObject.getBoolean("isRestricted");

		String desc = "";
		if (!jsonObject.isNull("description"))
			desc = jsonObject.getString("description");

		double percentageFinished = 0;
		if (jsonObject.has("percentageFinished"))
			percentageFinished = jsonObject.getDouble("percentageFinished");

		// read locations
		JSONArray locationArray = jsonObject.getJSONArray("locations");
		Map<EntityKey, LocationDt> locations = new HashMap<EntityKey, LocationDt>();
		LocationConverter locationConverter = new LocationConverter();
		for (int index = 0; index < locationArray.length(); index++) {
			JSONObject taskObject = (JSONObject) locationArray.get(index);
			locationConverter.convertObject(taskObject);
			LocationDt convLocation = locationConverter.getConvertedLocation();
			locations.put(convLocation.getKey(), convLocation);
		}

		// read competitor progress
		CompetitorProgress compProg = null;
		if (!jsonObject.isNull("competitorProgress")) {
			CompetitorProgressConverter compConverter = new CompetitorProgressConverter();
			compProg = (CompetitorProgress) compConverter.convertObject(jsonObject.getJSONObject("competitorProgress"));
		}
		conversionResult = convertedChallenge = new Challenge(key, name, desc, percentageFinished, locations, compProg, isRestricted, imageUrl);

		return conversionResult;
	}
}
