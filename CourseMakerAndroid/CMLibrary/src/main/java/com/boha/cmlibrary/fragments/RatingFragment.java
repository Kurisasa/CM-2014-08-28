package com.boha.cmlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmlibrary.ActivityListActivity;
import com.boha.cmlibrary.CMApp;
import com.boha.cmlibrary.R;
import com.boha.cmlibrary.listeners.RatingListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.base.PaddedRadioButton;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.RatingDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.coursemaker.util.actor.TraineeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RatingFragment extends Fragment implements PageInterface {

	public RatingFragment() {
	}

	RatingListener listener;
	BusyListener busyListener;
	View view;
	static final String LOG = "RatingFragment";

	@Override
	public void onAttach(Activity a) {
		if (a instanceof RatingListener) {
			listener = (RatingListener) a;
		} else {
			throw new UnsupportedOperationException();
		}
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException();
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
		Log.e(LOG, "############### onCreateView in " + LOG);
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_rating, container, false);
		setFields();
        CMApp app = (CMApp)getActivity().getApplication();
        imageLoader = app.getImageLoader();
                CacheUtil.getCachedData(ctx, CacheUtil.CACHE_TRAINEE_ACTIVITY, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    ratingList = response.getRatingList();
                    setRatingSpinner();
                }
            }

            @Override
            public void onDataCached() {

            }
        });
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

	private void setFields() {

		completionLayout = view.findViewById(R.id.RAT_layoutC);
		completionLayout.setVisibility(View.GONE);
		txtActivityName = (TextView) view
				.findViewById(R.id.RAT_txtActivityName);
		txtCourse = (TextView) view.findViewById(R.id.RAT_txtCourseName);
		txtTrainee = (TextView) view.findViewById(R.id.RAT_txtTrainee);
		txtDate = (TextView) view.findViewById(R.id.RAT_txtDate);
		radioComplete = (PaddedRadioButton) view
				.findViewById(R.id.RAT_radioComplete);
		radioIncomplete = (PaddedRadioButton) view
				.findViewById(R.id.RAT_radioIncomplete);
		ratingSpinner = (Spinner) view.findViewById(R.id.RAT_ratingSpinner);

		btnSubmit = (Button) view.findViewById(R.id.RAT_btnSubmit);
		
		comment = (EditText) view.findViewById(R.id.RAT_comment);

		image = (NetworkImageView) view.findViewById(R.id.RAT_image);
		image.setDefaultImageResId(R.drawable.boy);

		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (type) {
                    case ActivityListActivity.INSTRUCTOR:
                        sendInstructorRating();
                        break;
                    case ActivityListActivity.TRAINEE:
                        sendTraineeRating();
                        break;
                }
			}
		});
        Statics.setRobotoFontRegular(ctx, txtTrainee);
        Statics.setRobotoFontRegular(ctx, txtCourse);
        Statics.setRobotoFontRegular(ctx, txtActivityName);

	}


	private void sendInstructorRating() {
        Log.w(LOG, "###################...sending instructor rating .... ");
		hideKeyboard();
		int count = 0;

		if (!comment.getText().toString().isEmpty()) {
			courseTraineeActivity.setComment(comment.getText().toString());
		}
		if (radioComplete.isChecked()) {
			courseTraineeActivity.setCompletedFlag(1);
			count++;
		}
		if (radioIncomplete.isChecked()) {
			courseTraineeActivity.setCompletedFlag(0);
			count++;
		}
		if (count == 0) {
			ToastUtil.toast(ctx,
					ctx.getResources().getString(R.string.select_completion_status));
			return;
		}
		if (rating == null) {
			ToastUtil.toast(ctx,
					ctx.getResources().getString(R.string.select_rating));
			return;
		} else {
			courseTraineeActivity.setRating(rating);
		}
		
		CourseTraineeActivityDTO cta = new CourseTraineeActivityDTO();
		cta.setCourseTraineeActivityID(courseTraineeActivity.getCourseTraineeActivityID());
		cta.setComment(courseTraineeActivity.getComment());
		cta.setCompletedFlag(courseTraineeActivity.getCompletedFlag());
		cta.setRating(rating);
		
		RequestDTO request = new RequestDTO();
		request.setRequestType(RequestDTO.ADD_INSTRUCTOR_RATINGS);
		request.setInstructorID(SharedUtil.getInstructor(ctx).getInstructorID());
		request.setCourseTraineeActivity(cta);

		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
		btnSubmit.setEnabled(false);
        WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,request,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        btnSubmit.setEnabled(true);
                        response = r;
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        ToastUtil.toast(
                                ctx,
                                ctx.getResources().getString(
                                        R.string.rating_sent));
                        Log.e(LOG,
                                "about to tell listener that rating completed ...");
                        listener.onRatingCompleted(r.getCourseTraineeActivity());
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
//		BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, request, getActivity(),
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						Log.e(LOG, "Problem: " + error.getMessage());
//						busyListener.setNotBusy();
//						btnSubmit.setEnabled(true);
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
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						busyListener.setNotBusy();
//						btnSubmit.setEnabled(true);
//						response = r;
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						ToastUtil.toast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.rating_sent));
//						Log.e(LOG,
//								"about to tell listener that rating completed ...");
//						listener.onRatingCompleted(r.getCourseTraineeActivity());
//
//					}
//				});

	}

    private void sendTraineeRating() {
        Log.w(LOG, "###################...sending trainee rating .... ");
        if (!comment.getText().toString().isEmpty()) {
            courseTraineeActivity.setComment(comment.getText().toString());
        }
        if (rating == null) {
            ToastUtil.toast(ctx,
                    ctx.getResources().getString(R.string.select_rating));
            return;
        } else {
            courseTraineeActivity.setRating(rating);
        }
        CourseTraineeActivityDTO cta = new CourseTraineeActivityDTO();
        cta.setCourseTraineeActivityID(courseTraineeActivity.getCourseTraineeActivityID());
        cta.setComment(courseTraineeActivity.getComment());
        cta.setRating(courseTraineeActivity.getRating());
        cta.setCompletedFlag(courseTraineeActivity.getCompletedFlag());


        RequestDTO r = TraineeUtil.evaluateTraineeActivity(ctx,
                cta, courseTraineeActivity.getTraineeID());
        r.setRequestType(RequestDTO.EVALUATE_TRAINEE_ACTIVITY);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

        busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.TRAINEE_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        response = r;
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        SharedUtil.setLastCompletionDate(ctx);
                        courseTraineeActivity = response.getCourseTraineeActivity();
                        listener.onRatingCompleted(courseTraineeActivity);
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
//        BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, r, ctx,
//                new BaseVolley.BohaVolleyListener() {
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        busyListener.setNotBusy();
//                        ToastUtil.errorToast(
//                                ctx,
//                                ctx.getResources().getString(
//                                        R.string.error_server_comms));
//                    }
//
//                    @Override
//                    public void onResponseReceived(ResponseDTO r) {
//                        busyListener.setNotBusy();
//                        response = r;
//                        if (r.getStatusCode() > 0) {
//                            ToastUtil.errorToast(ctx, r.getMessage());
//                            return;
//                        }
//                        SharedUtil.setLastCompletionDate(ctx);
//                        courseTraineeActivity = response.getCourseTraineeActivity();
//                        listener.onRatingCompleted(courseTraineeActivity);
//
//                    }
//                });
    }
	public void setCourseTraineeActivity(CourseTraineeActivityDTO cta, int type) {
		Log.d(LOG, "setting courseTraineeActivity. ...");
		this.courseTraineeActivity = cta;
        this.type = type;
		if (txtActivityName != null) {
			txtActivityName.setText(courseTraineeActivity.getActivity()
					.getActivityName());
			txtCourse.setText(courseTraineeActivity.getCourseName());
			txtTrainee.setText(courseTraineeActivity.getTraineeName());
			txtCourse.setText(courseTraineeActivity.getCourseName());
			txtTrainee.setText(courseTraineeActivity.getTraineeName());
			StringBuilder sb = new StringBuilder();
			sb.append(Statics.IMAGE_URL).append("company")
					.append(courseTraineeActivity.getCompanyID())
					.append("/trainee/");
			sb.append(courseTraineeActivity.getTraineeID()).append(".jpg");
			image.setImageUrl(sb.toString(), imageLoader);
			//
			if (courseTraineeActivity.getCompletedFlag() > 0) {
				completionLayout.setVisibility(View.VISIBLE);
				txtDate.setText(sdf.format(new Date(courseTraineeActivity
						.getCompletionDate())));
				radioComplete.setChecked(true);
			} else {
				radioIncomplete.setChecked(true);
			}
            if (type == ActivityListActivity.TRAINEE) {
                radioComplete.setVisibility(View.GONE);
                radioIncomplete.setVisibility(View.GONE);
            }
		}
	}
	Context ctx;
	int type;
	ResponseDTO response;
	View completionLayout;
	List<RatingDTO> ratingList;
	RatingDTO rating;
	TextView txtActivityName, txtTrainee, txtCourse, txtDate;
	PaddedRadioButton radioComplete, radioIncomplete;
	NetworkImageView image;
	EditText comment;
	Button btnSubmit;
	CourseTraineeActivityDTO courseTraineeActivity;
	TraineeDTO trainee;
	ImageLoader imageLoader;
	Spinner ratingSpinner;
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE, dd MMMM yyyy HH:mm", loc);

	
	private void setRatingSpinner() {
		final ArrayList<String> tarList = new ArrayList<String>();
		if (ratingList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_rating));
			for (RatingDTO p : ratingList) {
				tarList.add(p.getRatingName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			ratingSpinner.setAdapter(dataAdapter);
			ratingSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == 0) {
								rating = null;
								return;
							}
							rating = ratingList.get(arg2 - 1);

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
	}
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
	}
}
