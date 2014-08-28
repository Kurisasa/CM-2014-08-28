package com.boha.cmadmin.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmadmin.ClassTraineeListActivity;
import com.boha.cmadmin.R;
import com.boha.cmadmin.adapter.TrainingClassAdapter;
import com.boha.cmadmin.listeners.ClassAddedUpdatedListener;
import com.boha.cmadmin.listeners.ContextMenuInterface;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.Util;
import com.boha.coursemaker.util.WebSocketUtil;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ClassListFragment extends Fragment implements PageInterface {

	Context ctx;
	View view;
	private BusyListener busyListener;
	private ClassAddedUpdatedListener classAddedListener;
	private ContextMenuInterface contextListener;

	public ClassListFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		Log.d(LOG, "---- onAttach ");
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity must implement BusyListener");
		}
		if (a instanceof ClassAddedUpdatedListener) {
			classAddedListener = (ClassAddedUpdatedListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity must implement ClassAddedListener");
		}
		if (a instanceof ContextMenuInterface) {
			contextListener = (ContextMenuInterface) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity must implement ContextMenuInterface");
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
		Log.d(LOG, "---- onCreateView");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_class_list, container, false);
		setFields();
		Bundle b = getArguments();
		response = (ResponseDTO) b.getSerializable("response");
		trainingClassList = response.getTrainingClassList();
		company = SharedUtil.getCompany(ctx);
		administrator = SharedUtil.getAdministrator(ctx);
		setList();
		return view;
	}

	@Override
	public void onResume() {
		Log.d(LOG, "---- onResume - nothing to be done");
		super.onResume();

	}

	private void openClassForm() {
		Log.w(LOG, "openClassForm - opening ...");
		classEditView.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		classEditView.startAnimation(a);
	}

	List<TrainingClassDTO> trainingClassList;


	public void addNewClass() {
		currentOperation = ADD_NEW;
		editClassName.setText("");
		openClassForm();
	}

	void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		if (trainingClassList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
		}
		if (trainingClassList.size() == 0) {
			addNewClass();
		}

		adapter = new TrainingClassAdapter(getActivity(),
				R.layout.training_class_item, trainingClassList);

		txtCount.setText("" + trainingClassList.size());
		listView.setAdapter(adapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				trainingClass = trainingClassList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				trainingClass = trainingClassList.get(arg2);
				return false;
			}
		});
	}

	static final String LOG = "ClassListFragment";

	private void sendClassData() {
		if (editClassName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getActivity(),
					ctx.getResources().getString(R.string.enter_class_name));
			return;
		}
		
		RequestDTO req = new RequestDTO();

		req.setAdministratorID(administrator.getAdministratorID());
		req.setCompanyID(company.getCompanyID());

		TrainingClassDTO tc = new TrainingClassDTO();
		tc.setTrainingClassName(editClassName.getText().toString());
		tc.setCompanyID(company.getCompanyID());
		tc.setStartDate(startDate);
		tc.setEndDate(endDate);
		req.setTrainingClass(tc);
		Log.i(LOG, "sendClassData currentOperation: " + currentOperation);
		switch (currentOperation) {
		case ADD_NEW:
			req.setRequestType(RequestDTO.ADD_TRAINING_CLASS);
			break;
		case UPDATE:
			req.setRequestType(RequestDTO.UPDATE_CLASS);
			break;
		case DELETE:
			req.setRequestType(RequestDTO.DELETE_CLASS);
			req.setTrainingClassID(trainingClass.getTrainingClassID());
			req.setTrainingClass(null);
			break;

		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
		btnSave.setVisibility(View.GONE);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        btnSave.setVisibility(View.VISIBLE);
                        response = r;
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        switch (currentOperation) {
                            case ADD_NEW:
                                Log.w(LOG,
                                        " new class added - telling: classAddedListener.onClassAdded.");
                                // trainingClassList.add(0,
                                // response.getTrainingClass());
                                classAddedListener.onClassAdded(response
                                        .getTrainingClass());
                                break;
                            case UPDATE:
                                classAddedListener.onClassUpdated(response
                                        .getTrainingClass());
                                break;
                            case DELETE:
                                classAddedListener.onClassDeleted(response
                                        .getTrainingClass());
                                break;

                            default:
                                break;
                        }

                        adapter.notifyDataSetChanged();
                        txtCount.setText("" + trainingClassList.size());
                        Animation an = AnimationUtils.loadAnimation(ctx,
                                R.anim.push_down_out);
                        an.setDuration(600);
                        an.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                classEditView.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                            }
                        });
                        classEditView.startAnimation(an);
                    }
                });
            }

            @Override
            public void onClose() {
                busyListener.setNotBusy();
            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						busyListener.setNotBusy();
//						btnSave.setVisibility(View.VISIBLE);
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						busyListener.setNotBusy();
//						btnSave.setVisibility(View.VISIBLE);
//						response = r;
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, response.getMessage());
//							return;
//						}
//						switch (currentOperation) {
//						case ADD_NEW:
//							Log.w(LOG,
//									" new class added - telling: classAddedListener.onClassAdded.");
//							// trainingClassList.add(0,
//							// response.getTrainingClass());
//							classAddedListener.onClassAdded(response
//									.getTrainingClass());
//							break;
//						case UPDATE:
//							classAddedListener.onClassUpdated(response
//									.getTrainingClass());
//							break;
//						case DELETE:
//							classAddedListener.onClassDeleted(response
//									.getTrainingClass());
//							break;
//
//						default:
//							break;
//						}
//
//						adapter.notifyDataSetChanged();
//						txtCount.setText("" + trainingClassList.size());
//						Animation an = AnimationUtils.loadAnimation(ctx,
//								R.anim.push_down_out);
//						an.setDuration(600);
//						an.setAnimationListener(new Animation.AnimationListener() {
//
//							@Override
//							public void onAnimationStart(Animation animation) {
//							}
//
//							@Override
//							public void onAnimationRepeat(Animation animation) {
//							}
//
//							@Override
//							public void onAnimationEnd(Animation animation) {
//
//								classEditView.setVisibility(View.GONE);
//								listView.setVisibility(View.VISIBLE);
//							}
//						});
//						classEditView.startAnimation(an);
//					}
//				});

	}


	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editClassName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editClassName.getWindowToken(), 0);
	}

	private void setFields() {
		classEditView = view.findViewById(R.id.CLS_editor);
		classEditView.setVisibility(View.GONE);
		
		startLayout = view.findViewById(R.id.EC_startLayout);
		endLayout = view.findViewById(R.id.EC_endLayout);
		
		editClassName = (EditText) view.findViewById(R.id.EC_className);
		txtStartDate  = (Button) view.findViewById(R.id.EC_txtStart);
		txtEndDate = (Button) view.findViewById(R.id.EC_txtEnd);
		txtStartDate.setText(ctx.getResources().getString(R.string.start_date));
		txtEndDate.setText(ctx.getResources().getString(R.string.end_date));
		
		txtCount = (TextView) view.findViewById(R.id.CLS_count);
		TextView txtHdr = (TextView) view.findViewById(R.id.CLS_label);
		txtHdr.setText(SharedUtil.getCompany(ctx).getCompanyName());

		listView = (ListView) view.findViewById(R.id.CLS_list);
		btnCancel = (Button) view.findViewById(R.id.EC_btnCancel);
		btnSave = (Button) view.findViewById(R.id.EC_btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				sendClassData();
			}
		});
        txtStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isFrom = true;
                hideKeyboard();
                showDateDialog();
            }
        });
        txtEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isFrom = false;
                hideKeyboard();
                showDateDialog();
            }
        });
		startLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isFrom = true;
				hideKeyboard();
				showDateDialog();
			}
		});
		endLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isFrom = false;
				hideKeyboard();
				showDateDialog();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyboard();
				editClassName.setText("");

				Animation an = AnimationUtils.loadAnimation(ctx,
						R.anim.options_panel_exit);
				an.setDuration(400);
				an.setAnimationListener(new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						classEditView.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);

					}
				});
				classEditView.startAnimation(an);
			}
		});

	}
	long startDate, endDate;
	private void showDateDialog() {
		
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
				new OnDateSetListener() {

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
							txtStartDate.setText(Util.getLongDate(mDay, mMonth,
									mYear));
							txtEndDate.setText(Util.getLongDate(tDay, tMonth,
									tYear));
							startDate = Util.getSimpleDate(mDay, mMonth, mYear);
							endDate = Util.getSimpleDate(tDay, tMonth, tYear);
						} else {
							tYear = year;
							tMonth = month;
							tDay = day;
							txtEndDate.setText(Util.getLongDate(tDay, tMonth,
									tYear));
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
		datePickerDialog.show(fragmentManager, DATEPICKER_TAG);

	}

	FragmentManager fragmentManager;
	public void setFragmentManager(FragmentManager v) {
		fragmentManager = v;
	}
	ContextMenu contextMenu;
	public static final String DATEPICKER_TAG = "datepicker";
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.class_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		trainingClass = trainingClassList.get(info.position);
        menu.setHeaderTitle(trainingClass.getTrainingClassName());
        menu.setHeaderIcon(R.drawable.ic_action_edit);
		if (trainingClass.getTraineeList() == null || trainingClass.getTraineeList().isEmpty() ) {
			MenuItem item = menu.findItem(R.id.menu_trainees);
            item.setVisible(false);
		}
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		Log.w(LOG,
				"onContextItemSelected - select option ..." + item.getTitle());
		if (item.getItemId() == R.id.menu_update_trainee
				|| item.getItemId() == R.id.menu_delete_trainee
				|| item.getItemId() == R.id.menu_equipment_trainee) {
			contextListener.redirectMenuItem(item);
			return false;
		}
		switch (item.getItemId()) {
		case R.id.menu_delete_class:
			currentOperation = DELETE;
			editClassName.setText(trainingClass.getTrainingClassName());
			openClassForm();
			return true;
		case R.id.menu_update_class:
			currentOperation = UPDATE;
			editClassName.setText(trainingClass.getTrainingClassName());
            txtStartDate.setText(Util.getLongDate(new Date(trainingClass.getStartDate())));
            txtEndDate.setText(Util.getLongDate(new Date(trainingClass.getEndDate())));
			openClassForm();
			return true;
		case R.id.menu_equipment_class:
			underConstruction();
			return true;
		case R.id.menu_trainees:
			startClassTraineeList();
			return true;
		case R.id.menu_deactivate_class:
			underConstruction();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EE, dd MM yyyy",loc);
	private void startClassTraineeList() {
		ResponseDTO r = new ResponseDTO();
		r.setTrainingClass(trainingClass);
		r.setProvinceList(response.getProvinceList());
		r.setTrainingClassList(response.getTrainingClassList());
		Intent i = new Intent(ctx, ClassTraineeListActivity.class);
		i.putExtra("response", r);
		startActivity(i);
	}
	private void underConstruction() {
		ToastUtil.toast(ctx, "Feature under construction. Watch the space!");
	}

	DatePicker startPicker, endPicker;
	TrainingClassAdapter adapter;
	TrainingClassDTO trainingClass;
	ResponseDTO response;
	CompanyDTO company;
	AdministratorDTO administrator;
	View classEditView;
	EditText editClassName;
	Button btnSave, btnCancel;
	LinearLayout layoutEquip;
	TextView txtCount;
    Button txtStartDate, txtEndDate;
	ListView listView;
	int currentOperation;
	static final int ADD_NEW = 1, UPDATE = 2, DELETE = 3;
	private int mYear, mMonth, mDay, tYear, tMonth, tDay;
	DatePickerDialog datePickerDialog;
	boolean isFrom;
	View startLayout, endLayout;
	@Override
	public void dummy() {
		// TODO Auto-generated method stub

	}

}
