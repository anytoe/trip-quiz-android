package com.tripquiz.webapi.converter;

import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webgeneric.ConvertJSON;


public class PrimitiveConverter<T> extends ConvertJSON {

	private T converted;

	public T getConvertedPrimitive() {
		return converted;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {
		converted = (T) jsonObject.get("value");
		return converted;
	}
}
