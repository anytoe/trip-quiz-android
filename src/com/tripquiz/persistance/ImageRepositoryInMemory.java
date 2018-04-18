package com.tripquiz.persistance;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageRepositoryInMemory {

	protected Context context;

	private HashMap<String, Bitmap> objectMap = new HashMap<String, Bitmap>();

	public ImageRepositoryInMemory(Context context) {
		this.context = context;
	}

	public void saveObject(Bitmap bmp, String uniqueName) {
		objectMap.put(uniqueName, bmp);
	}

	public Bitmap loadObject(String uniqueName) {
		return objectMap.get(uniqueName);
	}

	public boolean deleteObject(String uniqueName) {
		return objectMap.remove(uniqueName) != null;
	}

	public boolean hasObject(String uniqueName) {
		return objectMap.containsKey(uniqueName);
	}
	
	public void clearImages(){
		objectMap.clear();
	}

	public void loadImages() {
		Repository<HashMap<String, byte[]>> bitmapRepo = new Repository<HashMap<String, byte[]>>(context, "BitmapStore");
		HashMap<String, byte[]> bitmapStore = bitmapRepo.loadObject();
		if (bitmapStore != null) {
			objectMap = new HashMap<String, Bitmap>();
			for (Entry<String, byte[]> entry : bitmapStore.entrySet()) {
				byte[] byteArray = entry.getValue();
				Bitmap decodeByteArray = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
				if (entry.getKey() != null && decodeByteArray != null)
					objectMap.put(entry.getKey(), decodeByteArray);
			}
		}
	}

	public void saveImages() {
		if (objectMap.size() > 0) {
			// BitmapStore bitmapStore = new BitmapStore();
			HashMap<String, byte[]> imageStoreMap = new HashMap<String, byte[]>();
			Repository<HashMap<String, byte[]>> bitmapRepo = new Repository<HashMap<String, byte[]>>(context, "BitmapStore");
			for (Entry<String, Bitmap> entry : objectMap.entrySet()) {
				if (entry.getValue() != null) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					entry.getValue().compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					imageStoreMap.put(entry.getKey(), byteArray);
				}
			}
			bitmapRepo.saveObject(imageStoreMap);
		}
	}

}
