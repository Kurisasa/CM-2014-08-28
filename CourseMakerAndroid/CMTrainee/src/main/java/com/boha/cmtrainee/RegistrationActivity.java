package com.boha.cmtrainee;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.boha.coursemaker.base.BaseRegistration;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.io.UnsupportedEncodingException;

public class RegistrationActivity extends BaseRegistration {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		ctx = getApplicationContext();
		
		//TODO - remove before app store submission
		//GCMRegistrar.checkManifest(this);
		setFields();
		setVolley();
		checkVirginity(BaseRegistration.TRAINEE);
	}

	
	@Override
	public void startMain() {
		Intent i = new Intent(getApplicationContext(), MainPagerActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onPause() {
		Log.i("REG", "-- onPause ---");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}



	@Override
	public void processSignIn() {
		if (editPassword.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(),
					getApplicationContext().getResources().getString(R.string.enter_password));
			return;
		}
		password = editPassword.getText().toString();
//		type = RequestDTO.LOGIN_TRAINEE;
//		try {
//			getRemoteData(type, Statics.SERVLET_TRAINEE);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LOGIN_TRAINEE);
        w.setEmail(email);
        w.setPassword(password);
        w.setGcmDevice(gcmDevice);

        WebSocketUtil.sendRequest(ctx,Statics.TRAINEE_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        SharedUtil.saveTrainee(ctx, response.getTrainee());
                        SharedUtil.saveCompany(ctx,response.getCompany());
                        SharedUtil.saveTrainingClass(ctx,response.getTrainingClass());
                        CacheUtil.cacheData(ctx,response,CacheUtil.CACHE_TRAINEE_ACTIVITY,new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });
                        sendDeviceToServer(BaseRegistration.TRAINEE,response.getTrainee().getTraineeID());
                        startMain();
                    }
                });

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {

            }
        });
	}



	@Override
	public void setFields() {
		emailSpinner = (Spinner) findViewById(R.id.REG_spinner);		
		editPassword = (EditText) findViewById(R.id.REG_password);
		btnSignIn = (Button) findViewById(R.id.REG_btnSignIn);

		btnSignIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				processSignIn();
			}
		});
		
	}
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_help);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.action_bar_progess);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_help:
			return true;
		case R.id.menu_refresh:
			try {
				getRemoteData(type, Statics.SERVLET_TRAINEE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.registration, menu);
		mMenu = menu;
		return true;
	}


	@Override
	public void processRemoteResponse() {
		Log.i(LOG, "processRemoteResponse - startMain comin up!");
		startMain();
	}


	@Override
	public void setBusy() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			setRefreshActionButtonState(true);
		}
		
	}


	@Override
	public void setNotBusy() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			setRefreshActionButtonState(false);
		}
		
	}
	Menu mMenu;
	static final String LOG = "RegistrationAct trainee";
	
}