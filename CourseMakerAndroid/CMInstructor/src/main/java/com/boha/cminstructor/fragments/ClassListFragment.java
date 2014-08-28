package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cminstructor.AssignCoursesToClassActivity;
import com.boha.cminstructor.CalendarActivity;
import com.boha.cminstructor.R;
import com.boha.cminstructor.adapters.ClassAdapter;
import com.boha.cminstructor.listeners.ClassListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TotalsDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ClassListFragment extends Fragment implements PageInterface {

	public ClassListFragment() {
	}

	static final String LOG = "ClassListFragment";
	BusyListener busyListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof ClassListener) {
			listener = (ClassListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Activity must implement ClassListener");
		}
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Activity must implement BusyListener");
		}
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		Log.e(LOG, "--- onCreateView");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_class_list, container, false);
		setFields();
		Bundle b = getArguments();
		response = (ResponseDTO) b.getSerializable("response");
		setData();
		

		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "############### resuming in " + LOG);
		summarize();

		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG,
				"##### onSaveInstanceState  fired, not saving anything! ...."
						+ LOG);
		super.onSaveInstanceState(state);
	}

	List<TotalsDTO> totalsList;

	private void summarize() {
		if (instructorClassList != null) {
			for (InstructorClassDTO cls : instructorClassList) {
				getClassTotals(cls);
			}
			setList();
		}
	}

	private void setData() {		
		instructorClassList = response.getInstructor()
				.getInstructorClassList();
		totalsList = response.getTotals();
		summarize();

	}

	public void updateActivityEnrolment(int trainingClassID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ENROLL_IN_COURSE);
		r.setTrainingClassID(trainingClassID);
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.INSTRUCTOR_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        Log.i(LOG,
                                "Trainee activity enrolment update done, msg = "
                                        + r.getMessage());
                        response = r;
                        listener.onRefreshRequested();
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
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, r, ctx,
//				new BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						busyListener.setNotBusy();
//						Log.e(LOG, "Trainee activity enrolment update failed: "
//								+ error.getMessage());
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						busyListener.setNotBusy();
//						Log.i(LOG,
//								"Trainee activity enrolment update done, msg = "
//										+ r.getMessage());
//						response = r;
//						listener.onRefreshRequested();
//
//					}
//				});
	}

	ResponseDTO response;

	private void getClassTotals(InstructorClassDTO cls) {

		int totTask = 0, totComp = 0;
		for (TotalsDTO t : totalsList) {
			if (cls.getTrainingClassName().equalsIgnoreCase(
					t.getTrainingClassName())) {
				totTask += t.getTotalTasks();
				totComp += t.getTotalComplete();
			}
		}
		cls.setTotalComplete(totComp);
		cls.setTotalTasks(totTask);
		if (totTask > 0) {
			BigDecimal p = new BigDecimal(totComp).divide(
					new BigDecimal(totTask), 4, RoundingMode.HALF_EVEN)
					.multiply(new BigDecimal(100));
			cls.setPercComplete(p.doubleValue());
		}
	}

	private void setFields() {
	
		listView = (ListView) view.findViewById(R.id.CRS_listView);
		txtCount = (TextView) view.findViewById(R.id.LH_count);

	}

	private void setList() {

		if (instructorClassList == null) {
			Log.w(LOG, "instructorList is null");
			return;
		}
		ctx = getActivity();
		adapter = new ClassAdapter(getActivity(), R.layout.class_item,
				instructorClassList, new ClassAdapter.ClassListListener() {
            @Override
            public void onCalendarRequest(InstructorClassDTO ic) {
                instructorClass = ic;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    ToastUtil.toast(ctx, ctx.getResources().getString(R.string.version_problem));
                    return;
                }
                Intent ix = new Intent(ctx, CalendarActivity.class);
                ix.putExtra("instructorClass", instructorClass);
                startActivity(ix);
            }

            @Override
            public void onRefreshRequest(InstructorClassDTO ic) {
                instructorClass = ic;
                updateActivityEnrolment(ic.getTrainingClassID());
            }

            @Override
            public void onAssignCoursesRequest(InstructorClassDTO ic) {
                instructorClass = ic;
                Intent i = new Intent(ctx, AssignCoursesToClassActivity.class);
                i.putExtra("classID", ic.getTrainingClassID());
                startActivityForResult(i, ASSIGN_COURSES);
            }
        });

		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText("" + instructorClassList.size());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				instructorClass = instructorClassList.get(arg2);
				
				listener.onClassPicked(instructorClass);

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

    public List<InstructorClassDTO> getInstructorClassList() {
        return instructorClassList;
    }

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.w(LOG, "onCreateContextMenu ...");
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.class_contextual, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		instructorClass = instructorClassList.get(info.position);
		menu.setHeaderTitle(instructorClass.getTrainingClassName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.clipboard32));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected: " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_calendar:
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				ToastUtil.toast(ctx, ctx.getResources().getString(R.string.version_problem));
				return true;
			}
			Intent ix = new Intent(ctx, CalendarActivity.class);
			ix.putExtra("instructorClass", instructorClass);
			startActivity(ix);
			return true;
		case R.id.menu_refresh_atcivities:
			updateActivityEnrolment(instructorClass.getTrainingClassID());
			return true;
		case R.id.menu_assign_courses:
			Intent i = new Intent(ctx, AssignCoursesToClassActivity.class);
			i.putExtra("classID", instructorClass.getTrainingClassID());
			startActivityForResult(i, ASSIGN_COURSES);

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if courses added, refresh activities
		if (requestCode == ASSIGN_COURSES) {
			if (resultCode == Activity.RESULT_OK) {
				RequestDTO req = new RequestDTO();
				req.setRequestType(RequestDTO.ENROLL_IN_COURSE);
				req.setTrainingClassID(instructorClass.getTrainingClassID());
				if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
				busyListener.setBusy();
                WebSocketUtil.sendRequest(ctx, Statics.INSTRUCTOR_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
                    @Override
                    public void onMessage(final ResponseDTO response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.getStatusCode() > 0) {
                                    ToastUtil.errorToast(ctx,
                                            response.getMessage());
                                    return;
                                }
                                listener.onRefreshRequested();
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
                                ToastUtil.errorToast(ctx, message);
                            }
                        });
                    }
                });
//				BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, req, ctx,
//						new BohaVolleyListener() {
//
//							@Override
//							public void onVolleyError(VolleyError error) {
//								ToastUtil
//										.errorToast(
//												ctx,
//												ctx.getResources()
//														.getString(
//																R.string.error_server_comms));
//								busyListener.setNotBusy();
//							}
//
//							@Override
//							public void onResponseReceived(ResponseDTO response) {
//								if (response.getStatusCode() > 0) {
//									ToastUtil.errorToast(ctx,
//											response.getMessage());
//									return;
//								}
//                                listener.onRefreshRequested();
//							}
//						});

			}
		}
	}

	static final int ASSIGN_COURSES = 333;
	List<InstructorClassDTO> instructorClassList;
	InstructorClassDTO instructorClass;
	private ClassListener listener;
	Context ctx;
	View view;

	ClassAdapter adapter;
	ListView listView;
	TextView txtCompanyName, txtTrainee, txtCount;

}
