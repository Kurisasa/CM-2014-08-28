package com.boha.coursemaker.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.boha.coursemaker.listeners.GCMUtilListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class GCMUtil {
	static Context ctx;
	static GCMUtilListener gcmUtilListener;
	static String registrationID, msg;
	static final String LOG = "GCMUtil";
	static GoogleCloudMessaging gcm;

	public static void startGCMRegistration(Context context, GCMUtilListener listener) {
		ctx = context;
		gcmUtilListener = listener;
		new GCMTask().execute();
	}
	static class GCMTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			Log.e(LOG, "... startin GCM registration");
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(ctx);
				}
				registrationID = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID = \n" + registrationID;
				storeRegistrationId(ctx, registrationID);
				Log.i(LOG, msg);
				
			} catch (IOException e) {
				return Constants.ERROR_SERVER_COMMS;
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i(LOG, "... ending GCM registration");
			if (result > 0) {
				gcmUtilListener.onGCMError();
				ErrorUtil.handleErrors(ctx, result);
				return;
			}
			gcmUtilListener.onDeviceRegistered(registrationID);
		}

	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		int appVersion = getAppVersion(context);
		Log.i(LOG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(GCM_REGISTRATION_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
		Log.e(LOG, "GCM regId saved in prefs! Yebo!!!");
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public static String getRegistrationId(Context context) {
		ctx = context;
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String registrationId = prefs.getString(GCM_REGISTRATION_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(LOG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(LOG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static boolean checkPlayServices(Context ctx, Activity act) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(ctx);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, act,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(LOG, "This device is not supported.");
				return false;
			}
			return false;
		}
		return true;
	}
	public static final String GCM_REGISTRATION_ID = "gcmRegID";
	public static final String APP_VERSION = "appVersion";
	public static final String SENDER_ID = "624485365788";
	static final int PLAY_SERVICES_RESOLUTION_REQUEST = 11;

}
