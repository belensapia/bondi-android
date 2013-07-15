package com.bondi_android.tracking;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bondi_android.R;
import com.bondi_android.checkin.CheckinActivity;
import com.bondi_android.util.BondiConstants;
import com.bondi_android.util.ErrorDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class TrackingLineActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private TextView text;
	private LocationClient mLocationClient;
	// Define an object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking_line);

		// Add selected line to the text.
		text = (TextView) findViewById(R.id.trackingText);
		Intent intent = this.getIntent();
		String selectedLine = intent
				.getStringExtra(CheckinActivity.SELECTED_LINE);
		text.setText(text.getText() + selectedLine);

		// Set updates rate parameters
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(BondiConstants.UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(BondiConstants.FASTEST_INTERVAL);

		// Start the location service.
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracking_line, menu);
		return true;
	}

	public void stopTracking(View view) {
		// TODO Stop tracking
		finish();
	}

	/**
	 * When it couldn't connect to the service location.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	protected void onDestroy() {
		if (mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
		mLocationClient.disconnect();
		Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	/**
	 * Called by Location Services when the request to connect the client
	 * finishes successfully.
	 */
	@Override
	public void onConnected(Bundle bundle) {
		// Display the connection status
		// TODO is this necessary? No, the toast its just a pop up msg.
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

		// start tracking
		if (isServicesConnected()) {
//			currentLocation = mLocationClient.getLastLocation();
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}

	}

	/**
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				// Try the request again
				mLocationClient.connect();
				break;
			}

		}
	}

	private boolean isServicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			showErrorDialog(resultCode);
			return false;
		}
	}

	private void showErrorDialog(int errorCode) {
		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			// Create a new DialogFragment for the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(getSupportFragmentManager(), "Location Updates");
		}
	}

	// Define the callback method that receives location updates
	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		//TODO Is not getting updates. Why?!
		String msg = "Updated Location: "
				+ Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
		/* TODO
		 * OBS: If your app accesses the network or does other long-running work
		 * after receiving a location update, adjust the fastest interval to a
		 * slower value. This prevents your app from receiving updates it can't
		 * use. Once the long-running work is done, set the fastest interval
		 * back to a fast value.
		 */
	}
}
