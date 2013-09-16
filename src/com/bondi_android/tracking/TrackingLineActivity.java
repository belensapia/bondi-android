package com.bondi_android.tracking;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bondi_android.R;
import com.bondi_android.checkin.CheckinActivity;
import com.bondi_android.util.BondiConstants;
import com.bondi_android.util.BondiUtils;
import com.bondi_android.util.TrackingReminder;

public class TrackingLineActivity extends Activity implements LocationListener {
	// TODO Buscar pa que sea infinito el tiempo
	private static final int TIME_UPDATE_RATE = 100;// 0 * 60 * 2; // in
													// milliseconds
	private static final float DISTANCE_UPDATE_RATE = 100; // in meters

	private static final String LINE_SERVICE_MAP_KEY = "bus";
	private static final String LONGITUDE_SERVICE_MAP_KEY = "longitude";
	private static final String LATITUDE_SERVICE_MAP_KEY = "latitude";

	private static boolean activityVisible;

	private LocationManager locationManager;
	private Location currentLocation;
	private TextView text;
	private String selectedLine;
	private Handler reminderHandler;
	private TrackingReminder reminderRunnable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking_line);

		// Add selected line to the text.
		text = (TextView) findViewById(R.id.trackingText);
		Intent intent = this.getIntent();
		selectedLine = intent.getStringExtra(CheckinActivity.SELECTED_LINE);
		text.setText(text.getText() + selectedLine);

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		this.reminderHandler = new Handler();
		this.reminderRunnable = new TrackingReminder(this);

		BondiUtils.checkGPSStatus(this);
		
		registerNewBestProvider(null);
	}

	private void registerNewBestProvider(String oldProvider) {
		Criteria criteria = new Criteria();
		// Getting the name of the provider that meets the criteria
		String newProvider = locationManager.getBestProvider(criteria, false);

		if (newProvider != null && !newProvider.equals("")
				&& !newProvider.equals(oldProvider)) {
			if (oldProvider != null && locationManager != null) {
				locationManager.removeUpdates(this);
			}

			// Get the location from the given provider
			currentLocation = locationManager.getLastKnownLocation(newProvider);

			locationManager.requestLocationUpdates(newProvider,
					TIME_UPDATE_RATE, DISTANCE_UPDATE_RATE, this);

			if (currentLocation != null)
				onLocationChanged(currentLocation);
			else
				Toast.makeText(getBaseContext(), "No location was cached",
						Toast.LENGTH_SHORT).show();
			return;
		}

		// If here, there's no new provider.
		if (oldProvider == null) {
			Toast.makeText(getBaseContext(), "No Provider Found",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
		Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (BondiUtils.isBetterLocation(location, currentLocation)) {
			// Report to the UI that the location was updated
			String msg = "Updated Location: "
					+ Double.toString(location.getLatitude()) + ","
					+ Double.toString(location.getLongitude());
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

			// The new location is sent to the AsyncConnection to be send
			// to the server
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(LINE_SERVICE_MAP_KEY, this.selectedLine);
			parameters.put(LATITUDE_SERVICE_MAP_KEY,
					location.getLatitude());
			parameters.put(LONGITUDE_SERVICE_MAP_KEY,
					location.getLongitude());

			PostAsyncBondiConnection.executePostService(
					BondiConstants.TRACKING_SERVICE_URL, parameters, this);

			// Its to remind the user to close the app if its out of the bus.
			final int delay = 30*60*1000;//30 minutes
			// First remove any pending reminder to avoid that the reminder gets
			// call twice
			reminderHandler.removeCallbacks(reminderRunnable);
			reminderHandler.postDelayed(reminderRunnable, delay);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tracking_line, menu);
		return true;
	}

	public void stopTracking(View view) {
		// The finish calls #onDestroy which removes the listener from the
		// locationManager
		finish();
	}

	public static boolean isActivityVisible() {
		return activityVisible;
	}

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		TrackingLineActivity.activityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		TrackingLineActivity.activityPaused();
	}

	@Override
	public void onProviderDisabled(String provider) {
		registerNewBestProvider(provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Do nothing
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Do nothing
	}
}