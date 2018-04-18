package com.tripquiz.webapi.converter;

import org.json.JSONException;
import org.json.JSONObject;

import com.tripquiz.webapi.model.ImageUrl;
import com.tripquiz.webgeneric.ConvertJSON;

public class ImageUrlConverter extends ConvertJSON {

	private ImageUrl convertedImageUrl;

	public ImageUrl getConvertedImageUrl() {
		return convertedImageUrl;
	}

	@Override
	public Object convertObject(JSONObject jsonObject) throws JSONException {

		String imageUrl = null, imageSize = null;
		if (!jsonObject.isNull("imageUrl")) {
			imageUrl = jsonObject.getString("imageUrl");
			if (!jsonObject.isNull("imageSize")) {
				imageSize = jsonObject.getString("imageSize");
				if (imageSize.contains("x")) {
					// TODO needs check for two integers as well
					conversionResult = new ImageUrl(imageUrl, imageSize);
				}
			}
		}
		return conversionResult;
	}
}
