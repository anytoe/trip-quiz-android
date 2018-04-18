package com.tripquiz.persistance;

import java.io.Serializable;
import java.util.HashMap;

public class BitmapStore implements Serializable {

	private static final long serialVersionUID = 1081215989508151969L;
	
	public HashMap<String, byte[]> imageStoreMap = new HashMap<String, byte[]>();

	public BitmapStore() {

	}

}
