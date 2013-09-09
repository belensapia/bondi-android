package com.bondi_android.util;

import java.util.UUID;

import android.content.Context;
import android.content.ContextWrapper;
import android.telephony.TelephonyManager;

/**
 * 
 * @author Marcos
 *
 */
public class BondiUtils {

	/**
	 * Returns a unique identifier for the device. For security measures the device id
	 * is hashed. 
	 * @param context context from which to take the device id.
	 * @return a unique string representing the device.
	 */
	public static String getDeviceID(final ContextWrapper context) {
	    final TelephonyManager tm = (TelephonyManager) context.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}
}
