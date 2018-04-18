package com.tripquiz.persistance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageRepository<T> {

	protected Context context;
	private String uniqueName;
	private static String fileSuffix = ".dt";

	private ImageRepository(Context context, String uniqueName) {
		this.context = context;
		this.uniqueName = uniqueName;
	}

	public void saveObject(Bitmap bmp) {
		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		Editor editor = myPrefs.edit();
		editor.putBoolean("has" + uniqueName, true);
		editor.commit();

		FileOutputStream out = null;
		try {
			String fileName = uniqueName + fileSuffix;
			out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bitmap loadObject() {
		
		Bitmap loadedObject = null;
		try {
			String fileName = uniqueName + fileSuffix;
			FileInputStream fis = context.openFileInput(fileName);
			loadedObject = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return loadedObject;
	}

	public boolean deleteObject() {
		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		Editor editor = myPrefs.edit();
		editor.putBoolean("has" + uniqueName, false);
		editor.commit();

		String fileName = uniqueName + fileSuffix;
		return context.deleteFile(fileName);
	}

	public boolean hasObject() {
		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		return myPrefs.getBoolean("has" + uniqueName, false);
	}

}
