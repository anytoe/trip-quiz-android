package com.tripquiz.webapi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class CompetitorProgress implements Serializable {

	private static final long serialVersionUID = -4563734862582804222L;
	private int userRanking;
	private ArrayList<RankingListItem> rankingList;

	public CompetitorProgress(int userRanking) {
		super();
		this.userRanking = userRanking;
		rankingList = new ArrayList<RankingListItem>();
	}

	public void addUserRank(String userName, double percentageFinished) {
		rankingList.add(new RankingListItem(userName, percentageFinished));
	}

	public boolean isUserFirst() {
		return hasRanking() && getUserRanking() == 1;
	}

	public boolean hasRanking() {
		return rankingList.size() > 0;
	}
	
	public boolean hasUserRanking(){
		return userRanking > 0;
	}

	public int getUserRanking() {
		return userRanking;
	}

	public boolean hasLeader() {
		return rankingList.size() > 0;
	}

	public String getLeaderUsername() {
		return rankingList.get(0).getUserName();
	}

	public double getPercentageFinished() {
		return rankingList.get(0).getPercentageFinished();
	}

	public Collection<RankingListItem> GetRanking() {
		return rankingList;
	}

	public static class RankingListItem implements Serializable {

		private static final long serialVersionUID = 3006536916914678412L;
		private String userName;
		private double percentageFinished;

		public RankingListItem(String userName, double percentageFinished) {
			super();
			this.userName = userName;
			this.percentageFinished = percentageFinished;
		}

		public String getUserName() {
			return userName;
		}

		public double getPercentageFinished() {
			return percentageFinished;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(percentageFinished);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + ((userName == null) ? 0 : userName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RankingListItem other = (RankingListItem) obj;
			if (Double.doubleToLongBits(percentageFinished) != Double.doubleToLongBits(other.percentageFinished))
				return false;
			if (userName == null) {
				if (other.userName != null)
					return false;
			} else if (!userName.equals(other.userName))
				return false;
			return true;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rankingList == null) ? 0 : rankingList.hashCode());
		result = prime * result + userRanking;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompetitorProgress other = (CompetitorProgress) obj;
		if (rankingList == null) {
			if (other.rankingList != null)
				return false;
		} else if (!rankingList.equals(other.rankingList))
			return false;
		if (userRanking != other.userRanking)
			return false;
		return true;
	}

}
