package com.boha.coursemaker.util;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class CheckGooglePlayServices {

	public CheckGooglePlayServices() {
		// TODO Auto-generated constructor stub
	}
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static boolean checkPlayServices(Activity activity) {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("CheckGoogle", "This device is not supported.");
	        }
	        return false;
	    }
	    return true;
	}
}
