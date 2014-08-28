package com.boha.cmadmin.fragments;

import android.content.Context;
import android.util.Log;

import com.boha.cmadmin.listeners.PasswordRequestListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.WebSocketUtil;

public class PasswordRequestUtil {

	public static void sendTraineePasswordRequest(int id, Context ctx, BusyListener busyListener, PasswordRequestListener listener) {
		 sendPassword(id, TRAINEE, ctx, busyListener, listener);
	}
	public static void sendInstructorPasswordRequest(int id, Context ctx, BusyListener busyListener, PasswordRequestListener listener) {
		 sendPassword(id, INSTRUCTOR, ctx, busyListener, listener);
	}
	public static void sendAuthorPasswordRequest(int id, Context ctx, BusyListener busyListener, PasswordRequestListener listener) {
		 sendPassword(id, AUTHOR, ctx, busyListener, listener);
	}
	public static void sendAdminPasswordRequest(int id, Context ctx, BusyListener busyListener, PasswordRequestListener listener) {
		 sendPassword(id, ADMINISTRATOR, ctx, busyListener, listener);
	}
	private static void sendPassword(int id, int type, final Context ctx, final BusyListener busyListener, final PasswordRequestListener listener) {
		RequestDTO req = new RequestDTO();		
		switch (type) {
		case TRAINEE:
			req.setRequestType(RequestDTO.SEND_PASSWORD_TRAINEE);
			req.setTraineeID(id);
			break;
		case ADMINISTRATOR:
			req.setRequestType(RequestDTO.SEND_PASSWORD_ADMIN);
			req.setAdministratorID(id);
			break;
		case INSTRUCTOR:
			req.setRequestType(RequestDTO.SEND_PASSWORD_INSTRUCTOR);
			req.setInstructorID(id);
			break;
		case AUTHOR:
			req.setRequestType(RequestDTO.SEND_PASSWORD_AUTHOR);
			req.setAuthorID(id);
			break;
		case EXECUTIVE:
			
			break;

		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                busyListener.setNotBusy();
                if (response.getStatusCode() > 0) {
                    Log.e("PasswordRequest",response.getMessage());
                    return;
                }
                //ToastUtil.toast(ctx, ctx.getResources().getString(R.string.pswd_request_sent));
                listener.onPasswordReturned(response.getCredential());
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {

            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx, new BaseVolley.BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				busyListener.setNotBusy();
//				ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO response) {
//				busyListener.setNotBusy();
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, response.getMessage());
//					return;
//				}
//				//ToastUtil.toast(ctx, ctx.getResources().getString(R.string.pswd_request_sent));
//				listener.onPasswordReturned(response.getCredential());
//
//			}
//		});
	}
	
	
	public static final int TRAINEE = 1, AUTHOR = 2, INSTRUCTOR = 3, ADMINISTRATOR = 4,
    EXECUTIVE = 5;
}
