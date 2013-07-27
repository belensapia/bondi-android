package com.bondi_android.tracking;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;

import com.bondi_android.util.BondiConstants;

public class PostAsyncBondiConnection {

	private static final int CONNECTION_TIMEOUT = 10000;
	private static final int DATARETRIEVAL_TIMEOUT = 10000;

	//This is going to make it run in the background
	private static class LongRunningGetIO extends
			AsyncTask<URL, Void, JSONObject> {
		
		private Map<String, String> parameters;
		
		public LongRunningGetIO(Map<String, String> params) {
			if (params==null) {
				//parameters can't be null
				params = new HashMap<String, String>();
			}
			this.parameters = params;
		}
		
		@Override
		protected JSONObject doInBackground(URL... urls) {
			disableConnectionReuseIfNecessary();

			if (urls == null || urls.length == 0) {
				// Don't have a url to call to.
				return null;
			}

			HttpURLConnection urlConnection = null;
			try {
				// create connection
				urlConnection = (HttpURLConnection) urls[0].openConnection();

				// Encode
				String param = "user=" + URLEncoder.encode(BondiConstants.REST_USER, "UTF-8")
						+ "&password=" + URLEncoder.encode(BondiConstants.REST_PASSWORD, "UTF-8");
				
				//Add all the parameters
				for (String key : parameters.keySet()) {
					param.concat("&"+key+"="+parameters.get(key));
				}

				urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
				urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

				// set the output to true, indicating you are
				// outputting(uploading) POST data
				urlConnection.setDoOutput(true);
				// once you set the output to true, you don't really need to set
				// the request method to post, but I'm doing it anyway
				urlConnection.setRequestMethod("POST");

				urlConnection
						.setFixedLengthStreamingMode(param.getBytes().length);
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// send the POST out
				PrintWriter out = new PrintWriter(
						urlConnection.getOutputStream());
				out.print(param);
				out.close();

				// create JSON object from content
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				return new JSONObject(getResponseText(in));

			} catch (SocketTimeoutException e) {
				// data retrieval or connection timed out
			} catch (IOException e) {
				// could not read response body
				// (could not create input stream)
			} catch (JSONException e) {
				// The response couldn't be transformed to json.
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(JSONObject results) {
			// Do something after the execution
		}
	}

	public static JSONObject executePostService(String serviceUrl, Map<String, String> parameters) {
		try {
			URL url = new URL(serviceUrl);
			AsyncTask<URL, Void, JSONObject> result = new LongRunningGetIO(parameters)
					.execute(url);
			return result.get();
		} catch (MalformedURLException e) {
			// Do nothing
		} catch (InterruptedException e) {
			// Do nothing
		} catch (ExecutionException e) {
			// Do nothing
		}
		// The service couldn't be executed.
		return null;
	}

	/**
	 * required in order to prevent issues in earlier Android version.
	 */
	private static void disableConnectionReuseIfNecessary() {
		// see HttpURLConnection API doc
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	private static String getResponseText(InputStream inStream) {
		// very nice trick from
		// http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
		return new Scanner(inStream).useDelimiter("\\A").next();
	}
}
