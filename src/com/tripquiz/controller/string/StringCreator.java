package com.tripquiz.controller.string;

import android.content.Context;

import com.tripquiz.R;
import com.tripquiz.webapi.model.CompetitorProgress;

public class StringCreator {

	public static String getCompetitorProgressString(Context context, CompetitorProgress competitorProgress) {
		if (competitorProgress.hasLeader() && !competitorProgress.isUserFirst())
			if (competitorProgress.getPercentageFinished() < 1) {
				// somebody else is ahead but hasn't won
				return competitorProgress.getLeaderUsername() + " " + context.getString(R.string.challenges_user_leader) + " "
						+ Math.round(competitorProgress.getPercentageFinished() * 100) + "%";
			} else {
				// somebody else won
				return competitorProgress.getLeaderUsername() + " " + context.getString(R.string.challenges_user_otherwon);
			}
		else if (competitorProgress.isUserFirst()) {
			if (competitorProgress.getPercentageFinished() < 1) {
				// user is leading
				return context.getString(R.string.challenges_user_youlead);
			} else {
				// user won
				return context.getString(R.string.challenges_user_youwon);
			}
		} else
			return context.getString(R.string.challenges_user_nolead);
	}

	public static String getDistanceString(double distance) {
		String distanceString = "";
		if (distance >= 0) {
			long distanceMeter = Math.round(distance);
			if (distanceMeter > 500) {
				double distanceKm = distanceMeter / 1000.;
				distanceKm = Math.round(distanceKm * 10) / 10.;
				distanceString = distanceKm + "km";
			} else {
				distanceString = distanceMeter + "m";
			}
		}

		return distanceString;
	}
}
