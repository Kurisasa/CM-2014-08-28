package com.boha.cminstructor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmlibrary.fragments.HelpRequestFragment;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.HelpRequestDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.volley.toolbox.BohaRequest;
import com.boha.volley.toolbox.BohaVolley;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to manage a list of HelpRequests. The activity may be started by a
 * Google Cloud Message coming through or directly by the user. This activity
 * hosts a ViewPager of HelpRequestFragments
 * 
 * @author aubreyM
 * 
 */
public class HelpRequestPagerActivity extends FragmentActivity implements
		BusyListener {

	static final String LOG = "HelpRequestPagerActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG, "...onCreate...");
		ctx = getApplicationContext();
		setContentView(R.layout.activity_main_pager);
		imageLoader = BohaVolley.getImageLoader(ctx);
		instructor = SharedUtil.getInstructor(ctx);
		mPager = (ViewPager) findViewById(R.id.pager);
		if (savedInstanceState != null) {
			Log.i(LOG, "...restoring saved state");
			response = (ResponseDTO) savedInstanceState
					.getSerializable("response");
			helpRequestList = response.getHelpRequestList();
			setPages();
		}

	}

	RequestDTO request;
	InstructorDTO instructor;

	/**
	 * get all requests for this instructor's classes
	 */
	private void getHelpRequests() {
		request = new RequestDTO();
		request.setRequestType(RequestDTO.HELP_REQUESTS_BY_INSTRUCTOR);
		request.setInstructorID(instructor.getInstructorID());
		if (!BaseVolley.checkNetworkOnDevice(ctx))
			return;
		setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,request,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setNotBusy();
                        if (r.getStatusCode() == 0) {
                            response = r;
                            helpRequestList = r.getHelpRequestList();
                            setPages();
                        } else {
                            ToastUtil.errorToast(ctx, r.getMessage());
                        }
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
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, request, ctx,
//				new BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setNotBusy();
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						setNotBusy();
//						if (r.getStatusCode() == 0) {
//							response = r;
//							helpRequestList = r.getHelpRequestList();
//							setPages();
//						} else {
//							ToastUtil.errorToast(ctx, r.getMessage());
//						}
//
//					}
//				});
	}

	@Override
	public void onResume() {
		Log.d(LOG, "....onResume ...");
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		Log.w(LOG, "....Saving instance state");
		b.putSerializable("response", response);
		super.onSaveInstanceState(b);

	}

	private void setPages() {
		pageList = new ArrayList<HelpRequestFragment>();
		if (helpRequestList.size() == 0) {
			ToastUtil.toast(ctx,
					ctx.getResources().getString(R.string.no_help_requests));
			onBackPressed();
			return;
		}
		for (HelpRequestDTO hr : helpRequestList) {
			HelpRequestFragment hf = new HelpRequestFragment();
			Bundle b = new Bundle();
			b.putSerializable("helpRequest", hr);
			hf.setArguments(b);
			hf.setImageLoader(imageLoader);
			pageList.add(hf);
		}
		initializePager();
	}

	public void initializePager() {
		mAdapter = new PagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
				HelpRequestFragment hf = pageList.get(arg0);
				hf.animate();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onPause() {
		Log.i(LOG, "-- onPause ---");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.help_request, menu);
		mMenu = menu;
		if (response == null) {
			getHelpRequests();
		} else {
			setPages();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:
			onBackPressed();
			return true;
		case R.id.menu_refresh:
			getHelpRequests();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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
			
			HelpRequestDTO hr = helpRequestList.get(position);
			String title = "Title";
			if (hr.getCourseTraineeActivity() != null) {
				title = hr.getCourseTraineeActivity().getTraineeName();
			} else {
				title = hr.getHelpType().getHelpTypeName();
			}
			
			return title;
			
		}
	}

	@Override
	public void onBackPressed() {

		finish();
	}

	static List<HelpRequestFragment> pageList;
	private static List<HelpRequestDTO> helpRequestList;
	static RequestQueue requestQueue;
	static BohaRequest bohaRequest;
	static ImageLoader imageLoader;
	int currentPage;
	ResponseDTO response;
	TraineeDTO trainee;
	TrainingClassDTO trainingClass;

	ViewPager mPager;
	static int numberOfPages;
	PagerAdapter mAdapter;
	Context ctx;

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);

	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

	}

}
