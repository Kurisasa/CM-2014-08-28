package com.boha.cmauthor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.listeners.PageInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPagerActivity extends FragmentActivity {

	public AbstractPagerActivity() {

	}

	static List<PageInterface> pageList;
	static int numberOfPages;
	MyAdapter mAdapter;
	ViewPager mPager;

	TextView txtPageNumber;
	View progressLayout;
	CategoryDTO category;
	CourseDTO course;
	ActivityDTO activity;
	LessonResourceDTO lessonResource;
	ResponseDTO response;

	public abstract void getData();

	public void initializePager() {
		mAdapter = new MyAdapter(getSupportFragmentManager(), numberOfPages,
				this);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				txtPageNumber.setText("" + (arg0 + 1));

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	public void buildMasterPages(PageInterface page) {
		pageList = new ArrayList<PageInterface>();

		pageList.add(page);
		numberOfPages = pageList.size();
	}

	public void addPage(PageInterface page) {

		pageList.add(page);
		numberOfPages = pageList.size();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.resource, menu);
		return true;
	}

	public static class MyAdapter extends FragmentPagerAdapter {

		AbstractPagerActivity pager;

		public MyAdapter(FragmentManager fm, int pages,
				AbstractPagerActivity pager) {
			super(fm);
			numberOfPages = pages;
			this.pager = pager;
		}

		@Override
		public int getCount() {
			// notifyDataSetChanged();
			return numberOfPages;
		}

		@Override
		public Fragment getItem(int position) {
			return (Fragment) pageList.get(position);

		}

	}

}
