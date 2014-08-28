package com.boha.cmtrainee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.toolbox.ImageLoader;
import com.boha.cmlibrary.ActivityListActivity;
import com.boha.cmlibrary.fragments.TraineeSkillsFragment;
import com.boha.cmtrainee.fragments.DashboardFragment;
import com.boha.cmtrainee.fragments.TeamListFragment;
import com.boha.cmtrainee.fragments.TeamMemberListFragment;
import com.boha.cmtrainee.fragments.TraineeCourseListFragment;
import com.boha.cmtrainee.fragments.TraineeListFragment;
import com.boha.cmtrainee.fragments.TraineeProfileFragment;
import com.boha.cmtrainee.interfaces.CourseListener;
import com.boha.cmtrainee.interfaces.ImageCaptureListener;
import com.boha.cmtrainee.interfaces.TeamListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.ProvinceDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TeamDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.listeners.BitmapListener;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.Bitmaps;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.ImageTask;
import com.boha.coursemaker.util.PictureUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.volley.toolbox.BohaVolley;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class MainPagerActivity extends FragmentActivity
        implements BusyListener, CourseListener, TeamListener, ImageCaptureListener,
        TraineeSkillsFragment.TraineeSkillsFragmentListener {
    static Timer timer;
    static long TEN_SECONDS = 10000, THIRTY_SECONDS = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG, "---- onCreate");

        setContentView(R.layout.activity_main_pager);
        ctx = getApplicationContext();
        trainee = SharedUtil.getTrainee(ctx);

        mPager = (ViewPager) findViewById(R.id.pager);
        CompanyDTO company = SharedUtil.getCompany(ctx);
        ACRA.getErrorReporter().putCustomData("companyID", "" + company.getCompanyID());
        ACRA.getErrorReporter().putCustomData("companyName", company.getCompanyName());

        setTitle(ctx.getResources().getString(R.string.trainee_app));
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState
                    .getSerializable("response");
            if (response != null) {
                Log.i(LOG, "restored instance state, fragments to be set");
                buildPages();
                provinceList = response.getProvinceList();
                teamList = response.getTeamList();
                setTeamListFragment();
                setTraineeProfileFragment();
            }
        }
    }

    public void refreshTraineeData() {
        final RequestDTO request = new RequestDTO();
        request.setRequestType(RequestDTO.GET_TRAINEE_DATA);
        request.setTraineeID(trainee.getTraineeID());
        request.setTrainingClassID(trainee.getTrainingClassID());
        request.setCompanyID(trainee.getCompanyID());
        request.setCountryCode(Locale.getDefault().getCountry());
        //

        if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRefreshActionButtonState(true);
            }
        });
        getData(request);

//		BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, request, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setRefreshActionButtonState(false);
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//						Log.e(LOG, "Volley error - " + error.getMessage());
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						setRefreshActionButtonState(false);
//						response = r;
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//
//						traineeList = response.getTraineeList();
//                        if (traineeList == null)
//                            throw new UnsupportedOperationException("TraineeList is null");
//						buildPages();
//						getClassTeams();
//                        CacheUtil.cacheData(ctx,response,CacheUtil.CACHE_TRAINEE_ACTIVITY, new CacheUtil.CacheUtilListener() {
//                            @Override
//                            public void onFileDataDeserialized(ResponseDTO response) {
//
//                            }
//
//                            @Override
//                            public void onDataCached() {
//                                ResponseDTO w = new ResponseDTO();
//                                w.setRatingList(response.getRatingList());
//                                CacheUtil.cacheData(ctx,w,CacheUtil.CACHE_RATINGS, new CacheUtil.CacheUtilListener() {
//                                    @Override
//                                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                                    }
//
//                                    @Override
//                                    public void onDataCached() {
//                                        ResponseDTO w = new ResponseDTO();
//                                        w.setHelpTypeList(response.getHelpTypeList());
//                                        CacheUtil.cacheData(ctx,w,CacheUtil.CACHE_HELPTYPES,new CacheUtil.CacheUtilListener() {
//                                            @Override
//                                            public void onFileDataDeserialized(ResponseDTO response) {
//
//                                            }
//
//                                            @Override
//                                            public void onDataCached() {
//
//                                            }
//                                        });
//                                    }
//                                });
//                            }
//                        });
//					}
//				});

    }

    TraineeDTO me;

    private void getData(RequestDTO request) {
        WebSocketUtil.sendRequest(ctx, Statics.TRAINEE_ENDPOINT, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        response = r;
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }

                        traineeList = response.getTraineeList();
                        if (traineeList == null)
                            throw new UnsupportedOperationException("TraineeList is null");
                        for (TraineeDTO t : traineeList) {
                            if (t.getTraineeID() == SharedUtil.getTrainee(ctx).getTraineeID()) {
                                Log.w(LOG, "Found local trainee, skills: " + t.getTraineeSkillList().size());
                                me = t;
                                break;
                            }
                        }
                        buildPages();
                        provinceList = response.getProvinceList();
                        setTeamListFragment();
                        setTraineeProfileFragment();
                        setTraineeSkillsFragment();


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
                        timer.cancel();
                        timer = null;
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
    }

    private void buildPages() {
        Log.i(LOG, "############ --building pages");
        pageList = new ArrayList<PageInterface>();
        dashboardFragment = new DashboardFragment();
        ResponseDTO dResp = new ResponseDTO();
        dResp.setTrainingClassCourseList(response.getTrainingClassCourseList());
        Bundle data = new Bundle();
        data.putSerializable("response", dResp);
        dashboardFragment.setArguments(data);

        traineeListFragment = new TraineeListFragment();
        ResponseDTO tResp = new ResponseDTO();
        tResp.setTraineeList(response.getTraineeList());
        Bundle data2 = new Bundle();
        data2.putSerializable("response", tResp);
        traineeListFragment.setArguments(data2);
        imageLoader = BohaVolley.getImageLoader(ctx);
        traineeListFragment.setImageLoader(imageLoader);

        traineeCourseListFragment = new TraineeCourseListFragment();
        ResponseDTO dRespx = new ResponseDTO();
        dRespx.setTrainingClassCourseList(response.getTrainingClassCourseList());
        Bundle data3 = new Bundle();
        data3.putSerializable("response", dRespx);
        traineeCourseListFragment.setArguments(data3);


        pageList.add(dashboardFragment);
        pageList.add(traineeListFragment);
        pageList.add(traineeCourseListFragment);


        initializePager();


    }

    private void setTraineeSkillsFragment() {
        traineeSkillsFragment = new TraineeSkillsFragment();

        if (me != null) {
            Bundle b4 = new Bundle();
            b4.putSerializable("trainee", me);
            traineeSkillsFragment.setArguments(b4);
        }
        if (traineeSkillsFragment != null) {
            pageList.add(traineeSkillsFragment);
            mAdapter.notifyDataSetChanged();
        }

    }

    ImageLoader imageLoader;

    public void initializePager() {
        mAdapter = new PagerAdapter(getSupportFragmentManager());

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

    @Override
    public void onAddSkillsRequest(TraineeDTO trainee) {
        //not required
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
            String title = "";
            switch (position) {
                case 0:
                    title = ctx.getResources().getString(R.string.dashboard);
                    break;
                case 1:
                    title = ctx.getResources().getString(R.string.classmates);
                    break;
                case 2:
                    title = ctx.getResources().getString(R.string.class_courses);
                    break;
                case 4:
                    title = ctx.getResources().getString(R.string.trainee_profile);
                    break;
                case 3:
                    title = ctx.getResources().getString(R.string.class_teams);
                    break;
                case 5:
                    title = ctx.getResources().getString(R.string.skills);
                    break;


                default:
                    break;
            }
            return title;
        }
    }

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
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_pager, menu);
        mMenu = menu;
        if (response == null) {
            refreshTraineeData();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshTraineeData();
                return true;
            case R.id.menu_exit:
                finish();
                return true;
            case R.id.menu_profile:
                Intent i = new Intent(this, TraineeProfileActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.w(LOG, "onSaveInstanceState");
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    @Override
    public void onResume() {
        Log.e(LOG, "onResume ...nuthin be done");
        super.onResume();

    }

    @Override
    public void onDestroy() {
        Log.e(LOG, "###### onDestroy - closing websocket session");
        WebSocketUtil.disconnectSession();
        super.onDestroy();
    }

    TraineeDTO trainee;
    Menu mMenu;
    ResponseDTO response;
    TraineeSkillsFragment traineeSkillsFragment;
    DashboardFragment dashboardFragment;
    TraineeListFragment traineeListFragment;
    TraineeCourseListFragment traineeCourseListFragment;
    TraineeProfileFragment traineeProfileFragment;
    TeamListFragment teamListFragment;
    int currentPageIndex;
    ViewPager mPager;
    List<PageInterface> pageList = new ArrayList<PageInterface>();
    Context ctx;
    PagerAdapter mAdapter;
    static final String LOG = "MainPagerActivity";

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);

    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);

    }

    @Override
    public void onCoursePicked(TrainingClassCourseDTO course) {
        int cnt = 0;

        if (course.getCourseTraineeActivityList() == null || course.getCourseTraineeActivityList().size() == 0) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.no_activities));
            return;
        }
        Intent i = new Intent(ctx, ActivityListActivity.class);
        i.putExtra("course", course);
        i.putExtra("type", ActivityListActivity.TRAINEE);
        i.putExtra("trainee", SharedUtil.getTrainee(ctx));
        startActivityForResult(i, 1);
    }

    private List<TraineeDTO> traineeList;

    @Override
    public void onTeamPicked(TeamDTO team) {
        Intent i = new Intent(ctx, TeamMemberActivity.class);
        ResponseDTO r = new ResponseDTO();
        r.setTeam(team);
        r.setTraineeList(traineeList);
        i.putExtra("response", r);
        startActivityForResult(i, START_TEAM_MEMBER_ACTIVITY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(LOG, "--- *** onActivityResult resultCode = " + resultCode + " requestCode = " + requestCode);

        switch (requestCode) {
            case START_TEAM_MEMBER_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    ResponseDTO r = (ResponseDTO) data.getSerializableExtra("response");
                    Log.i(LOG, "-- response from activityResult, members: " + r.getTeamMemberList());
                    teamListFragment.addTeamMembers(r.getTeamMemberList());
                }
                break;
            case TraineeProfileFragment.CAPTURE_IMAGE:
                Log.w(LOG, "back from camera, should resize and store");
                if (resultCode == Activity.RESULT_OK) {
                    ImageTask.getResizedBitmaps(fileUri, ctx, new BitmapListener() {
                        @Override
                        public void onError() {
                            Log.e(LOG, "failed to resize resize bitmap");
                            ToastUtil.errorToast(
                                    ctx,
                                    ctx.getResources().getString(
                                            R.string.error_image_get));
                        }

                        @Override
                        public void onBitmapsResized(Bitmaps bitmaps) {
                            Log.i(LOG, "resizedimage, set on imageview");
                            traineeProfileFragment.setImage(bitmaps.getLargeBitmap());
                        }
                    });
                } else {
                    Log.w(LOG, "back from camera, activity result code not OK");
                    ToastUtil.toast(
                            ctx,
                            ctx.getResources().getString(
                                    R.string.image_capture_cancelled));
                }
                break;
            case TraineeProfileFragment.PICK_IMAGE:
                Log.w(LOG, "back from picking image, should resize and store");
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(LOG, "activity result is OK - shoud store pic");
                    fileUri = data.getData();
                    ImageTask.getResizedBitmaps(fileUri, ctx, new BitmapListener() {
                        @Override
                        public void onError() {
                            Log.e(LOG, "onError - resize bitmap failed");
                            ToastUtil.errorToast(
                                    ctx,
                                    ctx.getResources().getString(
                                            R.string.error_image_get));
                        }

                        @Override
                        public void onBitmapsResized(Bitmaps bitmaps) {
                            Log.i(LOG, "image resized and set to imageView");
                            traineeProfileFragment.setImage(bitmaps.getLargeBitmap());
                        }
                    });

                } else {
                    Log.e(LOG, "back from picking image, activity result code is not OK");
                    ToastUtil.toast(
                            ctx,
                            ctx.getResources().getString(
                                    R.string.image_pick_cancelled));
                }

                break;
        }
    }

    private void setTraineeProfileFragment() {
        traineeProfileFragment = new TraineeProfileFragment();
        ResponseDTO dRespx = new ResponseDTO();
        dRespx.setProvinceList(provinceList);
        Bundle data3 = new Bundle();
        data3.putSerializable("response", dRespx);
        traineeProfileFragment.setArguments(data3);
        pageList.add(traineeProfileFragment);
        mAdapter.notifyDataSetChanged();
    }


    private void setTeamListFragment() {
        teamListFragment = new TeamListFragment();
        ResponseDTO tResp2 = new ResponseDTO();
        tResp2.setTraineeList(response.getTraineeList());
        tResp2.setTeamList(response.getTeamList());
        Bundle data22 = new Bundle();
        data22.putSerializable("response", tResp2);
        teamListFragment.setArguments(data22);
        pageList.add(teamListFragment);
        mAdapter.notifyDataSetChanged();
    }

    List<TeamDTO> teamList;
    List<ProvinceDTO> provinceList;
    static final int START_TEAM_MEMBER_ACTIVITY = 333;
    TeamMemberListFragment teamMemberListFragment;

    @Override
    public void onCameraRequest(int width, int height) {
        startCameraIntent(width, height);

    }

    @Override
    public void onGalleryRequest() {
        startGalleryIntent();

    }

    Uri fileUri;

    private void startCameraIntent(int width, int height) {
        fileUri = PictureUtil.getImageFileUri();
        Intent cameraIntent = getCameraIntent(width, height, fileUri);
        startActivityForResult(cameraIntent, TraineeProfileFragment.CAPTURE_IMAGE);
    }

    private void startGalleryIntent() {
        fileUri = PictureUtil.getImageFileUri();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, TraineeProfileFragment.PICK_IMAGE);

    }

    public static Intent getCameraIntent(int width, int height, Uri fileUri) {


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("crop", "true");
        cameraIntent.putExtra("outputX", width);
        cameraIntent.putExtra("outputY", height);
        cameraIntent.putExtra("aspectX", 1);
        cameraIntent.putExtra("aspectY", 1);
        cameraIntent.putExtra("scale", true);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        return cameraIntent;

    }
}
