package com.bondi_android.tracking;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.bondi_android.util.BondiConstants;
import com.bondi_android.util.BondiUtils;

public class PostAsyncBondiConnection {

	// This is going to make it run in the background
	private static class LongRunningGetIO extends
			AsyncTask<String, Void, JSONObject> {

		private Map<String, Object> parameters;

		// Who called the longRunning
		private ContextWrapper context;

		public LongRunningGetIO(Map<String, Object> params, ContextWrapper context) {
			if (params == null) {
				// parameters can't be null
				params = new HashMap<String, Object>();
			}
			this.parameters = params;
			this.context = context;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		protected JSONObject doInBackground(String... urls) {
			disableConnectionReuseIfNecessary();

			if (urls == null || urls.length == 0) {
				// Don't have a url to call to.
				return null;
			}

			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPut httput = new HttpPut(urls[0]);
				JSONObject holder = new JSONObject();

				// Encode user and pass.
				holder.put("user",
						URLEncoder.encode(BondiConstants.REST_USER, "UTF-8"));
				holder.put("password", URLEncoder.encode(
						BondiConstants.REST_PASSWORD, "UTF-8"));
				holder.put("deviceId", BondiUtils.getDeviceID(this.context));

				for (String key : parameters.keySet()) {
					holder.put(key, parameters.get(key));
				}

				StringEntity se = new StringEntity(holder.toString());
				httput.setEntity(se);
				httput.setHeader("Accept", "application/json");
				httput.setHeader("Content-type", "application/json");

				ResponseHandler responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httput, responseHandler);
				return new JSONObject(response);

			} catch (ClientProtocolException e) {
				// Do nothing
			} catch (IOException e) {
				// Do nothing
			} catch (JSONException e) {
				// Do nothing
			}

			return null;
		}

		@Override
		protected void onPostExecute(JSONObject results) {
			// Do something after the execution
			if (results != null) {
				Toast.makeText(context, results.toString(), Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public static void executePostService(String serviceUrl,
			Map<String, Object> parameters, ContextWrapper context) {
		// Execute in the background
		new LongRunningGetIO(parameters, context).execute(serviceUrl);
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

}
