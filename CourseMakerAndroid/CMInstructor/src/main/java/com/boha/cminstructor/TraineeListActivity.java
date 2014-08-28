package com.boha.cminstructor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cminstructor.fragments.TraineeListFragment;
import com.boha.cminstructor.listeners.NoTraineesListener;
import com.boha.cminstructor.listeners.TraineeListener;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;

public class TraineeListActivity extends FragmentActivity 
implements TraineeListener, NoTraineesListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trainee_list);
		
		traineeListFragment = (TraineeListFragment)getSupportFragmentManager()
				.findFragmentById(R.id.trainee_list_fragment);
        instructorClass = (InstructorClassDTO) getIntent().getSerializableExtra("class");
        response = (ResponseDTO)getIntent().getSerializableExtra("response");

        traineeListFragment.setInstructorClass(instructorClass);
        setTitle(instructorClass.getTrainingClassName());
		
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(LOG, "*** onResume - doin nuthin' ");
	}
	
	static final String LOG = "TraineeListActivity";
	@Override
	public void onPause() {
		Log.i("REG", "-- onPause ---");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}
	Menu mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.trainee_list, menu);
		mMenu = menu; 
		return true;
	}
	
	ResponseDTO response;


	InstructorClassDTO instructorClass;
	TraineeListFragment traineeListFragment;
	
	@Override
	public void onTraineePicked(TraineeDTO trainee) {		
		Log.i(LOG, "onTraineePicked - starting CourseActivity");
		Intent i = new Intent(getApplicationContext(), TraineeCourseActivity.class);
		i.putExtra("trainee", trainee);		
		startActivityForResult(i, START_COURSE_ACTIVITY);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == START_COURSE_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				completedCount += data.getIntExtra("completedCount", 0);
				if (completedCount > 0) {
					Log.w(LOG, "onActivityResult - refresh list, completed: " + completedCount);
					traineeListFragment.refreshTraineeList();
				} else {
					Log.d(LOG, "No completed activities, no refresh");
				}
			}
		}
	}
	int completedCount;
	@Override
	public void onBackPressed() {
		Log.i(LOG, "onBackPressed, count = " + completedCount);
		Intent i = new Intent();
		i.putExtra("completedCount", completedCount);
		setResult(Activity.RESULT_OK, i);
		finish();
		super.onBackPressed();
	}
	static final int START_COURSE_ACTIVITY = 3;
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
		
		case R.id.menu_refresh:
			traineeListFragment.getTraineeData();
			return true;
		case android.R.id.home:
			onBackPressed();
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
	public void onTraineesNotFound() {
		onBackPressed();
		
	}
}
