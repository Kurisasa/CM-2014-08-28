package com.boha.reporter.coursemaker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.ErrorStoreAndroidDTO;
import com.boha.coursemaker.dto.ErrorStoreDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.coursemaker.util.PlatformWebSocketUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.reporter.coursemaker.fragments.AndroidCrashListFragment;
import com.boha.reporter.coursemaker.fragments.PageFragment;
import com.boha.reporter.coursemaker.fragments.ServerLogFragment;
import com.boha.reporter.coursemaker.fragments.SeverEventListFragment;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pager);
        ctx = getApplicationContext();
        mPager = (ViewPager) findViewById(R.id.pager);

    }


    private void getCrashes() {

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_ERROR_LIST);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);

        PlatformWebSocketUtil.sendRequest(ctx, Statics.PLATFORM_ENDPOINT, w, new PlatformWebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final StatsResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        response = r;
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }

                        buildPages();
                        //throw new UnsupportedOperationException("Testing exceptions in Android app");

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
//        BaseStatsVolley.getRemoteData(Statics.SERVLET_PLATFORM, w, ctx, new BaseStatsVolley.BohaVolleyListener() {
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                setRefreshActionButtonState(false);
//                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//            }
//
//            @Override
//            public void onResponseReceived(StatsResponseDTO r) {
//                setRefreshActionButtonState(false);
//                response = r;
//                if (r.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, r.getMessage());
//                    return;
//                }
//                buildPages();
//
//            }
//        });
    }

    private void buildPages() {
        pageFragmentList = new ArrayList<PageFragment>();

        androidCrashListFragment = new AndroidCrashListFragment();
        ResponseDTO r1 = new ResponseDTO();
        Bundle data1 = new Bundle();
        r1.setErrorStoreAndroidList(response.getErrorStoreAndroidList());
        data1.putSerializable("response", r1);
        androidCrashListFragment.setArguments(data1);

        severEventListFragment = new SeverEventListFragment();
        ResponseDTO r2 = new ResponseDTO();
        Bundle data2 = new Bundle();
        r2.setErrorStoreList(response.getErrorStoreList());
        data2.putSerializable("response", r2);
        severEventListFragment.setArguments(data2);

        serverLogFragment = new ServerLogFragment();
        StatsResponseDTO r3 = new StatsResponseDTO();
        Bundle data3 = new Bundle();
        r3.setLogString(response.getLogString());
        data3.putSerializable("response", r3);
        serverLogFragment.setArguments(data3);

        pageFragmentList.add(androidCrashListFragment);
        pageFragmentList.add(severEventListFragment);
        pageFragmentList.add(serverLogFragment);
        initializeAdapter();
    }

    private void initializeAdapter() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
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

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) pageFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return pageFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";

            switch (position) {
                case 0:
                    title = "Mobile Device Crashes";
                    break;
                case 1:
                    title = "Server Events";
                    break;
                case 2:
                    title = "Server Log";
                    break;

                default:
                    break;
            }
            return title;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        mMenu = menu;
        if (response == null) {
            getCrashes();
        }
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                getCrashes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Context ctx;
    ViewPager mPager;
    Menu mMenu;
    StatsResponseDTO response;
    PagerAdapter mPagerAdapter;
    int currentPageIndex;
    List<ErrorStoreAndroidDTO> errorStoreAndroidList;
    List<ErrorStoreDTO> errorStoreList;
    List<PageFragment> pageFragmentList;
    static final String LOG = "EventActivity";
    AndroidCrashListFragment androidCrashListFragment;
    SeverEventListFragment severEventListFragment;
    ServerLogFragment serverLogFragment;
}
