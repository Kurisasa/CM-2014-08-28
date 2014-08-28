package com.boha.cmlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.boha.cmlibrary.fragments.RatingFragment;
import com.boha.cmlibrary.listeners.RatingListener;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;


/**
 * FragmentActivity that hosts a RatingFragment
 *
 * @author aubreyM
 */
public class RatingActivity extends FragmentActivity implements RatingListener, BusyListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ratingFragment = (RatingFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rating_fragment);
        courseTraineeActivity = (CourseTraineeActivityDTO) getIntent().getSerializableExtra("courseTraineeActivity");
        type = getIntent().getIntExtra("type", ActivityListActivity.INSTRUCTOR);
        ratingFragment.setCourseTraineeActivity(courseTraineeActivity, type);
        bar =(ProgressBar)findViewById(R.id.progressBar);
        setTitle(getResources().getString(R.string.trainee_eval));
        CMApp app = (CMApp)getApplication();

    }


    @Override
    public void onPause() {
        Log.i("RA", "-- onPause ---");
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    Menu mMenu;

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {

        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.rating, menu);
        //mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_info) {

        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    CourseTraineeActivityDTO courseTraineeActivity;
    RatingFragment ratingFragment;
    ResponseDTO response;
    boolean ratingDone;
    int type;
    ProgressBar bar;

    @Override
    public void onRatingCompleted(CourseTraineeActivityDTO courseTraineeActivity) {
        Log.e(LOG, "--------------> onRatingCompleted - setting ratingDone = true ...");
        ratingDone = true;
        this.courseTraineeActivity = courseTraineeActivity;
        onBackPressed();
    }

    @Override
    public void onCancelRating() {
        ratingDone = false;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Log.w(LOG, "################ onBackPressed");
        if (ratingDone) {
            Intent data = new Intent();
            data.putExtra("courseTraineeActivity", courseTraineeActivity);
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
        //super.onBackPressed();
    }

    static final String LOG = "RatingActivity";

    @Override
    public void setBusy() {
       bar.setVisibility(View.VISIBLE);

    }

    @Override
    public void setNotBusy() {
        bar.setVisibility(View.GONE);

    }
}
