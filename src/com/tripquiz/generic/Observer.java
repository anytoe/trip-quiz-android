package com.tripquiz.generic;

import java.util.ArrayList;
import java.util.List;

public class Observer<T> {

	protected List<T> listeners;

	protected Observer() {
		listeners = new ArrayList<T>();
	}

	protected Observer(List<T> listeners) {
		this.listeners = listeners;
	}

	public boolean addListener(T listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(T listener) {
		return listeners.remove(listener);
	}
	
	public void removeListenerAll(){
		listeners = new ArrayList<T>();
	}

}
