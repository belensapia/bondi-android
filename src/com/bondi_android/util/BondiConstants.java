package com.bondi_android.util;

public class BondiConstants {

	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	public static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	
	public static final String REST_USER = "bondi";
	public static final String REST_PASSWORD = "bondi";
	public static final String TRACKING_SERVICE_URL = "http://192.168.1.110:8080/tracking";
	
}
