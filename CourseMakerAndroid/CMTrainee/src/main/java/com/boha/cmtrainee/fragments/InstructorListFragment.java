package com.boha.cmtrainee.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmtrainee.R;
import com.boha.cmtrainee.adapters.InstructorAdapter;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.ErrorUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class InstructorListFragment extends Fragment {

	public InstructorListFragment() {
	}

	BusyListener busyListener;
	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host must implement BusyListener");
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
		Log.i(LOG, "##### onCreateView");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater
				.inflate(R.layout.fragment_instructor_list, container, false);
		if (saved != null) {
			response = (ResponseDTO) saved.getSerializable("data");
		}
		
		setFields();
		new LocalDataTask().execute();
		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "## onResume");
		if (response != null) {
			Log.d(LOG, "refreshing list from saved instance");
			setList();
		} else {
			getInstructors();
		}
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ....");
		if (response != null) {
			state.putSerializable("data", response);
		}
		super.onSaveInstanceState(state);
	}

	private void setFields() {
		
		listView = (ListView) view.findViewById(R.id.CLS_list);
		txtCount = (TextView) view.findViewById(R.id.CLS_count);
		spinner = (Spinner) view.findViewById(R.id.CLS_helpTypeSpinner);
		editComment = (EditText)view.findViewById(R.id.CLS_editComment);
		btnSend = (Button)view.findViewById(R.id.CLS_btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
	}

	private void setList() {	
		adapter = new InstructorAdapter(ctx, R.layout.instructor_item,
				response.getInstructorList(), imageLoader);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText("" + response.getInstructorList().size());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				instructor = response.getInstructorList().get(
						arg2);
				if (response.getInstructorList() == null
						|| response.getInstructorList().isEmpty()) {
					ToastUtil.toast(
							ctx,
							ctx.getResources().getString(
									R.string.no_instructors));
				} 

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				instructor = response.getInstructorList().get(
						arg2);
				return false;
			}
		});
	}

	public void getInstructors() {
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_INSTRUCTOR_LIST_BY_CLASS);
		req.setTrainingClassID(trainingClass.getTrainingClassID());
		req.setZippedResponse(true);
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.TRAINEE_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        response = r;
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
//		BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, req, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						busyListener.setNotBusy();
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						busyListener.setNotBusy();
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						response = r;
//						setList();
//
//					}
//				});

	}
	public void sendMessage() {
		traineeShout = new TraineeShoutDTO();
		if (!editComment.getText().toString().isEmpty()) {
			traineeShout.setRemarks(editComment.getText().toString());
		}
		if (helpType == null) {
			ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_helptype));
			return;
		}
		traineeShout.setHelpTypeID(helpType.getHelpTypeID());
		animateButtonOut();
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GCM_SEND_TRAINEE_TO_INSTRUCTOR_MSG);
		req.setTraineeShout(traineeShout);
		req.setTrainingClassID(trainingClass.getTrainingClassID());
		req.setZippedResponse(false);
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

		busyListener.setBusy();
		BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, req, ctx,
				new BaseVolley.BohaVolleyListener() {

					@Override
					public void onVolleyError(VolleyError error) {
						busyListener.setNotBusy();
						animateButtonIn();
						ToastUtil.errorToast(ctx,
								ctx.getResources().getString(
										R.string.error_server_comms));

					}

					@Override
					public void onResponseReceived(ResponseDTO r) {
						busyListener.setNotBusy();
						animateButtonIn();
						if (r.getStatusCode() > 0) {
							ToastUtil.errorToast(ctx, r.getMessage());
							return;
						}
						Log.i(LOG, "Cloud message sent OK ...");
						ToastUtil.toast(ctx, ctx.getResources().getString(R.string.message_sent));

					}
				});

	}

	private void animateButtonOut() {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.fade_out);
		a.setDuration(500);
		a.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			};
			
			@Override
			public void onAnimationEnd(Animation animation) {
				btnSend.setVisibility(View.GONE);	
			}
		});
		btnSend.startAnimation(a);
	}
	private void animateButtonIn() {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);
		btnSend.setVisibility(View.VISIBLE);
		btnSend.startAnimation(a);
	}
	public void setTrainingClass(TrainingClassDTO trainingClass) {
		this.trainingClass = trainingClass;
		getInstructors();
	}
	private void setHelpSpinner() {
		final ArrayList<String> tarList = new ArrayList<String>();
		if (helpTypeList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_helptype));
			for (HelpTypeDTO p : helpTypeList) {
				tarList.add(p.getHelpTypeName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 0) {
						helpType = null;
						traineeShout = null;
						return;
					}
					helpType = helpTypeList.get(arg2 - 1);
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
	}
	class LocalDataTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {


            CacheUtil.getCachedData(ctx,CacheUtil.CACHE_HELPTYPES, new CacheUtil.CacheUtilListener() {
                @Override
                public void onFileDataDeserialized(ResponseDTO response) {
                    if (response != null) {
                        helpTypeList = response.getHelpTypeList();
                    }
                }

                @Override
                public void onDataCached() {

                }
            });
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				ErrorUtil.handleErrors(ctx, result);
			}
			setHelpSpinner();
		}

	}
	List<HelpTypeDTO> helpTypeList;
	TraineeDTO trainee;
	HelpTypeDTO helpType;
	TraineeShoutDTO traineeShout;
	TrainingClassDTO trainingClass;
	InstructorDTO instructor;
	Context ctx;
	View view;
	ResponseDTO response;
	Spinner spinner;
	InstructorAdapter adapter;
	ListView listView;
	TextView txtCount;
	ImageLoader imageLoader;
	Button btnSend;
	EditText editComment;
	
	
	
	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	static final String LOG = "InstructorListFragment";
	
	public void setBusyListener(BusyListener busyListener) {
		this.busyListener = busyListener;
	}

}
