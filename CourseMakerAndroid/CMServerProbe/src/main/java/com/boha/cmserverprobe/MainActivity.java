package com.boha.cmserverprobe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.boha.cmserverprobe.adapters.ErrorStoreAdapter;
import com.boha.coursemaker.base.BaseStatsVolley;
import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.CompanyStatsDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.Util;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends FragmentActivity {
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.error_list);
		ctx = getApplicationContext();
		setFields();
		setTitle(ctx.getResources().getString(R.string.err_title));
		if (savedInstanceState != null) {
			response = (StatsResponseDTO)savedInstanceState.getSerializable("response");
			if (response != null) {
				Log.i(LOG, "onCreate..., restored instance state");
				setList();
			}
		}
	}

	private void getErrorData() {
		
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_ERROR_LIST);
		req.setZippedResponse(true);
		setRefreshActionButtonState(true);
		
		BaseStatsVolley.getRemoteData(Statics.SERVLET_PLATFORM, req, ctx, new BaseStatsVolley.BohaVolleyListener() {
			
			@Override
			public void onVolleyError(VolleyError error) {
				setRefreshActionButtonState(false);
				ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));			
			}
			
			@Override
			public void onResponseReceived(StatsResponseDTO r) {
				setRefreshActionButtonState(false);
				response = r;
				if (r.getStatusCode() > 0) {
					ToastUtil.errorToast(ctx, r.getMessage());
					return;
				}
				setList();
				
			}
		});
	}
	private void setList() {
		if (response == null) return;

		adapter = new ErrorStoreAdapter(getApplicationContext(), R.layout.error_item, response.getErrorStoreList());
		list.setAdapter(adapter);
		
		txtCount.setText(df.format(response.getErrorStoreList().size()));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//companyStats = response.getStatsList().get(arg2);
				
				
			}
		});
	}
    private void showDateDialog() {
        fragmentManager = getSupportFragmentManager();
        final Calendar calendar = Calendar.getInstance();
        int xYear, xMth, xDay;
        if (isFrom) {
            if (mYear == 0) {
                xYear = calendar.get(Calendar.YEAR);
                xMth = calendar.get(Calendar.MONTH);
                xDay = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                xYear = mYear;
                xMth = mMonth;
                xDay = mDay;
            }
        } else {
            if (tYear == 0) {
                xYear = calendar.get(Calendar.YEAR);
                xMth = calendar.get(Calendar.MONTH);
                xDay = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                xYear = tYear;
                xMth = tMonth;
                xDay = tDay;
            }
        }

        datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog,
                                          int year, int month, int day) {
                        if (isFrom) {
                            mYear = year;
                            mMonth = month;
                            mDay = day;
                            // update date to
                            tDay = mDay;
                            tMonth = mMonth;
                            tYear = mYear;
                           // txtStartDate.setText(Util.getLongDate(mDay, mMonth,
                            //        mYear));
                            //txtEndDate.setText(Util.getLongDate(tDay, tMonth,
                              //      tYear));
                            startDate = Util.getSimpleDate(mDay, mMonth, mYear);
                            endDate = Util.getSimpleDate(tDay, tMonth, tYear);
                        } else {
                            tYear = year;
                            tMonth = month;
                            tDay = day;
                            //txtEndDate.setText(Util.getLongDate(tDay, tMonth,
                            //        tYear));
                            endDate = Util.getSimpleDate(tDay, tMonth, tYear);
                            //check dates
                            if (mYear > 0 && tYear > 0) {
                                Calendar calFrom = GregorianCalendar.getInstance();
                                calFrom.set(mYear, mMonth, mDay);
                                Date f = calFrom.getTime();
                                Calendar calTo = GregorianCalendar.getInstance();
                                calTo.set(tYear, tMonth, tDay);
                                Date t = calTo.getTime();

                                if (t.before(f)) {
                                    ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.date_end_wrong));
                                } else {
                                }
                            }
                        }

                    }
                }, xYear, xMth, xDay, true);

        datePickerDialog.setVibrate(true);
        datePickerDialog.setYearRange(2010, 2036);
        datePickerDialog.show(fragmentManager, DATE_PICKER_TAG);

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
		getMenuInflater().inflate(R.menu.main, menu);
		mMenu = menu;
		if (response == null)
			getErrorData();
		return true;
	}
	Menu mMenu;
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			getErrorData();
			break;
		case R.id.menu_log:
			Intent i = new Intent(ctx, LogActivity.class);
			startActivity(i);
			break;
		case R.id.menu_stats:
			Intent ix = new Intent(ctx, CompanyPagerActivity.class);
			startActivity(ix);
			break;
            case R.id.menu_android_crash:
                Intent iz = new Intent(ctx, AndroidCrashActivity.class);
                iz.putExtra("response", response);
                startActivity(iz);
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
		txtCount = (TextView)findViewById(R.id.E_count);
		list = (ListView)findViewById(R.id.E_list);

        txtCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFrom = true;
                showDateDialog();
            }
        });
	}
	ErrorStoreAdapter adapter;
	TextView txtCount;
	ListView list;
	CompanyStatsDTO companyStats;
	StatsResponseDTO response;
	static final DecimalFormat df = new DecimalFormat("###,###,###,###");
	static final String LOG = "MainActivity";
    FragmentManager fragmentManager;
    public void setFragmentManager(FragmentManager v) {
        fragmentManager = v;
    }
    private static final String DATE_PICKER_TAG = "datePicker";
    private int mYear, mMonth, mDay, tYear, tMonth, tDay;
    DatePickerDialog datePickerDialog;
    TextView txtStartDate, txtEndDate;
    boolean isFrom;
    long endDate, startDate;
	
}
