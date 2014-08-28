package com.boha.cmserverprobe;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.boha.cmserverprobe.fragments.CompanyListFragment;
import com.boha.cmserverprobe.fragments.CompanyStatsFragment;
import com.boha.cmserverprobe.listener.CompanyListener;
import com.boha.coursemaker.base.BaseStatsVolley;
import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.CompanyStatsDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;

public class CompanyPagerActivity extends FragmentActivity implements BusyListener, CompanyListener {

	ViewPager mPager;
	PagerAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_company_pager);
		ctx = getApplicationContext();
		setTitle(ctx.getResources().getString(R.string.company_stats));
		mPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new PagerAdapter(getSupportFragmentManager());
		if (savedInstanceState != null) {
			Log.e(LOG, "restoring saved instance data ...");
			response = (StatsResponseDTO) savedInstanceState
					.getSerializable("response");
			buildPages();
		}

	}

	@Override
	public void onPause() {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d(LOG, "onResume ...");
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		Log.d(LOG, "onSaveInstanceState ...");
		b.putSerializable("response", response);
		super.onSaveInstanceState(b);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.company_pager, menu);
		mMenu = menu;
		if (response == null)
			getRemoteData();
		return true;
	}

	private void getRemoteData() {
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_OVERALL_STATS);		
		req.setZippedResponse(true);

		setRefreshActionButtonState(true);
		BaseStatsVolley.getRemoteData(Statics.SERVLET_PLATFORM, req,
				getApplicationContext(), new BaseStatsVolley.BohaVolleyListener() {

					@Override
					public void onVolleyError(VolleyError error) {
						setRefreshActionButtonState(false);
						ToastUtil.errorToast(
								getApplicationContext(),
								getResources().getString(
										R.string.error_server_comms));
					}

					@Override
					public void onResponseReceived(StatsResponseDTO r) {
						setRefreshActionButtonState(false);
						response = r;
						if (response.getStatusCode() > 0) {
							ToastUtil.errorToast(ctx, response.getMessage());
							return;
						}
						buildPages();
						
					}
				});
	}

	StatsResponseDTO response;
	int currentPageIndex = 0;

	public void initializePager() {
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPageIndex = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	CompanyListFragment companyListFragment;

	private void buildPages() {
		Log.w(LOG, "building pages ...");
		pageList = new ArrayList<PageInterface>();
		companyListFragment = new CompanyListFragment();
		companyListFragment.setData(response);
		pageList.add(companyListFragment);
		
		for (CompanyStatsDTO cs : response.getStatsList()) {
			CompanyStatsFragment csf = new CompanyStatsFragment();
			Bundle b = new Bundle();
			b.putSerializable("companyStats", cs);
			csf.setArguments(b);
			csf.setData(cs);
			pageList.add(csf);
		}

		initializePager();
	}

	private class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {

			return (Fragment) pageList.get(i);
		}

		@Override
		public int getCount() {
			return pageList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String title = "Title";
			if (position == 0) {
				title = ctx.getResources().getString(R.string.companies);
			} else {
				PageInterface pi = pageList.get(position);
				if (pi instanceof CompanyStatsFragment) {
					CompanyStatsFragment csf = (CompanyStatsFragment)pi;
					title = csf.getCompanyStats().getCompanyName();
				}
			}
			
			return title;
		}
	}

	Menu mMenu;

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
		Log.e(LOG, "onOptionsItemSelected - " + item.getTitle()
				+ " pageIndex: " + currentPageIndex);
		switch (item.getItemId()) {

		case R.id.menu_refresh:
			getRemoteData();
			return true;
		case R.id.menu_back:
			finish();
			return true;

		case android.R.id.home:

			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	List<PageInterface> pageList;
	Context ctx;
	static final String LOG = "CompanyPagerActivity";
	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}

	@Override
	public void onCompanySelected(CompanyStatsDTO companyStats) {
		int index = 0;
		for (CompanyStatsDTO cs : response.getStatsList()) {
			if (cs.getCompanyID() == companyStats.getCompanyID()) {
				mPager.setCurrentItem(index + 1, true);
			}
			index++;
		}
	}
}
