package com.boha.cmtrainee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.HelpRequestDTO;
import com.boha.coursemaker.dto.HelpTypeDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeShoutDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rating);
		ctx = getApplicationContext();
		courseTraineeActivity = (CourseTraineeActivityDTO) getIntent()
				.getExtras().getSerializable("activity");
		trainee = SharedUtil.getTrainee(ctx);
		setFields();
        CacheUtil.getCachedData(ctx,CacheUtil.CACHE_HELPTYPES, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    helpTypeList = response.getHelpTypeList();
                    setHelpSpinner();
                }
            }

            @Override
            public void onDataCached() {

            }
        });
	}

	private void setFields() {
		spinnerHelp = (Spinner) findViewById(R.id.RAT_spinnerHelp);
		spinnerRating = (Spinner) findViewById(R.id.RAT_ratingSpinner);
        spinnerRating.setVisibility(View.GONE);
		txtActivityName = (TextView) findViewById(R.id.RAT_activityName);
		//txtDesc = (TextView) findViewById(R.id.RAT_desc);
		radioComplete = (RadioButton) findViewById(R.id.RAT_radioComplete);
		radioIncomplete = (RadioButton) findViewById(R.id.RAT_radioIncomplete);
		btnSubmit = (Button) findViewById(R.id.RAT_btnSubmit);
		comment = (EditText) findViewById(R.id.RAT_comment);
		txtActivityName.setText(courseTraineeActivity.getActivity()
				.getActivityName());
		//txtDesc.setText(courseTraineeActivity.getActivity().getDescription());
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});

	}

	int ratingNumber;

	private void submit() {
		if (helpType != null) {
			helpRequest = new HelpRequestDTO();
			helpRequest.setHelpType(helpType);
			helpRequest.setComment(comment.getText().toString());
			helpRequest.setCourseTraineeActivity(courseTraineeActivity);

		}
		if (!comment.getText().toString().isEmpty()) {
			courseTraineeActivity.setComment(comment.getText().toString());
		}

		sendHelpRequest();

	}


	private void setHelpSpinner() {
		final ArrayList<String> tarList = new ArrayList<String>();
		if (helpTypeList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_helptype));
			for (HelpTypeDTO p : helpTypeList) {
				tarList.add(p.getHelpTypeName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			spinnerHelp.setAdapter(dataAdapter);
			spinnerHelp.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 0) {
						helpType = null;
						helpRequest = null;
						return;
					}
					helpType = helpTypeList.get(arg2 - 1);
					helpRequest = new HelpRequestDTO();
					helpRequest.setHelpType(helpType);
					helpRequest.setCourseTraineeActivity(courseTraineeActivity);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		Log.i("RA", "*** onPause() - ...");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	Menu mMenu;
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rating, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:
			onBackPressed();
			return true;
		case R.id.menu_info:
			return true;

		case android.R.id.home:
			Intent intent = new Intent(this, MainPagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {

		if (hasRated) {
			Intent i = new Intent();
			i.putExtra("cta", courseTraineeActivity);
			setResult(Activity.RESULT_OK, i);
		} else {
			setResult(Activity.RESULT_CANCELED);
		}
		
		finish();
		super.onBackPressed();
	}


	static final String LOG = "HelpActivity";


	private void sendHelpRequest() {
		Log.w(LOG, "###############...sending help request .....");
        // public static ResponseDTO sendTraineeToInstructorMessage(
       // HelpRequestDTO req, TraineeShoutDTO traineeShout,
       //         Integer trainingClassID
        TraineeShoutDTO ts = new TraineeShoutDTO();
        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.GCM_SEND_TRAINEE_TO_INSTRUCTOR_MSG);
        CourseTraineeActivityDTO cta = new CourseTraineeActivityDTO();
        cta.setCourseTraineeActivityID(courseTraineeActivity.getCourseTraineeActivityID());
        helpRequest.setCourseTraineeActivity(cta);
        r.setHelpRequest(helpRequest);
        r.setTrainingClassID(courseTraineeActivity.getTrainingClassID());
		
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.TRAINEE_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        Log.i(LOG, "###### help request sent OK");
                        ToastUtil.toast(
                                ctx,
                                getResources().getString(
                                        R.string.help_request_sent));
                        onBackPressed();
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
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, r, ctx,
//				new BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setRefreshActionButtonState(false);
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO response) {
//						setRefreshActionButtonState(false);
//						Log.i(LOG, "###### help request sent OK");
//						ToastUtil.toast(
//								ctx,
//								getResources().getString(
//										R.string.help_request_sent));
//						onBackPressed();
//					}
//				});
	}

	boolean hasRated;
	Context ctx;
	ResponseDTO response;
	HelpRequestDTO helpRequest;
	List<HelpTypeDTO> helpTypeList;
	HelpTypeDTO helpType;
	Spinner spinnerHelp, spinnerRating;
	TextView txtActivityName, txtDesc;
	RadioButton radioComplete, radioIncomplete;
	EditText comment;
	Button btnSubmit;
	CourseTraineeActivityDTO courseTraineeActivity;
	TraineeDTO trainee;

}
