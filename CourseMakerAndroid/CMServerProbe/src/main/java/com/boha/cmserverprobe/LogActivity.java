package com.boha.cmserverprobe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.boha.coursemaker.base.BaseStatsVolley;
import com.boha.coursemaker.base.BaseStatsVolley.BohaVolleyListener;
import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;

public class LogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logger);
		ctx = getApplicationContext();
		setFields();
		setTitle("CourseMaker Logs");
	}

	private void getLogData() {
		
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_SERVER_LOG);
		//
		
		setRefreshActionButtonState(true);
		start = System.currentTimeMillis();
		BaseStatsVolley.getRemoteData(Statics.SERVLET_LOG, r, ctx, new BohaVolleyListener() {
			
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
				setLogFields();
			}
		});
	}
	private void setLogFields() {
		
		txtLog.setText(response.getLogString());
		long size = response.getLogString().length();
		long kb = size / 1024;
		txtSize.setText(df.format(kb) + " KB");
		txtElapsed.setText(df2.format(getElapsed(start, end)) + " sec");
		txtDate.setText(sdf.format(new Date()));
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log, menu);
		mMenu = menu;
		if (response == null)
			getLogData();
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
		txtDate = (TextView)findViewById(R.id.LOG_date);
		txtSize  = (TextView)findViewById(R.id.LOG_size);
		txtLog  = (TextView)findViewById(R.id.LOG_logTxt);
		txtElapsed  = (TextView)findViewById(R.id.LOG_elapsed);
		
		txtDate.setText(sdf.format(new Date()));
		
	}
	 public static double getElapsed(long start, long end) {
	        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));
	        return m.doubleValue();
	    }
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
