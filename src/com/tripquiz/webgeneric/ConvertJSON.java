package com.tripquiz.webgeneric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ConvertJSON {

	protected Object conversionResult;
	protected Event eventText;
	protected ReturnText returnText;
	protected ErrorCode[] errorCodes;

	public Object getConvertedResult() {
		return conversionResult;
	}

	public Event getEventText() {
		return eventText;
	}

	public ReturnText getReturnText() {
		return returnText;
	}

	public ErrorCode[] getErrorCodes() {
		return errorCodes;
	}

	/**
	 * Converts the header of a returning json text
	 * 
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
	public Object convert(String jsonString) throws JSONException {
		JSONObject rootObject = new JSONObject(jsonString);
		JSONObject meta = rootObject.getJSONObject("meta");
		eventText = Event.valueOf(meta.getString("eventText"));
		returnText = ReturnText.valueOf(meta.getString("returnText"));
		if (!meta.isNull("errorCodes")) {
			JSONArray errorCodesArray = meta.getJSONArray("errorCodes");
			int len = errorCodesArray.length();
			ErrorCode[] errorCodeList = new ErrorCode[len];
			for (int i = 0; i < len; i++) {
				errorCodeList[i] = ErrorCode.valueOf(errorCodesArray.get(i).toString());
			}
			errorCodes = errorCodeList;
		}

		try {
			return convertObject(rootObject.getJSONObject("response"));
		} catch (Exception ex) {
			return convertObject(null);
		}
	}

	protected abstract Object convertObject(JSONObject jsonObject) throws JSONException;
}