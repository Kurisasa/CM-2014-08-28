package com.boha.cmlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.boha.cmlibrary.fragments.ActivityPageFragment;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;

public class ActivityListActivity extends FragmentActivity
        implements ActivityListListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_list);

        activityPageFragment = (ActivityPageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        trainingClassCourse = (TrainingClassCourseDTO) getIntent().getSerializableExtra("course");
        trainee = (TraineeDTO) getIntent().getSerializableExtra("trainee");
        type = getIntent().getIntExtra("type", INSTRUCTOR);

        activityPageFragment.setTrainingClassCourse(trainingClassCourse, trainee, type);
        setTitle(trainingClassCourse.getCourse().getCourseName());
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "*** onResume - doin nuthin' ");
    }
    @Override
    public void onPause() {
        Log.i("REG", "-- onPause ---");
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trainee_list, menu);
        mMenu = menu;
        return true;
    }
    @Override
    public void onBackPressed() {
        Log.i(LOG, "onBackPressed, count = " + completedCount);
        Intent i = new Intent();
        i.putExtra("completedCount", completedCount);
        setResult(Activity.RESULT_OK, i);
        finish();
        super.onBackPressed();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {

        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onActivityCompleted(CourseTraineeActivityDTO cta) {
        //activityPageFragment.refresh(cta);
    }

    @Override
    public void onRatingRequested(CourseTraineeActivityDTO cta, int type) {

        Intent i = new Intent(this, RatingActivity.class);
        i.putExtra("courseTraineeActivity", cta);
        i.putExtra("type",type);
        startActivityForResult(i, REQUEST_RATING);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(LOG,"################ onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQUEST_RATING) {
            if (resultCode == RESULT_OK) {
                CourseTraineeActivityDTO cta = (CourseTraineeActivityDTO)
                        data.getSerializableExtra("courseTraineeActivity");
                activityPageFragment.refresh(cta);
            }
        }

    }
    private void getUpdatedRatings(CourseTraineeActivityDTO cta) {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_LIST);
        w.setTraineeID(cta.getTraineeID());

    }
    static final int REQUEST_RATING = 763;
    ActivityPageFragment activityPageFragment;
    TraineeDTO trainee;
    int completedCount, type;
    Menu mMenu;
    static final String LOG = "ActivityListActivity";
    TrainingClassCourseDTO trainingClassCourse;
    public static final int INSTRUCTOR = 1, TRAINEE = 2;
}
