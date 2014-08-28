package com.boha.cminstructor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cminstructor.adapters.Container;
import com.boha.cminstructor.fragments.CourseListFragment;
import com.boha.cminstructor.listeners.CourseListener;
import com.boha.cmlibrary.ActivityListActivity;
import com.boha.cmlibrary.CMApp;
import com.boha.cmlibrary.TraineeSkillsActivity;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.Locale;

public class TraineeCourseActivity extends FragmentActivity implements CourseListener {

	CourseListFragment courseListFragment;
	Container data;
	Context ctx;
	TraineeDTO trainee;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course);
		ctx = getApplicationContext();
		courseListFragment = (CourseListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.course_fragment);
        CMApp app = (CMApp)getApplication();
		courseListFragment.setImageLoader(app.getImageLoader());
		trainee = (TraineeDTO) getIntent().getExtras().getSerializable("trainee");	
		courseListFragment.setTrainee(trainee);
		
		setTitle(getResources().getString(R.string.class_courses));
	}

	@Override
	public void onPause() {		
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();

	}
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
	public void onBackPressed() {	
		Log.i(LOG, "-- onBackPressed ---");
		Intent i = new Intent();
		i.putExtra("completedCount", completedCount);
		setResult(Activity.RESULT_OK, i);
		finish();
		super.onBackPressed();
		
	}
	Menu mMenu;
	
	private void getTraineeData() {
		
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_TRAINEE_DATA);
		req.setTraineeID(trainee.getTraineeID());
		req.setTrainingClassID(trainee.getTrainingClassID());
		req.setCompanyID(trainee.getCompanyID());
        req.setCountryCode(Locale.getDefault().getCountry());

		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        for (TraineeDTO t: response.getTraineeList()) {
                            if (t.getTraineeID() == trainee.getTraineeID()) {
                                trainee = t;
                                break;
                            }
                        }
                        courseListFragment.setData(response);
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
                        setRefreshActionButtonState(false);
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, req, ctx, new BaseVolley.BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				setRefreshActionButtonState(false);
//				ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO response) {
//				setRefreshActionButtonState(false);
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, response.getMessage());
//					return;
//				}
//				courseListFragment.setData(response);
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.course, menu);
		mMenu = menu;
		getTraineeData();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

            case R.id.menu_skills:
                Intent i = new Intent(this, TraineeSkillsActivity.class);
                i.putExtra("trainee", trainee);
                Log.i(LOG,"@@@@@@@@ about to start TraineeSkillsActivity skills: " + trainee.getTraineeSkillList().size());
                startActivity(i);

                return true;
		case R.id.menu_refresh:
			getTraineeData();
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
	public void onCoursePicked(TrainingClassCourseDTO course) {
		Log.w(LOG, "####################### Course selected. " + course.getCourse().getCourseName());
        if (course.getNumberOfActivities() == 0) {
            ToastUtil.toast(ctx, ctx.getResources().getString(R.string.course_noact));
            return;
        }
		Intent i = new Intent(ctx, ActivityListActivity.class);
		i.putExtra("course", course);
		i.putExtra("trainee", trainee);
        i.putExtra("type", ActivityListActivity.INSTRUCTOR);
		startActivityForResult(i, START_ACTIVITY_LIST);

	}

	static final int START_ACTIVITY_LIST = 33;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == START_ACTIVITY_LIST) {
			if (resultCode == Activity.RESULT_OK) {
				completedCount = data.getIntExtra("count", 0);
				trainee.setTotalCompleted(trainee.getTotalCompleted() + completedCount);
				Log.i(LOG, "onActivityResult setting activity count " + completedCount);
				courseListFragment.setActivityCompleted(completedCount);
			}
		}
	}
	int completedCount;
	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}
	static final String LOG = "CourseActivity";
}
