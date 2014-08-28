package com.boha.cminstructor;

import android.app.Activity;
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

import com.boha.cminstructor.fragments.ClassListFragment;
import com.boha.cminstructor.fragments.DashboardFragment;
import com.boha.cminstructor.listeners.ClassListener;
import com.boha.cmlibrary.ProfileActivity;
import com.boha.cmlibrary.fragments.ProfileFragment;
import com.boha.cmlibrary.fragments.SkillLevelListFragment;
import com.boha.cmlibrary.fragments.SkillListFragment;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * FragmentActivity that uses a ViewPager to host a DashboardFragment and a ClassListFragment. Refreshes lookup data
 * and controls the request for remote data to populate the fragments.
 * This is the entry point for the Instructor app after login (which is a one-time per device event).
 *
 * @author aubreyM
 */
public class MainPagerActivity extends FragmentActivity implements
        ClassListener, BusyListener {

    static final String LOG = "MainPagerActivity";
    ClassListFragment classListFragment;
    DashboardFragment dashboardFragment;
    static Timer timer;
    static long TEN_SECONDS = 10000, THIRTY_SECONDS = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getApplicationContext();
        setContentView(R.layout.activity_main_pager);
        mPager = (ViewPager) findViewById(R.id.pager);
        company = SharedUtil.getCompany(ctx);

        ACRA.getErrorReporter().putCustomData("companyID", "" + company.getCompanyID());
        ACRA.getErrorReporter().putCustomData("companyName", company.getCompanyName());
        refresh = getIntent().getBooleanExtra("refresh", false);
        Log.d(LOG, "######### refresh: " + refresh);

        setTitle(company.getCompanyName());
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState
                    .getSerializable("response");
            if (response != null) {
                Log.i(LOG, "restored instance state, fragments to be set");
                buildPages();
            }
        }


    }

    boolean refresh;

    @Override
    public void onDestroy() {
        Log.e(LOG, "###### onDestroy - closing websocket session");
        WebSocketUtil.disconnectSession();
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.i(LOG, "onSaveInstanceState");
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    private void buildPages() {
        pageList = new ArrayList<PageInterface>();
        classListFragment = new ClassListFragment();
        dashboardFragment = new DashboardFragment();
        skillListFragment = new SkillListFragment();
        skillLevelListFragment = new SkillLevelListFragment();

        Bundle b = new Bundle();
        b.putSerializable("response", response);
        classListFragment.setArguments(b);
        dashboardFragment.setArguments(b);

        Bundle xx = new Bundle();
        ResponseDTO r = new ResponseDTO();
        r.setSkillList(response.getSkillList());
        xx.putSerializable("response", r);
        skillListFragment.setArguments(xx);

        Bundle xy = new Bundle();
        ResponseDTO ry = new ResponseDTO();
        ry.setSkillLevelList(response.getSkillLevelList());
        xy.putSerializable("response", ry);
        skillLevelListFragment.setArguments(xy);

        //
        pageList.add(dashboardFragment);
        pageList.add(classListFragment);
        pageList.add(skillListFragment);
        pageList.add(skillLevelListFragment);

        initializePager();
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

    @Override
    public void onPause() {
        Log.i(LOG, "-- onPause ---");
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i(LOG, "--------------- onResume  - " + LOG);
        super.onResume();
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
        Log.i(LOG, "-- onCreateOptions  --- getting remote data");
        getMenuInflater().inflate(R.menu.main_pager, menu);
        mMenu = menu;

        if (refresh) {
            getRemoteData();
            return true;
        }

        if (response == null)

            CacheUtil.getCachedData(ctx, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
                @Override
                public void onFileDataDeserialized(ResponseDTO r) {
                    if (r != null) {
                        response = r;
                        buildPages();
                    }
                    getRemoteData();
                }

                @Override
                public void onDataCached() {

                }
            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_help_requests:
                Intent i = new Intent(this, HelpRequestPagerActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_refresh:
                getRemoteData();
                return true;
            case R.id.menu_back:
                finish();
                return true;
            case R.id.menu_info:
                return true;
            case R.id.menu_profile:
                Intent x = new Intent(ctx, ProfileActivity.class);
                int type = ProfileFragment.INSTRUCTOR;
                x.putExtra("type", type);
                startActivity(x);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    int currentPage;

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
                    title = ctx.getResources().getString(R.string.dashboard);
                    break;
                case 1:
                    title = ctx.getResources().getString(R.string.instructor_classes);
                    break;
                case 2:
                    title = ctx.getResources().getString(R.string.skills);
                    break;
                case 3:
                    title = ctx.getResources().getString(R.string.skill_levels);
                    break;

                default:
                    break;
            }
            return title;
        }
    }

    @Override
    public void onClassPicked(InstructorClassDTO instructorClass) {
        Intent i = new Intent(ctx, TraineeListActivity.class);
        i.putExtra("class", instructorClass);
        startActivityForResult(i, START_TRAINEE_LIST);
    }

    @Override
    public void onRefreshRequested() {
        getRemoteData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG, "onActivityResult resultCode: " + resultCode);
        if (requestCode == START_TRAINEE_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                completedCount += data.getIntExtra("completedCount", 0);
                if (completedCount > 0) {
                    getRemoteData();
                }
            }
        }
    }

    public void getRemoteData() {

        instructor = SharedUtil.getInstructor(ctx);
        final RequestDTO request = new RequestDTO();
        request.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_TOTALS_BY_INSTRUCTOR);
        request.setInstructorID(instructor.getInstructorID());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRefreshActionButtonState(true);
            }
        });

        getData(request);

//		BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, request, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setRefreshActionButtonState(false);
//						Log.e(LOG, "Problem: " + error.getMessage());
//						if (error.getMessage() != null) {
//							if (error.getMessage().contains("SocketException")) {
//								try {
//									if (retried == 0 || retried == 1) {
//										Thread.sleep(2000);
//									} else {
//										Thread.sleep(retried * 2000);
//									}
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
//							if (retried < RETRY_COUNT) {
//								retried++;
//								Log.i(LOG,
//										"Retrying after socket error ... retry: "
//												+ retried);
//								getRemoteData();
//								return;
//							}
//						}
//						if (error instanceof NetworkError) {
//							NetworkError ne = (NetworkError) error;
//							if (ne.networkResponse != null) {
//								Log.w(LOG, "volley http status code: "
//										+ ne.networkResponse.statusCode);
//							}
//							ToastUtil.errorToast(
//									ctx,
//									ctx.getResources().getString(
//											R.string.error_server_unavailable));
//						} else {
//							ToastUtil.errorToast(ctx, ctx.getResources()
//									.getString(R.string.error_server_comms));
//						}
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						setRefreshActionButtonState(false);
//						response = r;
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//
//                        if (pageList == null) {
//                            buildPages();
//                        } else {
//                            dashboardFragment.refresh(response);
//                        }
//                        final ResponseDTO rx = new ResponseDTO();
//                        rx.setRatingList(r.getRatingList());
//                        final ResponseDTO ry = new ResponseDTO();
//                        ry.setHelpTypeList(r.getHelpTypeList());
//                        CacheUtil.cacheData(ctx,rx, CacheUtil.CACHE_RATINGS, new CacheUtil.CacheUtilListener() {
//                            @Override
//                            public void onFileDataDeserialized(ResponseDTO response) {
//
//                            }
//
//                            @Override
//                            public void onDataCached() {
//                                CacheUtil.cacheData(ctx, ry, CacheUtil.CACHE_HELPTYPES, new CacheUtil.CacheUtilListener() {
//                                    @Override
//                                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                                    }
//
//                                    @Override
//                                    public void onDataCached() {
//                                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_DATA, new CacheUtil.CacheUtilListener() {
//                                            @Override
//                                            public void onFileDataDeserialized(ResponseDTO response) {
//
//                                            }
//
//                                            @Override
//                                            public void onDataCached() {
//                                                Log.e(LOG,"#### main data cache written");
//                                            }
//                                        });
//                                    }
//                                });
//                            }
//                        });
//
//                        //////
//
//
//					}
//				});
    }

    private void getData(RequestDTO request) {
        WebSocketUtil.sendRequest(ctx, Statics.INSTRUCTOR_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        response = r;
                        buildPages();

                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TRAINEE_ACTIVITY, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });
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
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
    }

    private int completedCount;
    private static final int RETRY_COUNT = 3, START_TRAINEE_LIST = 33;
    private int retried = 0;
    private InstructorDTO instructor;
    private ResponseDTO response;
    private CompanyDTO company;
    private ViewPager mPager;
    private static List<PageInterface> pageList;
    private PagerAdapter mAdapter;
    private Context ctx;
    private SkillListFragment skillListFragment;
    private SkillLevelListFragment skillLevelListFragment;

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);

    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);

    }
}
