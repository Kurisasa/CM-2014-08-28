package com.boha.coursemaker.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WebCheck {
	static ConnectivityManager connectivity;

	public static void startWirelessSettings(Context ctx) {
		Intent i = new Intent(
				android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		ctx.startActivity(i);
	}
	public static WebCheckResult checkNetworkAvailabilityNoLog(Context ctx) {
		// ARE WE CONNECTED TO THE NET
		long start = System.currentTimeMillis();
		WebCheckResult result = new WebCheckResult();
		try {
			if (connectivity == null) {
				connectivity = (ConnectivityManager) ctx
						.getSystemService(Context.CONNECTIVITY_SERVICE);
			}

			NetworkInfo wifiInfo = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileInfo = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (wifiInfo.isAvailable()) {
				result.setWifiAvailable(true);
			} else {
				result.setWifiAvailable(false);
			}

			if (wifiInfo.isConnected()) {
				result.setWifiConnected(true);
			} else {
				result.setWifiConnected(false);
			}
			//
			if (mobileInfo.isAvailable()) {
				result.setMobileAvailable(true);
			} else {
				result.setMobileAvailable(false);
			}
			if (mobileInfo.isConnected()) {
				result.setMobileConnected(true);
			} else {
				result.setMobileConnected(false);
			}
			if (!result.isWifiConnected() && !result.isMobileConnected()) {
				result.setNetworkUnavailable(true);
			}
		} catch (Exception e) {
			Log.e(TAG, "Something wrong in check", e);
		}
		long end = System.currentTimeMillis();
		Log.d(TAG, "###### Network check completed in " + (end - start) + " ms");
		return result;

	}
	/**
	 * Quick check of network availability
	 * @param ctx
	 * @return
	 */
	public static WebCheckResult checkNetworkAvailability(Context ctx) {
		// ARE WE CONNECTED TO THE NET
		long start = System.currentTimeMillis();
		WebCheckResult result = new WebCheckResult();
		try {
			if (connectivity == null) {
				connectivity = (ConnectivityManager) ctx
						.getSystemService(Context.CONNECTIVITY_SERVICE);
			}

			NetworkInfo wifiInfo = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileInfo = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (wifiInfo.isAvailable()) {
				Log.i(TAG, "###### WIFI AVAILABLE on check");
				result.setWifiAvailable(true);
			} else {
				Log.i(TAG, "###### WIFI NOT AVAILABLE on check");
				result.setWifiAvailable(false);
			}

			if (wifiInfo.isConnected()) {
				Log.i(TAG, "###### WIFI CONNECTED on check");
				result.setWifiConnected(true);
			} else {
				Log.e(TAG, "@@@ WIFI NOT CONNECTED on check");
				result.setWifiConnected(false);
			}
			//
			if (mobileInfo.isAvailable()) {
				Log.i(TAG, "###### MOBILE_NETWORK AVAILABLE on check");
				result.setMobileAvailable(true);
			} else {
				Log.e(TAG, "@@@ MOBILE_NETWORK NOT AVAILABLE on check");
				result.setMobileAvailable(false);
			}
			if (mobileInfo.isConnected()) {
				Log.i(TAG, "###### MOBILE_NETWORK CONNECTED on check");
				result.setMobileConnected(true);
			} else {
				Log.e(TAG, "@@@ MOBILE_NETWORK NOT CONNECTED on check");
				result.setMobileConnected(false);
			}
			if (!result.isWifiConnected() && !result.isMobileConnected()) {
				result.setNetworkUnavailable(true);
			}
		} catch (Exception e) {
			Log.e(TAG, "oops! Something wrong in check", e);
		}
		long end = System.currentTimeMillis();
		Log.d(TAG, "###### Network check completed in " + (end - start) + " ms");
		return result;

	}

	public static final int WIFI_AVAILABLE_CONNECTED = 1;
	//
	public static final int MOBILE_NETWORK_AVAILABLE = 2;
	public static final int BOTH_NETWORKS_AVAILABLE = 3;
	//
	public static final int NETWORK_NOT_AVAILABLE = 5;
	public static final String TAG = "WebCheck";
}
