package com.boha.cmadmin;
import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.boha.coursemaker.base.BaseRegistration;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;

public class RegistrationActivity extends BaseRegistration {

	LinearLayout loginLayout, newLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.greeting);
		ctx = getApplicationContext();
		setVolley();
		setFields();		
		checkVirginity(BaseRegistration.ADMINISTRATOR);
	}

	public void startMain() {
		
		Intent i = new Intent(getApplicationContext(), MainPagerActivity.class);
		i.putExtra("response", response);
		startActivity(i);
		finish();
	}

	public void processSignUp() {
		Log.i(LOG, "processSignUp");
		if (editCompanyName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(), ctx.getResources().getString(R.string.enter_company));
			return;
		}
		if (editFirstName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(),  ctx.getResources().getString(R.string.enter_firstname));
			return;
		}
		if (editLastName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(),  ctx.getResources().getString(R.string.enter_surname));
			return;
		}
		if (editPassword.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(),
					 ctx.getResources().getString(R.string.enter_password));
			return;
		}
		if (editCellphone.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(),
					 ctx.getResources().getString(R.string.enter_cell));
			return;
		}
		if (city == null) {
			ToastUtil.errorToast(getApplicationContext(),
					"Please select nearest city");
			return;
		}
		
		//check provinces
		if (provinceList == null || provinceList.size() == 0) {
			getCountryList();
			ToastUtil.toast(ctx, ctx.getResources().getString(R.string.province_data_loading));
			return;
		}
		company = new CompanyDTO();
		company.setCompanyName(editCompanyName.getText().toString());
		company.setEmail(email);
		company.setCity(city);

		administrator = new AdministratorDTO();
		administrator.setEmail(email);
		administrator.setFirstName(editFirstName.getText().toString());
		administrator.setLastName(editLastName.getText().toString());
		administrator.setPassword(editPassword.getText().toString());
		administrator.setCellphone(editCellphone.getText().toString());

		type = RequestDTO.REGISTER_TRAINING_COMPANY;
		try {
			btnNew.setVisibility(View.GONE);
			Log.i(LOG, "...about to getRemoteData");
			getRemoteData(type, Statics.SERVLET_ADMIN);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		

	}

	@Override
	public void processSignIn() {
		if (editPassword.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getApplicationContext(),
					ctx.getResources().getString(R.string.enter_password));
			return;
		}
		password = editPassword.getText().toString();
		type = RequestDTO.LOGIN_ADMINISTRATOR;

		try {
			btnLogin.setVisibility(View.GONE);
			getRemoteData(type, Statics.SERVLET_ADMIN);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	Button btnNew, btnLogin;
	
	

	@Override
	public void setFields() {
		//
		loginLayout = (LinearLayout) findViewById(R.id.REG_loginLayout);
		newLayout = (LinearLayout) findViewById(R.id.REG_newCompanyLayout);

		progress = (ProgressBar) findViewById(R.id.REG_progress);
		emailSpinner = (Spinner) findViewById(R.id.REG_spinner);
		provinceSpinner = (Spinner) findViewById(R.id.REG_spinnerProvince);
		citySpinner = (Spinner) findViewById(R.id.REG_spinnerCity);
		editCompanyName = (EditText) findViewById(R.id.REG_companyName);
		editFirstName = (EditText) findViewById(R.id.REG_firstName);
		editLastName = (EditText) findViewById(R.id.REG_lastName);
		editPassword = (EditText) findViewById(R.id.REG_password);
		editCellphone = (EditText) findViewById(R.id.REG_cellphone);
		btnSignIn = (Button) findViewById(R.id.REG_btnSignIn);
		btnSignUp = (Button) findViewById(R.id.REG_btnSignUp);
		btnNew = (Button) findViewById(R.id.GREET_btnNew);
		btnLogin = (Button) findViewById(R.id.GREET_btnLogin);
		final Animation anIn = AnimationUtils.loadAnimation(ctx, R.anim.push_down_in);
		anIn.setDuration(1000);
		btnNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loginLayout.setVisibility(View.GONE);
				newLayout.setVisibility(View.VISIBLE);
				btnNew.setVisibility(View.GONE);
				btnLogin.setVisibility(View.VISIBLE);
				emailSpinner = (Spinner) findViewById(R.id.REG_emailSpinnerCompany);
				editPassword = (EditText) findViewById(R.id.REG_passwordCompany);
				getEmail();
				
				newLayout.startAnimation(anIn);
			}
		});
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loginLayout.setVisibility(View.VISIBLE);
				newLayout.setVisibility(View.GONE);
				btnNew.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.GONE);
				emailSpinner = (Spinner) findViewById(R.id.REG_spinner);
				editPassword = (EditText) findViewById(R.id.REG_password);
				getEmail();
				loginLayout.startAnimation(anIn);
			}
		});

		btnSignIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				processSignIn();
			}
		});
		btnSignUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				processSignUp();

			}
		});
	}
	Menu mMenu;
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_refresh);
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
		case R.id.menu_more_info:
			Intent i = new Intent(getApplicationContext(),
					MoreInfoActivity.class);
			startActivity(i);
			return true;
		case R.id.menu_refresh:
			getCountryList();
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
		btnLogin.setVisibility(View.VISIBLE);
		btnNew.setVisibility(View.VISIBLE);
		startMain();

	}

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);

	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

	}
	static final String LOG = "RegistrationActivity";
}
