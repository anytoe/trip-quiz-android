package com.tripquiz.positionservice;

import java.util.Comparator;

public class LocationDataComparator implements Comparator<LocationData> {

	@Override
	public int compare(LocationData lhs, LocationData rhs) {
		if (!lhs.hasDistance() && !rhs.hasDistance())
			return 0;
		else if (!lhs.hasDistance())
			return -1;
		else if (!rhs.hasDistance())
			return 1;
		else {
			float distDiff = rhs.getDistance() - lhs.getDistance();
			if (distDiff != 0)
				return distDiff < 0 ? -1 : 1;
			else
				return 0;
		}
	}

}
