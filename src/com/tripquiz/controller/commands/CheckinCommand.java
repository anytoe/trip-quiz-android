package com.tripquiz.controller.commands;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import com.tripquiz.controller.TripQuizContext;
import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.LocationDt;

public class CheckinCommand implements OnClickListener, DialogInterface.OnClickListener {

	private final TripQuizContext challengeManager;
	private final LocationDt location;
	private final String[] keys;

	public CheckinCommand(LocationDt location, TripQuizContext challengeManager){
		this.location = location;
		this.challengeManager = challengeManager;
		keys = null;
	}
	
	public CheckinCommand(String[] keys, TripQuizContext challengeManager){
		this.keys = keys;
		this.challengeManager = challengeManager;
		location = null;
	}
	
	@Override
	public void onClick(View v) {
		challengeManager.getActionFactory().checkin(location.getKey());
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		challengeManager.getActionFactory().checkin(new EntityKey(keys[which], LocationDt.class));
	}

}
