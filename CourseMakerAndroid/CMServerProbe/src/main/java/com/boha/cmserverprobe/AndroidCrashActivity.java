package com.boha.cmserverprobe;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.boha.cmserverprobe.fragments.AndroidCrashListFragment;
import com.boha.coursemaker.base.BaseStatsVolley;
import com.boha.coursemaker.base.BaseStatsVolley.BohaVolleyListener;
import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AndroidCrashActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_crash);
		ctx = getApplicationContext();
        androidCrashListFragment = (AndroidCrashListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
		setFields();
        response = (StatsResponseDTO)getIntent().getSerializableExtra("response");
        androidCrashListFragment.setAdndroidErrorList(response.getErrorStoreAndroidList());
		setTitle("Android Crash Logs");
	}

	private void getLogData() {
		
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_ERROR_LIST);
		//
		
		setRefreshActionButtonState(true);
		start = System.currentTimeMillis();
		BaseStatsVolley.getRemoteData(Statics.SERVLET_PLATFORM, r, ctx, new BohaVolleyListener() {
			
			@Override
			public void onVolleyError(VolleyError error) {
				setRefreshActionButtonState(false);
				end = System.currentTimeMillis();
				txtElapsed.setText(df.format(getElapsed(start, end)));
				
			}
			
			@Override
			public void onResponseReceived(StatsResponseDTO r) {
				setRefreshActionButtonState(false);
				end = System.currentTimeMillis();
				response = r;
				if (r.getStatusCode() > 0) {
					ToastUtil.errorToast(ctx, r.getMessage());
					return;
				}
				androidCrashListFragment.setAdndroidErrorList(r.getErrorStoreAndroidList());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log, menu);
		mMenu = menu;
		//if (response == null)
			//getLogData();
		return true;
	}
	Menu mMenu;
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			getLogData();
			break;
		case R.id.menu_back:
			finish();
			break;

		default:
			break;
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
	private void setFields() {

		
	}
	 public static double getElapsed(long start, long end) {
	        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));
	        return m.doubleValue();
	    }

    AndroidCrashListFragment androidCrashListFragment;

	Context ctx;
	TextView txtDate, txtSize, txtLog, txtElapsed;
	StatsResponseDTO response;
	long start, end;
	static final DecimalFormat df = new DecimalFormat("###,###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###,###,##0.00");
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", loc);
	static final String LOG = "MainActivity";
}
