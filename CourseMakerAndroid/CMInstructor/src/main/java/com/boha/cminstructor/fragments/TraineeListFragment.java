package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.boha.cminstructor.R;
import com.boha.cminstructor.adapters.TraineeAdapter;
import com.boha.cminstructor.listeners.NoTraineesListener;
import com.boha.cminstructor.listeners.TraineeListener;
import com.boha.cmlibrary.CMApp;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.text.DecimalFormat;
import java.util.List;

public class TraineeListFragment extends Fragment {

	public TraineeListFragment() {
	}

	static final String LOG = "TraineeListFragment";
	ImageLoader imageLoader;
	NoTraineesListener noTraineesListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof TraineeListener) {
			listener = (TraineeListener) a;
		} else {
			throw new UnsupportedOperationException("Host activity "+a.getLocalClassName() +
					" must implement TraineeListener" );
		}

		if (a instanceof NoTraineesListener) {
			noTraineesListener = (NoTraineesListener) a;
		} else {
			throw new UnsupportedOperationException("Host activity "+a.getLocalClassName() +
					" must implement NoTraineesListener" );
		}

		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		Log.e(LOG, "........onActivityCreated");
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		Log.w(LOG, "........onCreateView");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_trainee_list, container,
                false);
		setFields();
		if (saved != null) {
			ResponseDTO r = (ResponseDTO) saved.getSerializable("response");
			traineeList = r.getTraineeList();
		}
        CMApp app = (CMApp)getActivity().getApplication();
        imageLoader = app.getImageLoader();
		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "..... resuming in " + LOG);
		if (traineeList != null) {
			setList();
		} else {
			getTraineeData();
		}
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  saving list: ");
		if (traineeList != null) {
			ResponseDTO r = new ResponseDTO();
			r.setTraineeList(traineeList);
			state.putSerializable("response", r);
		}
		super.onSaveInstanceState(state);
	}

	private void setFields() {
		txtClassName = (TextView) view.findViewById(R.id.CRS_class);
		listView = (ListView) view.findViewById(R.id.CRS_listView);
		txtCount = (TextView) view.findViewById(R.id.LH_count);
		txtAggr = (TextView) view.findViewById(R.id.LH_aggregate);
	}

	private void setList() {

		adapter = new TraineeAdapter(getActivity(), R.layout.trainee_item,
				traineeList, imageLoader);

		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText("" + traineeList.size());
		txtAggr.setText(df.format(getAggregateComplete()) + "%");
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				trainee = traineeList.get(arg2);
				listener.onTraineePicked(trainee);

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

	public void refreshTraineeList() {
		getTraineeData();
	}

	private double getAggregateComplete() {
		int totalComplete = 0, totalTasks = 0;
		for (TraineeDTO t : traineeList) {
			totalTasks += t.getTotalTasks();
			totalComplete += t.getTotalCompleted();
		}
		return getPercentage(totalTasks, totalComplete);
	}

	public static double getPercentage(int total, int complete) {
		if (total == 0)
			return 0;
		Double t = Double.valueOf(total);
		Double c = Double.valueOf(complete);
		Double p = (c / t) * 100;
		return p.doubleValue();
	}

	static final DecimalFormat df = new DecimalFormat("###.00");
	List<TraineeDTO> traineeList;
	TraineeDTO trainee;
	InstructorClassDTO instructorClass;
	private TraineeListener listener;
	Context ctx;
	View view;
	TextView txtAggr;

	TraineeAdapter adapter;
	ListView listView;
	TextView txtClassName, txtCount;

	public void setInstructorClass(InstructorClassDTO instructorClass) {
		Log.d(LOG,
				"setInstructorClass - "
						+ instructorClass.getTrainingClassName());
		this.instructorClass = instructorClass;
	}

	public void getTraineeData() {
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_TRAINEE_CLASS_LIST, instructorClass.getTrainingClassID(),new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    traineeList = response.getTraineeList();
                    setList();
                }
                RequestDTO r = new RequestDTO();
                r.setRequestType(RequestDTO.GET_CLASS_TRAINEE_LIST);
                r.setTrainingClassID(instructorClass.getTrainingClassID());
                //
                if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
                listener.setBusy();
                WebSocketUtil.sendRequest(ctx,Statics.INSTRUCTOR_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
                    @Override
                    public void onMessage(final ResponseDTO response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.setNotBusy();
                                if (response.getStatusCode() > 0) {
                                    ToastUtil.errorToast(ctx, response.getMessage());
                                    return;
                                }
                                if (response.getTraineeList().size() == 0) {
                                    ToastUtil
                                            .toast(ctx,
                                                    ctx.getResources()
                                                            .getString(
                                                                    R.string.class_trainee_list_not_found));
                                    noTraineesListener.onTraineesNotFound();
                                }
                                traineeList = response.getTraineeList();
                                if (adapter == null) {
                                    setList();
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                                CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TRAINEE_CLASS_LIST, instructorClass.getTrainingClassID(), new CacheUtil.CacheUtilListener() {
                                    @Override
                                    public void onFileDataDeserialized(ResponseDTO response) {

                                    }

                                    @Override
                                    public void onDataCached() {

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
//                BaseVolley.getRemoteData(Statics.SERVLET_INSTRUCTOR, r, ctx,
//                        new BaseVolley.BohaVolleyListener() {
//
//                            @Override
//                            public void onVolleyError(VolleyError error) {
//                                ToastUtil.errorToast(
//                                        ctx,
//                                        ctx.getResources().getString(
//                                                R.string.error_server_comms));
//                                listener.setNotBusy();
//                            }
//
//                            @Override
//                            public void onResponseReceived(ResponseDTO response) {
//                                listener.setNotBusy();
//                                if (response.getStatusCode() > 0) {
//                                    ToastUtil.errorToast(ctx, response.getMessage());
//                                    return;
//                                }
//                                if (response.getTraineeList().size() == 0) {
//                                    ToastUtil
//                                            .toast(ctx,
//                                                    ctx.getResources()
//                                                            .getString(
//                                                                    R.string.class_trainee_list_not_found));
//                                    noTraineesListener.onTraineesNotFound();
//                                }
//                                traineeList = response.getTraineeList();
//                                if (adapter == null) {
//                                    setList();
//                                } else {
//                                    adapter.notifyDataSetChanged();
//                                }
//                                CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_TRAINEE_CLASS_LIST, instructorClass.getTrainingClassID(), new CacheUtil.CacheUtilListener() {
//                                    @Override
//                                    public void onFileDataDeserialized(ResponseDTO response) {
//
//                                    }
//
//                                    @Override
//                                    public void onDataCached() {
//
//                                    }
//                                });
//
//                            }
//                        });

            }

            @Override
            public void onDataCached() {

            }
        });

    }
}
