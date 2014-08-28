package com.boha.coursemaker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.boha.coursemaker.dto.ResponseDTO;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GCMRegistrar {

	public GCMRegistrar() {
		// TODO Auto-generated constructor stub
	}

	public static String getRegistrationID(Context ctx) {
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		String regID = sp.getString(Constants.GCM_REGISTRATION_ID, null);
		if (regID == null) {
			Log.w(TAG, "GCM Registration ID not found");
			return null;
		}
		int registeredVersion = sp.getInt(Constants.APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(ctx);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return null;
		}
		return regID;
	}

	public static void setRegistrationID(Context ctx, String registrationID) {
		sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putString(Constants.GCM_REGISTRATION_ID, registrationID);
		ed.putInt(Constants.APP_VERSION, getAppVersion(ctx));
		ed.commit();
	}

	static GoogleCloudMessaging gcm;
	static Context ctx;
	static final String SENDER_ID = "AIzaSyCJIUMPXsL-GVAfNAl1i-fDy6qf7g5TtCU";
	static String registrationID;
	static ResponseDTO response;
	static int type;
	static Integer ID;
	
	public static void registerDevice(Context context, int actorType) {
		type = actorType;
		ctx = context;
		new RegTask().execute();
	}

	static class RegTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(ctx);
				}
				registrationID = gcm.register(SENDER_ID);
				if (registrationID != null && !registrationID.isEmpty()) {
					setRegistrationID(ctx, registrationID);
					Log.i(TAG, "Device registered, registration ID="
							+ registrationID);
					//response = AdminUtil.registerGCM(ctx, registrationID, ID,
					//		type);
				}
			} catch (UnsupportedEncodingException e) {
				return Constants.ERROR_ENCODING;
			
			} catch (IOException e) {
				return Constants.ERROR_SERVER_COMMS;
			}

			return 0;
		}
		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				ErrorUtil.handleErrors(ctx, result);
				return;
			}
			if (response.getStatusCode() > 0) {
				ToastUtil.errorToast(ctx, response.getMessage());
				return;
			}
		}

	}

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

	static SharedPreferences sp;
	static final String TAG = "GCMRegistra";
}
