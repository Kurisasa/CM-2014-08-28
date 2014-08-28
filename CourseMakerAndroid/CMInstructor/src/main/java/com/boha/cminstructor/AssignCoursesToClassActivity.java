package com.boha.cminstructor;

import com.boha.cminstructor.fragments.CategoryListFragment;
import com.boha.cminstructor.listeners.CourseAssignedListener;
import com.boha.cmlibrary.ProfileActivity;
import com.boha.cmlibrary.fragments.ProfileFragment;
import com.boha.coursemaker.listeners.BusyListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class AssignCoursesToClassActivity extends FragmentActivity implements CourseAssignedListener, BusyListener{

	CategoryListFragment categoryListFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_category_list);
		categoryListFragment = (CategoryListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.category_fragment);
		setTitle(getApplicationContext().getResources().getString(R.string.class_courses));
		getData();
	}
	private void getData() {
		trainingClassID = Integer.valueOf(getIntent().getExtras().getInt("classID"));
		categoryListFragment.setTrainingClassID(trainingClassID);
		
	}
	
	int trainingClassID;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.category, menu);
		mMenu = menu;
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_back:
	            onBackPressed();
	            return true;
	        case R.id.menu_refresh:
	        	categoryListFragment.getCategories();
	            return true;
	        case R.id.menu_profile:
	        	Intent i = new Intent(this, ProfileActivity.class);
	        	i.putExtra("type", ProfileFragment.AUTHOR);
	        	startActivity(i);
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onBackPressed() {
		if (coursesAdded) {
			setResult(Activity.RESULT_OK);
		} else {
			setResult(Activity.RESULT_CANCELED);
		}
		finish();	
		super.onBackPressed();
	}
	boolean coursesAdded;
	Menu mMenu;

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
	@Override
	public void onCourseAssigned() {
		coursesAdded = true;
		
	}

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}
}
