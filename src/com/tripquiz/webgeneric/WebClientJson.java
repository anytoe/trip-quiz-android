package com.tripquiz.webgeneric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;


public class WebClientJson {


	public String getStringFromServer(final UrlBuilder url, final boolean post) throws RemoteError {

		DefaultHttpClient client = new DefaultHttpClient();
		if (url.hasCredentials()) {
			CredentialsProvider credProvider = new BasicCredentialsProvider();
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(url.getUserName(), url.getUserPwd());
			credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
			client.setCredentialsProvider(credProvider);
		}

		HttpResponse response = null;
		short htmlStatusCode = 0;

		// request server
		String urlString = url.create();
		try {
			HttpRequestBase request = null;
			if (!post) {
				request = new HttpGet(new URI(urlString));
			} else {
				request = new HttpPost(new URI(urlString));
			}
			response = client.execute(request);
			htmlStatusCode = (short) response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			throw new RemoteError(RemoteError.NO_INTERNET_CONNECTION);
		} catch (Exception e) {
			throw new RemoteError(e, htmlStatusCode, urlString);
		}

		// result arrived, check for errors
		if (htmlStatusCode != 200) {
			String errorCode = "Error:" + htmlStatusCode;
			String errorText = response.getStatusLine().getReasonPhrase() != null ? response.getStatusLine().getReasonPhrase().equals("") + errorCode
					: errorCode;
			throw new RemoteError(errorText);
		}

		// HTML status 200: OK, try to get content as string
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null)
				total.append(line);

			return total.toString();
		} catch (Exception e) {
			throw new RemoteError(e, htmlStatusCode, urlString);
		}
	}
}
