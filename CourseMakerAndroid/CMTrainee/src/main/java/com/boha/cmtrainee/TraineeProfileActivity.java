package com.boha.cmtrainee;

import com.boha.cmtrainee.fragments.TraineeProfileFragment;
import com.boha.cmtrainee.interfaces.ImageCaptureListener;
import com.boha.coursemaker.listeners.BusyListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TraineeProfileActivity extends FragmentActivity implements BusyListener,
ImageCaptureListener{

	TraineeProfileFragment traineeProfileFragment;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trainee_profile);
		ctx = getApplicationContext();
		traineeProfileFragment = (TraineeProfileFragment) getSupportFragmentManager().findFragmentById(R.id.trainee_fragment);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("CA", "*** onPause() - ...");		
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

	}
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.action_bar_progess);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}
	Menu mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profile, menu);
		mMenu = menu;
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:
			finish();
			return true;
		case R.id.menu_info:
			
			return true;
		
		case android.R.id.home:
			Intent intent = new Intent(this, MainPagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}

	@Override
	public void onCameraRequest(int width, int height) {
		// TODO start camera ....
		
	}

	@Override
	public void onGalleryRequest() {
		// TODO Auto-generated method stub
		
	}
	


}
