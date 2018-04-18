package com.tripquiz.persistance;

import java.io.Serializable;

public interface SerializableRepo extends Serializable {

	void afterLoad();
}
