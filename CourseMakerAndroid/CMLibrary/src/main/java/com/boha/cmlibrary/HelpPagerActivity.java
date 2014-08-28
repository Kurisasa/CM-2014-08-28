package com.boha.cmlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmlibrary.fragments.HelpFragment;

import java.util.List;

public class HelpPagerActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_pager);
		//setPages();
	}
	/*private void setPages() {
		setRefreshActionButtonState(true);
		pageList = new ArrayList<HelpFragment>();
		
		numberOfPages = 3;
		type = getIntent().getIntExtra("type", GENERAL_HELP);
		txtPage.setText("Page 1 of " + numberOfPages);

		initializePager();
		setRefreshActionButtonState(false);
	}
*/
	public void initializePager() {
		mAdapter = new MyAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
				txtPage.setText("Page " + (currentPage + 1) + " of "
						+ numberOfPages);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help_pager, menu);
		return true;
	}

	public static class MyAdapter extends FragmentStatePagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return numberOfPages;
		}

		@Override
		public Fragment getItem(int position) {			
			return HelpFragment.init(type, imageLoader);
		}
	}

	Menu mMenu;
	ViewPager mPager;
	MyAdapter mAdapter;
	Context ctx;
	static ImageLoader imageLoader;
	static int numberOfPages, type, currentPage;
	static List<HelpFragment> pageList;
	TextView txtPage;
	
	public static final int INSTRUCTOR_HELP = 1, GENERAL_HELP = 2, TRAINEE_HELP = 3;
}
