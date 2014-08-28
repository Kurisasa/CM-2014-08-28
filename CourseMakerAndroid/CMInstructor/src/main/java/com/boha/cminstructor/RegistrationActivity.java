package com.boha.cminstructor;

import java.io.UnsupportedEncodingException;

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
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.google.android.gcm.GCMRegistrar;

/**
 * Activity to manage the initial login for an Instructor.
 *
 * @see com.boha.coursemaker.dto.InstructorDTO
 * @author aubreyM
 */
public class RegistrationActivity extends BaseRegistration {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_registration);
		ctx = getApplicationContext();
		GCMRegistrar.checkManifest(this);
		setFields();
		setVolley();
		
		//LocaleUtil.setLocale(this, LocaleUtil.AFRIKAANS);
		checkVirginity(BaseRegistration.INSTRUCTOR);
	}

	@Override
	public void startMain() {
		Intent i = new Intent(getApplicationContext(), MainPagerActivity.class);
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
					ctx.getResources().getString(R.string.enter_password));
			return;
		}
		password = editPassword.getText().toString();
		type = RequestDTO.LOGIN_INSTRUCTOR;
		
		try {
			getRemoteData(type, Statics.SERVLET_INSTRUCTOR);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void setFields() {
		emailSpinner = (Spinner) findViewById(R.id.REG_spinner);
		
		editPassword = (EditText) findViewById(R.id.REG_password);
		//editCellphone = (EditText) findViewById(R.id.REG_cellphone);

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
				getRemoteData(type, Statics.SERVLET_INSTRUCTOR);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	Menu mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.registration, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public void processRemoteResponse() {	
		Log.d(LOG, "#### processRemoteResponse - starting Main");
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
	static final String LOG = "RegistrationActivity Instructor";
}