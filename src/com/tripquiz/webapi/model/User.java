package com.tripquiz.webapi.model;

import java.util.Date;

import com.tripquiz.persistance.SerializableRepo;

public class User implements SerializableRepo {

	private static final long serialVersionUID = 8999427316085480356L;
	
	private EntityKey key;
	private String email;
	private String username;
	private String password;
	private String token;
	private long expiryDateMs;

	public User(String email, String username, String password) {
		super();
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public User(String identifier, String password) {
		super();
		this.email = identifier;
		this.username = identifier;
		this.password = password;
	}

	public void updateCredentials(String key, String token, long expiryDateMs) {
		this.key = new EntityKey(key, User.class);
		this.token = token;
		this.expiryDateMs = expiryDateMs;
	}

	public EntityKey getKey() {
		return key;
	}

	public String getEmail() {
		return email;
	}

	public String getUserName() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getToken() {
		return token;
	}

	public String getIdentifier() {
		return (username != null && !username.equals("")) ? username : email;
	}

	public boolean hasValidToken() {
		return (new Date()).getTime() < expiryDateMs;
	}

	@Override
	public void afterLoad() {
		// nothing to do
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (int) (expiryDateMs ^ (expiryDateMs >>> 32));
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (expiryDateMs != other.expiryDateMs)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
