package com.tripquiz.webapi.model;

import java.io.Serializable;

public class EntityKey implements Serializable {

	private static final long serialVersionUID = -3144467252631379831L;
	private String key;
	private String type;

	public EntityKey(String key, Class<?> type) {
		super();
		this.key = key;
		this.type = type.toString();
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// if (getClass() != obj.getClass())
		// return false;
		EntityKey other = (EntityKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (getType() == null) {
			if (other.getType() != null)
				return false;
		} else if (!getType().equals(other.getType()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return key;
	}

}