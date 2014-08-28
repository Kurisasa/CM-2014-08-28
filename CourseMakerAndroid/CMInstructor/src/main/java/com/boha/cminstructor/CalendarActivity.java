package com.boha.cminstructor;

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

import com.boha.cminstructor.fragments.CalendarEventListFragment;
import com.boha.cminstructor.fragments.CalendarFragment;
import com.boha.cminstructor.listeners.EventAddedListener;
import com.boha.cmlibrary.HelpPagerActivity;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.dto.TrainingClassEventDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.CalendarListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.CalendarAsyncTask;
import com.boha.coursemaker.util.CalendarRequest;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the Instructor's class calendars. It checks automatically
 * for existing class calendars and creates them when required. Calendar events
 * are entered into class calendars and managed from this Activity.
 * 
 * @author aubreyM
 * 
 */
public class CalendarActivity extends FragmentActivity implements BusyListener,
EventAddedListener{
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG, "-- onCreate  ");
		setContentView(R.layout.activity_calendar_pager);
		ctx = getApplicationContext();
		instructor = SharedUtil.getInstructor(ctx);
		instructorClass = (InstructorClassDTO)getIntent().getSerializableExtra("instructorClass");
		getSavedCalendar();

		mPager = (ViewPager) findViewById(R.id.pager);
		setTitle(getResources().getString(R.string.calendar));
	}

	InstructorDTO instructor;
	InstructorClassDTO instructorClass;
	List<TrainingClassDTO> trainingClassList;
	
	private void getSavedCalendar() {
		long id = SharedUtil.getCalendarID(ctx);
		if (id > -1) {
			Log.i(LOG, "we have an instructors calendar id: " + id);
		} else {
			// create calendar --- one per instructor
			CalendarRequest cr = new CalendarRequest();
			cr.setRequestType(CalendarRequest.ADD_CALENDAR);
			cr.setAccountName(instructor.getEmail());
			cr.setCalendarName("CourseMaker - " + instructor.getFirstName()
					+ " " + instructor.getLastName());
			cr.setContext(ctx);
			CalendarAsyncTask.performTask(cr, new CalendarListener() {

				@Override
				public void onError() {
					Log.e(LOG, "error on calendar add");
				}

				@Override
				public void onCalendarTaskDone(CalendarRequest c) {
					Log.i(LOG, "Calendar added, name: " + c.getCalendarName()
							+ " id: " + c.getCalendarID());
					SharedUtil.saveCalendarID(ctx, c.getCalendarID());
					Log.i(LOG, "---- Instructor calendar ID stored in prefs");

				}

				@Override
				public void onCalendarQueryComplete(
						List<CalendarRequest> calendarRequests) {
				}
			});
		}
	}

	@Override
	public void onPause() {
		Log.i(LOG, "-- onPause ---");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(LOG, "-- onResume  ");
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
		getMenuInflater().inflate(R.menu.calendar, menu);
		mMenu = menu;
		if (response == null)
			getTrainingClasses();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		
		case R.id.menu_help:
			Intent i = new Intent(this, HelpPagerActivity.class);
			i.putExtra("type", HelpPagerActivity.INSTRUCTOR_HELP);
			startActivity(i);
			return true;

		case R.id.menu_back:
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	Context ctx;

	protected void setPages() {
		pageList = new ArrayList<PageInterface>();
		CalendarFragment calendarFragment = new CalendarFragment();
		Bundle b = new Bundle();
		b.putSerializable("response", response);
		b.putSerializable("instructorClass", instructorClass);
		calendarFragment.setArguments(b);
		calendarFragment.setFragmentManger(getSupportFragmentManager());
		//
		CalendarEventListFragment calendarEventListFragment = new CalendarEventListFragment();
		calendarEventListFragment.setArguments(b);

		pageList.add(calendarFragment);
		pageList.add(calendarEventListFragment);
		initializePager();
	}



	int currentPage;
	private void getTrainingClasses() {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TRAINING_CLASSES_BY_INSTRUCTOR);
		r.setInstructorID(instructor.getInstructorID());

		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                        }
                        response = r;
                        trainingClassList = r.getTrainingClassList();
                        setPages();
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
//		BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, r, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setRefreshActionButtonState(false);
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						setRefreshActionButtonState(false);
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//						}
//						response = r;
//						trainingClassList = r.getTrainingClassList();
//						setPages();
//					}
//				});
	}



	ResponseDTO response;
	static final String LOG = "CalendarActivity";
	Menu mMenu;

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

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
			String title = null;
			switch (position) {
			case 0:
				title = ctx.getResources().getString(R.string.enter_event);
				break;
			case 1:
				title = ctx.getResources().getString(
						R.string.calendar_events);
				break;

			default:
				break;
			}
			return title;
		}
	}
	public void initializePager() {
		mAdapter = new PagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPage = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	PagerAdapter mAdapter;
	ViewPager mPager;
	List<PageInterface> pageList;

	@Override
	public void OnEventAdded(TrainingClassEventDTO trainingClassEvent) {
		// TODO refresh somebody ....
		
	}
}
