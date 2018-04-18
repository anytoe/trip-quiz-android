package com.tripquiz.webapi.converter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.ImageUrl;
import com.tripquiz.webapi.model.LocationDt;
import com.tripquiz.webapi.model.Task;
import com.tripquiz.webapi.model.Challenge.State;
import com.tripquiz.webgeneric.ConvertJSON;

public class LocationConverter extends ConvertJSON {

	private LocationDt convertedLocation;

	public LocationDt getConvertedLocation() {
		return convertedLocation;
	}

	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {
		EntityKey locKey = new EntityKey(jsonObject.getString("key"), LocationDt.class);
		String name = jsonObject.getString("name");

		long solvedAt = 0;
		if (jsonObject.has("solvedAt"))
			solvedAt = jsonObject.getLong("solvedAt");
		State locationState = State.valueOf(jsonObject.getString("status"));
		double lat = jsonObject.getDouble("lat");
		double lng = jsonObject.getDouble("lng");

		ImageUrlConverter imageUrlConverter = new ImageUrlConverter();
		ImageUrl imageUrl = (ImageUrl) imageUrlConverter.convertObject(jsonObject);

		// read task
		JSONArray taskArray = jsonObject.getJSONArray("tasks");
		Map<EntityKey, Task> tasks = new HashMap<EntityKey, Task>();
		TaskConverter taskConverter = new TaskConverter();
		for (int index = 0; index < taskArray.length(); index++) {
			JSONObject taskObject = (JSONObject) taskArray.get(index);
			taskConverter.convertObject(taskObject);
			Task convert = taskConverter.getConvertedTask();
			tasks.put(convert.getKey(), convert);
		}

		conversionResult = convertedLocation = new LocationDt(locKey, name, lat, lng, locationState, solvedAt, tasks, imageUrl);
		return conversionResult;
	}
}
