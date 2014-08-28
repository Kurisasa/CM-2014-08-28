package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.volley.VolleyError;
import com.boha.cminstructor.R;
import com.boha.cminstructor.Util;
import com.boha.cminstructor.listeners.EventAddedListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.CalendarListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.*;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.*;

public class CalendarFragment extends Fragment implements PageInterface {

	public CalendarFragment() {
	}

	static final String LOG = "CalendarFragment";
	EventAddedListener eventAddedListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host "
					+ a.getLocalClassName() + " must implement BusyListener");
		}
		if (a instanceof EventAddedListener) {
			eventAddedListener = (EventAddedListener) a;
		} else {
			throw new UnsupportedOperationException("Host "
					+ a.getLocalClassName()
					+ " must implement EventAddedListener");
		}
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	ResponseDTO response;
	InstructorClassDTO instructorClass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_calendar, container, false);
		setFields();
		instructor = SharedUtil.getInstructor(ctx);
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO) b.getSerializable("response");
			trainingClassList = response.getTrainingClassList();
			instructorClass = (InstructorClassDTO) b
					.getSerializable("instructorClass");
			setClassSpinner();
			setSelected();
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(LOG, "-- onResume  ");
	}

	void toggleEditFields() {
		hideKeyboard();
		if (mLayout.getVisibility() == View.GONE) {
			mLayout.setVisibility(View.VISIBLE);
			btnMore.setText("Close");
		} else {
			mLayout.setVisibility(View.GONE);
			btnMore.setText("More..");
		}
	}

	long startDate, endDate;

	private void submitEvent() {

		if (eTitle.getText().toString() == null
				|| eTitle.getText().toString().equalsIgnoreCase("")) {
			ToastUtil.toast(ctx,
					ctx.getResources().getString(R.string.enter_title));
			return;
		}

		startDate = Util.getSimpleDate(mDay, mMonth, mYear);
		startDate = startDate + (mHour * 60 * 60 * 1000) + (mMin * 60 * 1000);
		endDate = Util.getSimpleDate(tDay, tMonth, tYear);
		endDate = endDate + (tHour * 60 * 60 * 1000) + (tMin * 60 * 1000);

		// TODO - check Util for dealing with recurrence
		// /////// add event to local calendar and to remote site
		CalendarRequest cr = new CalendarRequest();
		cr.setRequestType(CalendarRequest.ADD_EVENT);
		cr.setCalendarID(SharedUtil.getCalendarID(ctx));
		cr.setAccountName(instructor.getEmail());
		cr.setContext(ctx);
		cr.setDateStart(startDate);
		cr.setDateEnd(endDate);
		cr.setTitle(eTitle.getText().toString());
		if (classCourse != null)
			cr.setDescription(classCourse.getCourse().getDescription());
		cr.setAttendeeRequestList(new ArrayList<AttendeeRequest>());
		// attendees
		for (TraineeDTO t : trainingClass.getTraineeList()) {
			AttendeeRequest ar = new AttendeeRequest();
			ar.setAttendeeName(t.getFirstName() + " " + t.getLastName());
			ar.setEmail(t.getEmail());
			cr.getAttendeeRequestList().add(ar);
		}

		CalendarAsyncTask.performTask(cr, new CalendarListener() {

			@Override
			public void onError() {
				Log.e(LOG, "Failed to add event to local calendar");

			}

			@Override
			public void onCalendarTaskDone(CalendarRequest crx) {
				// add reminders
				CalendarRequest cr = new CalendarRequest();
				cr.setRequestType(CalendarRequest.ADD_REMINDER);
				cr.setEventID(crx.getEventID());
				cr.setMinutes(CalendarUtil.DAY);
				cr.setContext(ctx);
				CalendarAsyncTask.performTask(cr, new CalendarListener() {

					@Override
					public void onError() {
					}

					@Override
					public void onCalendarTaskDone(
							CalendarRequest calendarRequest) {
					}

					@Override
					public void onCalendarQueryComplete(
							List<CalendarRequest> calendarRequests) {
					}
				});
				CalendarRequest cr2 = new CalendarRequest();
				cr2.setRequestType(CalendarRequest.ADD_REMINDER);
				cr2.setEventID(crx.getEventID());
				cr2.setMinutes(CalendarUtil.WEEK);
				cr2.setContext(ctx);
				CalendarAsyncTask.performTask(cr2, new CalendarListener() {

					@Override
					public void onError() {
					}

					@Override
					public void onCalendarTaskDone(
							CalendarRequest calendarRequest) {
					}

					@Override
					public void onCalendarQueryComplete(
							List<CalendarRequest> calendarRequests) {
					}
				});
				// notify the server
				RequestDTO d = new RequestDTO();
				d.setRequestType(RequestDTO.ADD_EVENTS);
				TrainingClassEventDTO e = new TrainingClassEventDTO();
				e.setTrainingClassID(trainingClass.getTrainingClassID());

				if (classCourse != null) {
					e.setDescription(classCourse.getCourse().getDescription());
					e.setTrainingClassCourseID(classCourse
							.getTrainingClassCourseID());
				}
				e.setEndDate(endDate);
				e.setStartDate(startDate);
				e.setEventName(eTitle.getText().toString());
				e.setEventID(crx.getEventID());
				e.setLocation(SharedUtil.getCompany(ctx).getCompanyName());
				d.setTrainingClassEvent(e);
				if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
				busyListener.setBusy();
				animateButtonOut();
                WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,d,new WebSocketUtil.WebSocketListener() {
                    @Override
                    public void onMessage(final ResponseDTO response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                busyListener.setNotBusy();
                                animateButtonIn();
                                if (response.getStatusCode() > 0) {
                                    ToastUtil.errorToast(ctx,
                                            response.getMessage());
                                    return;
                                }
                                eTitle.setText("");
                                classCourse = null;
                                ToastUtil.toast(
                                        ctx,
                                        getResources().getString(
                                                R.string.event_sent));
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
				BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, d, ctx,
						new BaseVolley.BohaVolleyListener() {

							@Override
							public void onVolleyError(VolleyError error) {
								busyListener.setNotBusy();
								animateButtonIn();

							}

							@Override
							public void onResponseReceived(ResponseDTO response) {
								busyListener.setNotBusy();
								animateButtonIn();
								if (response.getStatusCode() > 0) {
									ToastUtil.errorToast(ctx,
											response.getMessage());
									return;
								}
								eTitle.setText("");
								classCourse = null;
								ToastUtil.toast(
										ctx,
										getResources().getString(
												R.string.event_sent));

							}
						});

			}

			@Override
			public void onCalendarQueryComplete(
					List<CalendarRequest> calendarRequests) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void animateButtonIn() {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		btnSubmit.setVisibility(View.VISIBLE);
		btnSubmit.startAnimation(a);
	}

	private void animateButtonOut() {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.shrink_fade_out_center);
		a.setDuration(1000);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				btnSubmit.setVisibility(View.GONE);
			}
		});
		btnSubmit.startAnimation(a);
	}

	private FragmentManager fragmentManager;

	public void setFragmentManger(FragmentManager fm) {
		fragmentManager = fm;
	}

	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;

	private void showDateDialog2() {
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
							btnFromDate.setText(Util.getLongDate(mDay, mMonth,
									mYear));
							btnToDate.setText(Util.getLongDate(tDay, tMonth,
									tYear));
						} else {
							tYear = year;
							tMonth = month;
							tDay = day;
							btnToDate.setText(Util.getLongDate(tDay, tMonth,
									tYear));
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
									animateButtonOut();
								} else {
									animateButtonIn();
								}
							}
						}

					}
				}, xYear, xMth, xDay, true);
		datePickerDialog.setVibrate(true);
		datePickerDialog.setYearRange(2010, 2036);
		Bundle args = new Bundle();
		if (isFrom && mYear > 0) {
			args.putInt("year", mYear);
			args.putInt("month", mMonth);
			args.putInt("day", mDay);
		}
		if (!isFrom && tYear > 0) {
			args.putInt("year", tYear);
			args.putInt("month", tMonth);
			args.putInt("day", tDay);
		}

		datePickerDialog.setArguments(args);
		datePickerDialog.show(fragmentManager, DATEPICKER_TAG);

	}

	private void showTimeDialog2() {
		final Calendar calendar = Calendar.getInstance();
		timePickerDialog = TimePickerDialog.newInstance(
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(RadialPickerLayout view,
							int hourOfDay, int minute) {
						chkBoxAllDay.setChecked(false);
						if (isFrom) {
							mHour = hourOfDay;
							mMin = minute;
							tHour = hourOfDay;
							tMin = minute;

							btnFromTime.setText(getTime(mHour, mMin));
							tHour = mHour + 1;
							if (tHour > 23) {
								tHour = 1;
							}
							btnToTime.setText(getTime(tHour, tMin));
						} else {
							tHour = hourOfDay;
							tMin = minute;
							btnToTime.setText(getTime(tHour, tMin));

						}

					}
				}, calendar.get(Calendar.HOUR_OF_DAY), calendar
						.get(Calendar.MINUTE), false, false);
		timePickerDialog.setVibrate(true);
		timePickerDialog.show(fragmentManager, TIMEPICKER_TAG);
	}

	private void setFields() {

		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		spinInstructorClass = (Spinner) view.findViewById(R.id.ED_spinClass);
		spinClassCourse = (Spinner) view.findViewById(R.id.ED_spinClassCourse);
		spinRecur = (Spinner) view.findViewById(R.id.ED_spinRecur);
		spinLesson = (Spinner) view.findViewById(R.id.ED_spinLesson);

		eTitle = (EditText) view.findViewById(R.id.ED_editTitle);
		// eDesc = (EditText) view.findViewById(R.id.ED_editDescription);
		// eWhere = (EditText) view.findViewById(R.id.ED_editWhere);

		btnFromDate = (Button) view.findViewById(R.id.ED_btnFromDate);
		btnToDate = (Button) view.findViewById(R.id.ED_btnToDate);
		btnFromDate.setText(Util.getLongDate(new Date()));
		btnToDate.setText(Util.getLongDate(new Date()));

		btnFromTime = (Button) view.findViewById(R.id.ED_btnFromTime);
		btnToTime = (Button) view.findViewById(R.id.ED_btnToTime);
		//
		btnFromTime.setText(Util.getShortTime(new Date()));
		long now = new Date().getTime();
		int[] time = Util.getTime(new Date(now));
		mHour = time[0];
		mMin = time[1];
		//
		long hourLater = now + (60 * 60 * 1000);
		time = Util.getTime(new Date(hourLater));
		tHour = time[0];
		tMin = time[1];

		spinRecur = (Spinner) view.findViewById(R.id.ED_spinRecur);
		int[] dp = Util.getDateParts(new Date());
		mYear = dp[2];
		mMonth = dp[1];
		mDay = dp[0];
		tYear = dp[2];
		tMonth = dp[1];
		tDay = dp[0];
		setRecurStrings();
		chkBoxAllDay = (CheckBox) view.findViewById(R.id.ED_chlBoxAllDay);

		btnToTime.setText(Util.getShortTime(new Date(hourLater)));

		mLayout = view.findViewById(R.id.ED_editLayout2);
		btnMore = (Button) view.findViewById(R.id.ED_btnMore);
		btnMore.setVisibility(View.GONE);
		btnFromTime.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				vb.vibrate(30);
				isFrom = true;
				showTimeDialog2();
			}
		});
		btnToTime.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				vb.vibrate(30);
				isFrom = false;
				showTimeDialog2();
			}
		});
		btnFromDate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				vb.vibrate(30);
				isFrom = true;
				// if (mDay == 0) {
				// Calendar cal = GregorianCalendar.getInstance();
				// mDay = cal.get(Calendar.DAY_OF_MONTH);
				// mMonth = cal.get(Calendar.MONTH);
				// mYear = cal.get(Calendar.YEAR);
				//
				// }
				showDateDialog2();
			}
		});
		btnToDate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				vb.vibrate(30);
				isFrom = false;
				if (tDay == 0) {
					Calendar cal = GregorianCalendar.getInstance();
					tDay = cal.get(Calendar.DAY_OF_MONTH);
					tMonth = cal.get(Calendar.MONTH);
					tYear = cal.get(Calendar.YEAR);
				}
				showDateDialog2();
			}
		});
		btnMore.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				vb.vibrate(30);
				toggleEditFields();
			}
		});
		btnSubmit = (Button) view.findViewById(R.id.ED_btnSubmit);
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				vb.vibrate(30);
				submitEvent();
			}
		});

		chkBoxAllDay.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				doCheckBox(isChecked);
			}
		});
	}

	private void doCheckBox(boolean isChecked) {
		if (isChecked) {
			mHour = 8;
			mMin = 0;
			tHour = 17;
			tMin = 0;

			btnFromTime.setText(getTime(mHour, mMin));
			btnToTime.setText(getTime(tHour, tMin));
		} else {
			//
			btnFromTime.setText(Util.getShortTime(new Date()));
			long now = new Date().getTime();
			int[] time = Util.getTime(new Date(now));
			mHour = time[0];
			mMin = time[1];
			//
			long hourLater = now + (60 * 60 * 1000);
			time = Util.getTime(new Date(hourLater));
			tHour = time[0];
			tMin = time[1];
			btnToTime.setText(Util.getShortTime(new Date(hourLater)));
		}
		setRecurStrings();
	}

	private String getTime(int hour, int min) {
		StringBuilder sb = new StringBuilder();
		if (hour < 10) {
			sb.append("0").append(hour);
		} else {
			sb.append(hour);
		}
		sb.append(":");
		if (min < 10) {
			sb.append("0").append(min);
		} else {
			sb.append(min);
		}
		return sb.toString();
	}

	private void setRecurStrings() {
		final long dt = Util.getSimpleDate(mDay, mMonth, mYear);
		recurList = Util.getRecurStrings(new Date(dt));

		ArrayAdapter<String> adapterMax = new ArrayAdapter<String>(ctx,
				android.R.layout.simple_spinner_item, recurList);
		adapterMax
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinRecur.setAdapter(adapterMax);
		spinRecur
				.setPrompt(ctx.getResources().getString(R.string.select_recur));
		spinRecur.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// recurDTO = Util.getRecur(arg2, new Date(dt));
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	private List<TrainingClassDTO> trainingClassList;
	private TrainingClassDTO trainingClass;

	private void setClassSpinner() {
		//Log.d(LOG, "......setting class spinner, classes = "
		//		+ trainingClassList.size());

		final ArrayList<String> tarList = new ArrayList<String>();
		if (trainingClassList != null) {
			for (TrainingClassDTO p : trainingClassList) {
				tarList.add(p.getTrainingClassName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					android.R.layout.simple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinInstructorClass.setAdapter(dataAdapter);
			spinInstructorClass.setPrompt(ctx.getResources().getString(
					R.string.select_class));

			spinInstructorClass
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

							trainingClass = trainingClassList.get(arg2);
							classCourseList = trainingClass
									.getTrainingClassCourseList();
							setCourseSpinner();
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
			if (tc.getTrainingClassID() == instructorClass
					.getTrainingClassID()) {
				spinInstructorClass.setSelection(index);
				trainingClass = tc;
				return;
			}
			index++;
		}
	}

	List<String> stringList;

	private void setCourseSpinner() {
		Log.d(LOG, "setting course spinner ...");
		final ArrayList<String> tarList = new ArrayList<String>();
		if (classCourseList != null) {
			if (classCourseList.size() > 0) {
				tarList.add(ctx.getResources()
						.getString(R.string.select_course));
			}
			for (TrainingClassCourseDTO p : classCourseList) {
				tarList.add(p.getCourse().getCourseName());
			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					android.R.layout.simple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinClassCourse.setAdapter(dataAdapter);
			spinClassCourse.setPrompt("Please select Course");
			spinClassCourse
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == 0) {
								classCourse = null;
								eTitle.setText("");
								//lessonList = new ArrayList<LessonDTO>();
								setLessonSpinner();
								myTitle.courseName = "";
								myTitle.lessonName = "";
								eTitle.setText(myTitle.courseName + "\n" + myTitle.lessonName);
								return;
							}
							classCourse = classCourseList.get(arg2 - 1);
							myTitle.courseName = classCourse.getCourse().getCourseName();
							myTitle.lessonName = "";
							
							eTitle.setText(myTitle.courseName + "\n" + myTitle.lessonName);
							//lessonList = classCourse.getLessonList();
							setLessonSpinner();

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
	}
	MyTitle myTitle = new MyTitle();
	class MyTitle {
		String courseName = "", lessonName = "";
	}
	private void setLessonSpinner() {

		final ArrayList<String> tarList = new ArrayList<String>();
//		if (lessonList != null) {
//			if (lessonList.size() > 0) {
//				tarList.add(ctx.getResources()
//						.getString(R.string.select_lesson));
//			}
//			for (LessonDTO p : lessonList) {
//				tarList.add(p.getLessonName());
//			}
//
//			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
//					android.R.layout.simple_spinner_item, tarList);
//			dataAdapter
//					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			spinLesson.setAdapter(dataAdapter);
//			spinLesson.setPrompt(ctx.getResources().getString(
//					R.string.select_class));
//
//			spinLesson.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//				@Override
//				public void onItemSelected(AdapterView<?> arg0, View arg1,
//						int arg2, long arg3) {
//
//					if (arg2 == 0) {
//						lesson = null;
//						myTitle.lessonName = "";
//						eTitle.setText(myTitle.courseName + "\n" + myTitle.lessonName);
//						return;
//					}
//					lesson = lessonList.get(arg2 - 1);
//					myTitle.lessonName = lesson.getLessonName();
//					eTitle.setText(myTitle.courseName + "\n" + myTitle.lessonName);
//
//				}
//
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//
//				}
//			});
//		}

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
		super.onSaveInstanceState(state);
	}

	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(eTitle.getWindowToken(), 0);
	}

	private View view;

	private int mYear, mMonth, mDay, tYear, tMonth, tDay, mHour, mMin, tHour,
			tMin;
	private TrainingClassCourseDTO classCourse;
	private boolean isFrom;
	private Context ctx;
	private InstructorDTO instructor;
	private List<TrainingClassCourseDTO> classCourseList;

	private Vibrator vb;
	private Button btnMore, btnFromDate, btnToDate, btnFromTime, btnToTime,
			btnSubmit;
	private CheckBox chkBoxAllDay;
	private Spinner spinRecur, spinClassCourse, spinInstructorClass,
			spinLesson;
	private EditText eTitle; // eDesc, eWhere;
	private View mLayout;
	private List<String> recurList;
	private BusyListener busyListener;

}
