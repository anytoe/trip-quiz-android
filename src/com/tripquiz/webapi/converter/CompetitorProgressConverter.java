package com.tripquiz.webapi.converter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.CompetitorProgress;
import com.tripquiz.webgeneric.ConvertJSON;

public class CompetitorProgressConverter extends ConvertJSON {

	private CompetitorProgress convertedCompetitorProgress;

	public CompetitorProgress getConvertedCompetitorProgress() {
		return convertedCompetitorProgress;
	}

	@Override
	protected Object convertObject(JSONObject jsonObject) throws JSONException {

		int userRanking = -1;
		if (!jsonObject.isNull("userRanking"))
			userRanking = jsonObject.getInt("userRanking");

		conversionResult = convertedCompetitorProgress = new CompetitorProgress(userRanking);

		if (!jsonObject.isNull("ranking")) {
			JSONArray rankingArray = jsonObject.getJSONArray("ranking");
			if (rankingArray.length() > 0) {
				for (int index = 0; index < rankingArray.length(); index++) {
					JSONObject rankingObject = rankingArray.getJSONObject(index);
					String leaderUsername = null;
					if (rankingObject.has("userName"))
						leaderUsername = rankingObject.getString("userName");
					double percentageFinished = 0;
					if (rankingObject.has("percentageFinished"))
						percentageFinished = rankingObject.getDouble("percentageFinished");
					convertedCompetitorProgress.addUserRank(leaderUsername, percentageFinished);
				}
			}
		}

		return conversionResult;
	}

}
