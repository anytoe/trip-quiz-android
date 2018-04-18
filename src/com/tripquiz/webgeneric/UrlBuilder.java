package com.tripquiz.webgeneric;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlBuilder {

	private String protocol;
	private String host;
	private String userName;

	private String userPwd;
	private StringBuilder uriPath = null;
	private StringBuilder parameterPath = null;
	private boolean hasParameter = false;

	public UrlBuilder(String protocol, String host) {
		this.protocol = protocol;
		uriPath = new StringBuilder();
		parameterPath = new StringBuilder();
		this.host = host;
	}

	public UrlBuilder(String protocol, String host, String userName, String userPwd) {
		this(protocol, host);
		this.userName = userName;
		this.userPwd = userPwd;
	}

	public UrlBuilder addUrlPath(String uripath) {
		uriPath.append(uripath);
		return this;
	}

	public UrlBuilder addParameter(String parameter, String paramValue, boolean encode) {
		String value = paramValue;
		if (encode) {
			try {
				if(paramValue == null)
					paramValue = "";	
				value = URLEncoder.encode(paramValue, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (!hasParameter) {
			parameterPath.append("?");
			hasParameter = true;
		} else {
			parameterPath.append("&");
		}
		parameterPath.append(parameter + "=" + value);
		return this;
	}

	public UrlBuilder addParameter(String parameter, String paramValue) {
		return addParameter(parameter, paramValue, false);
	}

	public String create() {
		return buildProtocol()+ buildUserName() + host + uriPath.toString() + parameterPath.toString();
	}

	public void resetPath() {
		uriPath = new StringBuilder();
	}

	public void resetParameter() {
		parameterPath = new StringBuilder();
	}

	public void resetAll() {
		uriPath = new StringBuilder();
		parameterPath = new StringBuilder();
	}

	private String buildProtocol() {
		return protocol == null ? "" : protocol + "://";
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public boolean hasCredentials() {
		return userPwd != null || userName != null;
	}

	private String buildUserName() {
		if(hasCredentials()){
			StringBuilder builder = new StringBuilder();		
			builder.append(userName);
			builder.append(":");
			builder.append(userPwd);
			builder.append("@");
			return builder.toString();
		}

		return "";
	}
}
