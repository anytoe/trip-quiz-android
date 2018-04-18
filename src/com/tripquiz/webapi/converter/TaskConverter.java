package com.tripquiz.webapi.converter;

import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.EntityKey;
import com.tripquiz.webapi.model.Task;
import com.tripquiz.webapi.model.Challenge.State;
import com.tripquiz.webapi.model.Challenge.TaskType;
import com.tripquiz.webapi.model.Task.QuestionType;
import com.tripquiz.webgeneric.ConvertJSON;

public class TaskConverter extends ConvertJSON {

	private Task convertedTask;

	public Task getConvertedTask() {
		return convertedTask;
	}

	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {
		EntityKey id = new EntityKey(jsonObject.getString("key"), Task.class);
		TaskType type = TaskType.valueOf(jsonObject.getString("actionType"));
		String description = jsonObject.getString("description");
		QuestionType solutionType = QuestionType.valueOf(jsonObject.getString("solutionType"));
		String solutionHint = jsonObject.getString("solutionHint");
		State status = State.valueOf(jsonObject.getString("status"));
		int quantityCount = jsonObject.getInt("repeatCount");
		int quantityMax = jsonObject.getInt("repeat");

		conversionResult = convertedTask = new Task(id, type, status, description, solutionType, solutionHint, quantityMax, quantityCount);
		return convertedTask;
	}

}
