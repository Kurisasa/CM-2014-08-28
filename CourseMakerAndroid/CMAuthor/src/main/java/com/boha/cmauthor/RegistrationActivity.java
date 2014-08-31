package com.boha.cmauthor;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

public class RegistrationActivity extends BaseRegistration {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		ctx = getApplicationContext();
		setVolley();
		setFields();
		checkVirginity(BaseRegistration.AUTHOR);
	}

	@Override
	public void startMain() {
		Intent i = new Intent(getApplicationContext(),
				CategoryActivity.class);
		i.putExtra("response", response);
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
//		type = RequestDTO.LOGIN_AUTHOR;
//		try {
//			getRemoteData(type, Statics.SERVLET_AUTHOR);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.LOGIN_AUTHOR);
        w.setEmail(email);
        w.setPassword(password);

        WebSocketUtil.sendRequest(ctx, Statics.AUTHOR_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        SharedUtil.saveCompany(ctx, response.getCompany());
                        SharedUtil.saveAuthor(ctx, response.getAuthor());
                        sendDeviceToServer(BaseRegistration.AUTHOR, response.getAuthor().getAuthorID());
                        startMain();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.more_information:
			Intent i = new Intent(getApplicationContext(),
					MoreInfoActivity.class);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	Menu mMenu;
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.more_information);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.registration, menu);
		mMenu = menu;
		return true;
	}

	SharedPreferences sp;
	int type;

	@Override
	public void processRemoteResponse() {
		
		if (response != null) {
			if (response.getStatusCode() == 0) {
				startMain();
			} else {
				ToastUtil.errorToast(ctx, response.getMessage());
			}
		}
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
}
