package com.tripquiz.persistance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Repository<T> {

	protected Context context;
	private String uniqueName;
	private static String fileSuffix = ".dt";

	public Repository(Context context, String uniqueName) {
		this.context = context;
		this.uniqueName = uniqueName;
	}

	public void saveObject(T object) {
		SharedPreferences myPrefs = context.getSharedPreferences(uniqueName, Context.MODE_PRIVATE);
		Editor editor = myPrefs.edit();
		editor.putBoolean("has" + uniqueName, true);
		editor.commit();

		String fileName = uniqueName + fileSuffix;
		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(object);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public T loadObject(T newObjectToReturnIfNothingSaved) {
		String fileName = uniqueName + fileSuffix;
		try {
			FileInputStream fis = context.openFileInput(fileName);
			ObjectInputStream in = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			T loadedObject = (T) in.readObject();
			in.close();
			return loadedObject;
		} catch (Exception e) {
//			if (newObjectToReturnIfNothingSaved == null)
//				throw new RuntimeException(e.getMessage());
		}
		
		return newObjectToReturnIfNothingSaved;
	}

	public T loadObject() {
		return loadObject(null);
	}

	public boolean deleteObject(){
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
