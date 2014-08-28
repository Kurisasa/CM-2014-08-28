package com.boha.cmauthor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.boha.cmauthor.fragments.CategoryListFragment;
import com.boha.cmauthor.fragments.CourseListFragment;
import com.boha.cmauthor.interfaces.CategoryListener;
import com.boha.cmauthor.interfaces.CourseListener;
import com.boha.cmlibrary.ProfileActivity;
import com.boha.cmlibrary.fragments.ProfileFragment;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import org.acra.ACRA;

import java.util.Timer;

/**
 * The Author app entry point after log in. Manages categories, adding, updating and deleting category items.
 *
 * @author aubreyM
 */
public class CategoryActivity extends FragmentActivity implements
		CategoryListener, CourseListener, BusyListener {
    static Timer timer;
    static long TEN_SECONDS = 10000, THIRTY_SECONDS = 30000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_course);
		ctx = getApplicationContext();
		mainLayout = findViewById(R.id.mainLayout);
        setTitle(ctx.getResources().getString(R.string.author));
        CompanyDTO c = SharedUtil.getCompany(ctx);
        getActionBar().setSubtitle(c.getCompanyName());
        ACRA.getErrorReporter().putCustomData("companyID", "" + c.getCompanyID());
        ACRA.getErrorReporter().putCustomData("companyName", c.getCompanyName());

		categoryListFragment = (CategoryListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.category_fragment);
		courseListFragment = (CourseListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.course_fragment);
		if (savedInstanceState != null) {
			response = (ResponseDTO) savedInstanceState
					.getSerializable("response");
			if (response != null) {
				Log.i(LOG, "Instate state recovered, response set");
				categoryListFragment.setResponse(response);
			}
		}
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


	}

	private void getCategoryList() {
        CacheUtil.getCachedData(ctx,CacheUtil.CACHE_CATEGORIES, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                if (r != null) {
                    response = r;
                    categoryListFragment.setResponse(response);
                }
                getCategoryListFromServer();
            }

            @Override
            public void onDataCached() {

            }
        });

	}

    private void getCategoryListFromServer() {
        final RequestDTO request = new RequestDTO();
        request.setRequestType(RequestDTO.GET_CATEGORY_LIST_BY_COMPANY);
        request.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
        request.setAuthorID(SharedUtil.getAuthor(ctx).getAuthorID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        getData(request);
//        BaseVolley.sendRequest(Statics.SERVLET_AUTHOR, request, ctx,
//                new BaseVolley.BohaVolleyListener() {
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        ToastUtil.errorToast(
//                                ctx,
//                                ctx.getResources().getString(
//                                        R.string.error_server_comms));
//                        setRefreshActionButtonState(false);
//                    }
//
//                    @Override
//                    public void onResponseReceived(ResponseDTO r) {
//                        setRefreshActionButtonState(false);
//                        response = r;
//                        if (response.getStatusCode() > 0) {
//                            ToastUtil.errorToast(ctx, r.getMessage());
//                            return;
//                        }
//                        categoryListFragment.setResponse(response);
//                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_CATEGORIES, new CacheUtil.CacheUtilListener() {
//                            @Override
//                            public void onFileDataDeserialized(ResponseDTO response) {
//
//                            }
//
//                            @Override
//                            public void onDataCached() {
//
//                            }
//                        });
//
//                    }
//                });

    }
    private void getData(RequestDTO request) {
        WebSocketUtil.sendRequest(ctx,Statics.AUTHOR_ENDPOINT,request,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {

                if (r.getStatusCode() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.errorToast(ctx,r.getMessage());
                        }
                    });
                }
                response = r;

                CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_CATEGORIES, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO response) {

                    }

                    @Override
                    public void onDataCached() {

                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        categoryListFragment.setResponse(response);
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
                        setRefreshActionButtonState(false);
                        ToastUtil.errorToast(ctx,message);
                    }
                });

            }


        });
    }
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu
					.findItem(R.id.menu_refresh);
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
		getMenuInflater().inflate(R.menu.category_course, menu);
		mMenu = menu;
		if (courseListFragment == null) {
			MenuItem mi = mMenu.getItem(1);
			mi.setVisible(false);
		}
		if (response == null) {
			getCategoryList();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_add_category:
			categoryListFragment.openAddLayout();
			return true;
		case R.id.action_add_course:
			courseListFragment.openAddLayout();
			return true;

		case R.id.menu_back:
			onBackPressed();
			return true;
		case R.id.menu_refresh:
			getCategoryListFromServer();
			return true;
		case R.id.action_profile:
			Intent i = new Intent(this, ProfileActivity.class);
			i.putExtra("type", ProfileFragment.AUTHOR);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	CategoryListFragment categoryListFragment;
	CourseListFragment courseListFragment;
	CourseDTO course;

	@Override
	public void onCoursePicked(CourseDTO course) {
		Log.i(LOG, "######## onCoursePicked : " + course.getCourseName());
		this.course = course;

	}

	CategoryDTO category;

	@Override
	public void onCategoryPicked(CategoryDTO category) {
		this.category = category;
		Log.i(LOG,
				"########xxx onCategoryPicked : " + category.getCategoryName());
		if (courseListFragment != null) {
			courseListFragment.setCourses(category);
		} else {
			Log.e(LOG,
					"------onCategoryPicked------ starting CourseActivity");
			Intent i = new Intent(this, CourseActivity.class);
			i.putExtra("category", category);
			startActivityForResult(i, START_COURSE_ACTIVITY);
		}

	}

    @Override
    public void onImportRequested() {
        Intent x = new Intent(ctx, ImportActivity.class);
        startActivityForResult(x, IMPORT_CATEGORY);
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w(LOG, "onActivityResult ... resultCode: " + resultCode);
		if (requestCode == START_COURSE_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				Log.i(LOG, "alerting that courses added/updated - refreshing");
                ResponseDTO w = (ResponseDTO)data.getSerializableExtra("response");
				courseListFragment.setCourseList(w.getCourseList());
			}
		}
	}

	static final int START_COURSE_ACTIVITY = 133, IMPORT_CATEGORY = 3375;
	static final String LOG = "CategoryActivity";

	@Override
	public void onBackPressed() {
		Log.i(LOG, "-- onBackPressed ---");
		super.onBackPressed();
	}

	@Override
	public void onPause() {
		Log.d(LOG, "-- onPause ---");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		Log.i(LOG, "-- onSaveInstanceState ---");
		b.putSerializable("response", response);
		super.onSaveInstanceState(b);
	}

	@Override
	public void onResume() {
		Log.i(LOG, "-- onResume ---");
		super.onPause();
	}

	

	View progressLayout, mainLayout;

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);

	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

	}

	Context ctx;
	Menu mMenu;
	ResponseDTO response;

	@Override
	public void onCourseAdded(CourseDTO course) {
		// TODO Auto-generated method stub
		
	}
}
