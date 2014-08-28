package com.boha.cmauthor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boha.cmauthor.fragments.CourseListFragment;
import com.boha.cmauthor.interfaces.CategoryListener;
import com.boha.cmauthor.interfaces.CourseListener;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends FragmentActivity
        implements CourseListener,
        CategoryListener {

    public CourseActivity() {

    }

    CourseListFragment courseListFragment;
    View mainLayout;
    CategoryDTO category;
    static final String LOG = "CourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category2);
        courseListFragment = (CourseListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.course_fragment);
        courseListFragment.setFragmentManager(getSupportFragmentManager());
        mainLayout = findViewById(R.id.mainLayout);
        getData();
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
    }

    public void getData() {

        Log.i(LOG, "getData - feeding courseListFragment the category selected");
        category = (CategoryDTO) getIntent().getSerializableExtra("category");
        courseListFragment.setCourses(category);
        setTitle(category.getCategoryName());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    Menu mMenu;

    public void setRefreshActionButtonState(final boolean refreshing) {
        Log.d(LOG, "setRefreshActionButtonState refreshing: " + refreshing);
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_add_course);
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
        if (!addedCourseList.isEmpty()) {
            ResponseDTO w = new ResponseDTO();
            w.setCourseList(addedCourseList);
            Intent data = new Intent();
            data.putExtra("response", w);
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("CA", "### onCreateMenuOptions .... saving menu");
        getMenuInflater().inflate(R.menu.course, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_course:
                courseListFragment.showDialog(false);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    CourseDTO course;

    @Override
    public void onCoursePicked(CourseDTO course) {
        Log.i("CourseAct", "---- Course selected: " + course.getCourseName());
        this.course = course;
        //TODO - start list of course's activities
        Intent i = new Intent(getApplicationContext(), ActivitiesAndLinksPager.class);
        i.putExtra("course", course);
        i.putExtra("category", category);
        startActivityForResult(i, START_LESSONS);

    }

    static final int START_LESSONS = 333;

    private boolean lessonsAddedUpdated;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == START_LESSONS) {
            if (resultCode == Activity.RESULT_OK) {
                lessonsAddedUpdated = true;
                refreshCourseLessons();
            }

        }
    }

    private void refreshCourseLessons() {
        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_LESSON_LIST_BY_COURSE);
        req.setCourseID(course.getCourseID());
        setBusy();
        WebSocketUtil.sendRequest(getApplicationContext(),Statics.AUTHOR_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setNotBusy();
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(getApplicationContext(), r.getMessage());
                            return;
                        }
                        courseListFragment.refreshCourse(r, course.getCourseID());
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
                        ToastUtil.errorToast(getApplicationContext(),message);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_AUTHOR, req, getApplicationContext(), new BaseVolley.BohaVolleyListener() {
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setNotBusy();
//                ToastUtil.errorToast(getApplicationContext(),
//                        getApplicationContext().getResources().getString(R.string.error_server_comms));
//
//            }
//
//            @Override
//            public void onResponseReceived(ResponseDTO r) {
//                setNotBusy();
//                if (r.getStatusCode() > 0) {
//                    ToastUtil.errorToast(getApplicationContext(), r.getMessage());
//                    return;
//                }
//                courseListFragment.refreshCourse(r, course.getCourseID());
//            }
//        });
    }

    @Override
    public void onCategoryPicked(CategoryDTO category) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onImportRequested() {
        Intent x = new Intent(this, ImportActivity.class);
        startActivity(x);
    }

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);

    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);

    }

    ResponseDTO response;

    List<CourseDTO> addedCourseList = new ArrayList<CourseDTO>();

    @Override
    public void onCourseAdded(CourseDTO course) {
        Log.i(LOG, "adding added/updated course to list: " + course.getCourseName());
        addedCourseList.add(course);

    }
}
