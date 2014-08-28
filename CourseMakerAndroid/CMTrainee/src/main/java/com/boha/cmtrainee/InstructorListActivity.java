package com.boha.cmtrainee;

import com.boha.cmtrainee.fragments.InstructorListFragment;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.volley.toolbox.BohaVolley;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class InstructorListActivity extends FragmentActivity implements BusyListener{

	InstructorListFragment instructorListFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_instructor_list);
		instructorListFragment = (InstructorListFragment)
				getSupportFragmentManager().findFragmentById(R.id.instructorListFragment);
		instructorListFragment.setImageLoader(BohaVolley.getImageLoader(getApplicationContext()));
		TrainingClassDTO cls = SharedUtil.getTrainingClass(getApplicationContext());
		instructorListFragment.setTrainingClass(cls);				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.instructor_list, menu);
		mMenu = menu;
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_back_to) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_back_to);
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
	public void onPause() {
		super.onPause();
		Log.i("CA", "*** onPause() - ...");		
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

	}
	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}
	Menu mMenu;
}
