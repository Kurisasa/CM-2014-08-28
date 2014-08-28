package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.cminstructor.R;
import com.boha.cminstructor.adapters.EventAdapter;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.dto.TrainingClassEventDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarEventListFragment extends Fragment implements
		PageInterface {

	public CalendarEventListFragment() {
	}

	static final String LOG = "CalendarEventListFragment";

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host "+a.getLocalClassName()+" must implement BusyListener");
		}
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	ResponseDTO response;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_calendar_event_list,
				container, false);
		setFields();
		instructor = SharedUtil.getInstructor(ctx);
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO)b.getSerializable("response");
			trainingClassList = response.getTrainingClassList();
			instructorClass = (InstructorClassDTO)b.getSerializable("instructorClass");
			setClassSpinner();
			setSelected();
		}
		
		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "############### resuming in " + LOG);
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
		super.onSaveInstanceState(state);
	}

	private void getEvents() {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_EVENTS_BY_CLASS);
		r.setTrainingClassID(trainingClass.getTrainingClassID());
		//
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        trainingClassEventList = response.getTrainingClassEventList();
                        setList();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
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
//			@Override
//			public void onVolleyError(VolleyError error) {
//				busyListener.setNotBusy();
//
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO response) {
//				busyListener.setNotBusy();
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, response.getMessage());
//					return;
//				}
//				trainingClassEventList = response.getTrainingClassEventList();
//				setList();
//			}
//		});
	}
	List<TrainingClassDTO> trainingClassList;
	TrainingClassDTO trainingClass;
	private void setClassSpinner() {
		
		
		final ArrayList<String> tarList = new ArrayList<String>();
		if (trainingClassList != null) {
			for (TrainingClassDTO p : trainingClassList) {
				tarList.add(p.getTrainingClassName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			spinner.setPrompt(ctx.getResources().getString(R.string.select_class));
			spinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							
							trainingClass = trainingClassList.get(arg2);
							getEvents();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}

	}
	private void setSelected() {
		int index = 0;
		for (TrainingClassDTO tc : trainingClassList) {
			if (tc.getTrainingClassID() == instructorClass.getTrainingClassID()) {
				spinner.setSelection(index);
				trainingClass = tc;
				return;
			}
			index++;
		}
	}
	private void setFields() {
		instructor = SharedUtil.getInstructor(ctx);
		spinner = (Spinner) view.findViewById(R.id.CEV_spinnerClass);
		listView = (ListView) view.findViewById(R.id.CEV_listView);
		txtCount = (TextView) view.findViewById(R.id.CEV_count);

		setClassSpinner();

	}

	private void setList() {
		adapter = new EventAdapter(ctx, R.layout.event_item, trainingClassEventList);
		listView.setAdapter(adapter);
		txtCount.setText("" + trainingClassEventList.size());
		if (trainingClassEventList.size() == 0) {
			ToastUtil.toast(ctx, getResources().getString(R.string.no_events_found));
			return;
		}
		registerForContextMenu(listView);
		listView.setSelection(getNearestEventToToday());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				return false;
			}
		});
	}

	Context ctx;
	View view;

	List<TrainingClassEventDTO> trainingClassEventList;
	EventAdapter adapter;
	ListView listView;
	TextView txtClassName, txtCount;
	InstructorClassDTO instructorClass;
	InstructorDTO instructor;
	BusyListener busyListener;

	Spinner spinner;

	
	private int getNearestEventToToday() {
	
		int index = 0;
		for (TrainingClassEventDTO ce : trainingClassEventList) {
			if (isWeek(ce.getStartDate())) {
				return index;
			}
			index++;
		}
		
		
		return index;
	}

	private boolean isWeek(long date) {		
		Calendar c = GregorianCalendar.getInstance();
		Calendar c2 = GregorianCalendar.getInstance();
		c2.setTimeInMillis(date);
		
		int wk = c.get(Calendar.WEEK_OF_YEAR);
		int wk2 = c2.get(Calendar.WEEK_OF_YEAR);
		if (wk == wk2) {
			return true;
		}
		return false;
	}
	TrainingClassEventDTO trainingClassEvent;
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", loc);
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.event_context, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		trainingClassEvent = trainingClassEventList.get(info.position);
		Date from = new Date(trainingClassEvent.getStartDate());
		Date to = new Date(trainingClassEvent.getEndDate());
		menu.setHeaderTitle(trainingClassEvent.getEventName() + "\n"
				+ sdf.format(from) 
				+ " - "
				+ sdf.format(to));
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.cal1_32));
		super.onCreateContextMenu(menu, v, menuInfo);

	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected: " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_change:
			ToastUtil.toast(ctx, "Feature under construction! Watch this space!!");
			return true;
		case R.id.menu_delete:
			ToastUtil.toast(ctx, "Feature under construction! Watch this space!!");
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}
}
