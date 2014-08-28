package com.boha.cmauthor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boha.cmauthor.fragments.ActivityListFragment;
import com.boha.cmauthor.fragments.ResourceListFragment;
import com.boha.cmauthor.interfaces.ActivityListener;
import com.boha.cmauthor.interfaces.ResourceListener;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.listeners.PageInterface;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesAndLinksPager extends FragmentActivity implements
        ActivityListener, ResourceListener {

    public ActivitiesAndLinksPager() {
    }


    CategoryDTO category;
    static CourseDTO course;
    View mainLayout;
    View progressLayout;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stuff_pager);
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_title_strip);
        mainLayout = findViewById(R.id.mainLayout);
        getData();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

    public void getData() {
        course = (CourseDTO) getIntent().getSerializableExtra("course");
        category = (CategoryDTO) getIntent().getSerializableExtra("category");
        setTitle(course.getCourseName());
        if (mPager != null) {
            buildMasterPages();
            initializePager();
        } else {
        }
    }

    @Override
    public void onBackPressed() {

        if (lessonAddedUpdated || activityAddedUpdated) {
            setResult(Activity.RESULT_OK);
        }
        finish();
        super.onBackPressed();

    }

    static final String LOG = "StuffPagerActivity";

    @Override
    public void onPause() {
        Log.i(LOG, "--- onPause ---");
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (menu != null) {
            final MenuItem refreshItem = menu.findItem(R.id.menu_add_course);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }

        if (menu != null) {
            final MenuItem actMenuItem = menu.getItem(1);
            if (actMenuItem.isVisible()) {
                if (refreshing) {
                    actMenuItem.setActionView(R.layout.action_bar_progess);
                } else {
                    actMenuItem.setActionView(null);
                }
            }
            final MenuItem objMenuItem = menu.getItem(2);
            if (objMenuItem.isVisible()) {
                if (refreshing) {
                    objMenuItem.setActionView(R.layout.action_bar_progess);
                } else {
                    objMenuItem.setActionView(null);
                }
            }
            final MenuItem resMenuItem = menu.getItem(3);
            if (resMenuItem.isVisible()) {
                if (refreshing) {
                    resMenuItem.setActionView(R.layout.action_bar_progess);
                } else {
                    resMenuItem.setActionView(null);
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.activities_and_links, menu);
        turnMenuItemsOff();
        menu.getItem(0).setVisible(true);
        return true;
    }

    private void setMenuItems() {
        turnMenuItemsOff();
        switch (currentPage) {
            case 0: // activity is showing
                menu.getItem(0).setVisible(true);
                break;
            case 1: // links is showing
                menu.getItem(1).setVisible(true);
                break;


            default:
                break;
        }
    }

    private void turnMenuItemsOff() {
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_activity:
                activityListFragment.openAddLayout();
                return true;

            case R.id.menu_add_resource:
                resourceListFragment.addLink();
                return true;

            case R.id.menu_hide_description:
                activityListFragment.hideDescriptions();
                return true;
            case R.id.menu_show_description:
                activityListFragment.showDescriptions();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onShowProgressBar() {
        setRefreshActionButtonState(true);

    }

    @Override
    public void onRemoveProgressBar() {
        setRefreshActionButtonState(false);

    }

    ActivityListFragment activityListFragment;
    ResourceListFragment resourceListFragment;

    public void initializePager() {
        mAdapter = new MyAdapter(getSupportFragmentManager(), numberOfPages,
                this);

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentPage = arg0;
                setMenuItems();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    int currentPage;

    public void buildMasterPages() {

        if (course == null) {
            throw new UnsupportedOperationException(".....course is NULL, WHY??");
        }
        pageList = new ArrayList<PageInterface>();
        activityListFragment = new ActivityListFragment();
        resourceListFragment = new ResourceListFragment();

        Bundle b1 = new Bundle();
        b1.putSerializable("course", course);
        activityListFragment.setArguments(b1);
        resourceListFragment.setArguments(b1);

        pageList.add(activityListFragment);
        pageList.add(resourceListFragment);


        numberOfPages = pageList.size();

    }

    private Menu menu;

    public static class MyAdapter extends FragmentStatePagerAdapter {

        ActivitiesAndLinksPager pager;

        public MyAdapter(FragmentManager fm, int pages, ActivitiesAndLinksPager pager) {
            super(fm);
            numberOfPages = pages;
            this.pager = pager;
        }

        @Override
        public int getCount() {
            return numberOfPages;
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) pageList.get(position);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";
            switch (position) {
                case 0:
                    title = "Activities";
                    break;
                case 1:
                    title = "Resource Links";
                    break;

                default:
                    break;
            }
            return title;
        }
    }

    static PagerTabStrip pagerTabStrip;
    static List<PageInterface> pageList;
    static int numberOfPages;
    MyAdapter mAdapter;

    @Override
    public void onResourcePicked(LessonResourceDTO resource) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onActivityPicked(ActivityDTO activity) {
        // TODO Auto-generated method stub

    }


    boolean lessonAddedUpdated;

    boolean activityAddedUpdated;

    @Override
    public void onActivitiesAddedUpdated() {
        activityAddedUpdated = true;

    }

    @Override
    public void onLinksAddedUpdated() {
        activityAddedUpdated = true;

    }


}
