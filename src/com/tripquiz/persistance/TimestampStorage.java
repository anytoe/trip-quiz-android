package com.tripquiz.persistance;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TimestampStorage {

	private Context context;
	private String uniqueName;

	public TimestampStorage(Context activity, String uniqueName) {
		super();
		this.context = activity;
		this.uniqueName = uniqueName;
	}

	public Date getLat() {
		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		return new Date(myPrefs.getLong("timestamp", 0));
	}

	public void saveTimestamp() {
		saveTimestamp(new Date());
	}

	public void saveTimestamp(Date date) {

		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		Editor editor = myPrefs.edit();
		editor.putBoolean("hasTimestamp", true);
		editor.putLong("timestamp", date.getTime());
		editor.commit();
	}

	public boolean hasSavedPosition() {
		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		return myPrefs.getBoolean("hasTimestamp", false);
	}

}
