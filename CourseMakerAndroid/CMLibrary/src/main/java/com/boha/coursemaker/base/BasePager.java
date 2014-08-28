package com.boha.coursemaker.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.boha.coursemaker.dto.ProvinceDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.volley.toolbox.BohaRequest;

import java.util.List;
/**
 * Base class to support paging for Fragments using a ViewPager
 * @author aubreyM
 *
 */
public abstract class BasePager extends FragmentActivity {

	protected abstract void setPages();
	protected abstract void onPageChanged(int currentPage);
	
	protected  void changePageTo(int currentPage) {
		mPager.setCurrentItem(currentPage, true);
	}


	public void initializePager() {
		mAdapter = new PagerAdapter(getSupportFragmentManager(), numberOfPages);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
				onPageChanged(currentPage);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private static class PagerAdapter extends FragmentStatePagerAdapter {
		
		public PagerAdapter(FragmentManager fm, int pages) {
			super(fm);
			numberOfPages = pages;
		}

		@Override
		public int getCount() {
			return numberOfPages;
		}

		@Override
		public Fragment getItem(int position) {
			return (Fragment) pageList.get(position);
		}
	}

	public void notifyAdapter() {
		mAdapter.notifyDataSetChanged();
	}


	protected void getRemoteData(int remoteType, String servletSuffix) {
		
	}
	protected TextView txtPage, txtTip;
	protected BohaRequest bohaRequest;
	protected RequestQueue requestQueue;
	protected ImageLoader imageLoader;
	protected int countryID;
	protected ProvinceDTO province;
	
	protected ViewPager mPager;
	protected static List<PageInterface> pageList;
	protected static int numberOfPages;
	protected PagerAdapter mAdapter;
	protected Context ctx;
	protected ResponseDTO response;
	protected int currentPage;
	static final String LOG = "BasePager";
	}
