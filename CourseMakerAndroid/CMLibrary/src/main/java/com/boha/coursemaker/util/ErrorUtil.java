package com.boha.coursemaker.util;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;

public class ErrorUtil {

	static final String LOG = "ErrorUtil";
	public static void logVolleyError(Context ctx, VolleyError error) {
		if (error instanceof TimeoutError) {
			Log.e(LOG, "******** Volley: TimeoutError");
		}
		if (error instanceof NetworkError) {
			Log.e(LOG, "******** Volley: NetworkError");
		}
		if (error instanceof ParseError) {
			Log.e(LOG, "******** Volley: ParseError");
		}
		if (error instanceof ServerError) {
			Log.e(LOG, "******** Volley: ServerError");
		}
		
	}
	public static void handleErrors(Context ctx, int errCode) {
		switch (errCode) {
		case Constants.ERROR_DATABASE:
			ToastUtil
					.errorToast(ctx,
							"Database error. Contact CourseMaker support on the web site");
			break;
		case Constants.ERROR_LOCAL_DATABASE:
			ToastUtil
					.errorToast(ctx,
							"Local Database error. Contact CourseMaker support on the web site");
			break;
		case Constants.ERROR_NETWORK_UNAVAILABLE:
			ToastUtil.errorToast(ctx,
					"Network unavailable. Check settings and signal strength");
			break;
		case Constants.ERROR_ENCODING:
			ToastUtil.errorToast(ctx,
					"Error encoding request. Contact CourseMaker support");
			break;
		case Constants.ERROR_SERVER_COMMS:
			ToastUtil
					.errorToast(ctx,
							"Problem communicating with the server. Please contact CourseMaker support");
			break;
		case Constants.ERROR_DUPLICATE:
			ToastUtil.errorToast(ctx,
					"Attempting to put duplicate data in database, ignored");
			break;
		default:
			break;
		}
	}
}
