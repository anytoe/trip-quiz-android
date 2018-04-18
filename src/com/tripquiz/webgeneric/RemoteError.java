package com.tripquiz.webgeneric;


/***
 * Wraps an exception to easen displaying and logging of errrors
 * 
 * @author Andy
 */
public class RemoteError extends RuntimeException {

	private static final long serialVersionUID = 7593912879996349090L;
	private String calledUri;

	public static String NO_INTERNET_CONNECTION = "NO_INTERNET_CONNECTION";

	public RemoteError(Exception e) {
		super(e);
	}

	public RemoteError(String message) {
		super(message);
	}

	public RemoteError(String message, short htmlcode, String calledUri) {
		super(message);
		this.calledUri = calledUri;
	}

	public RemoteError(Exception ex, short htmlcode, String calledUri) {
		super(ex.getMessage());
		this.calledUri = calledUri;
	}

	public String getUrlCall() {
		return calledUri;
	}

}
