package com.boha.cmtrainee.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.VolleyError;
import com.boha.cmtrainee.R;
import com.boha.cmtrainee.adapters.TeamAdapter;
import com.boha.cmtrainee.interfaces.TeamListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class TeamListFragment extends Fragment implements PageInterface {
	View view;
	Context ctx;
	ResponseDTO response;
	BusyListener busyListener;
	TeamListener teamListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host must implement BusyListener");
		}
		if (a instanceof TeamListener) {
			teamListener = (TeamListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host must implement TeamListener");
		}
		super.onAttach(a);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		Log.i(LOG, "##### onCreateView");
		Log.e(LOG, "TrainingClassID = "
				+ SharedUtil.getTrainingClass(ctx).getTrainingClassID());

		inflater = getActivity().getLayoutInflater();

		view = inflater.inflate(R.layout.fragment_team_list, container, false);
		setFields();
		Bundle b = getArguments();
		if (b != null) {
			ResponseDTO r = (ResponseDTO) b.getSerializable("response");
			traineeList = r.getTraineeList();
			teamList = r.getTeamList();
			setList();
		}
		
		return view;
	}

	List<TraineeDTO> traineeList;

	@Override
	public void onSaveInstanceState(Bundle b) {
		Log.w(LOG, "## onSaveInstanceState");
		if (response != null) {
			b.putSerializable("response", response);
		}
		super.onSaveInstanceState(b);
	}

	@Override
	public void onResume() {
		Log.w(LOG, "## onResume");

		super.onResume();

	}

	public void getTeams() {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TEAMS_BY_CLASS);
		r.setTrainingClassID(SharedUtil.getTrainingClass(ctx)
				.getTrainingClassID());
		//
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.TEAM_ENDPOINT, r, new WebSocketUtil.WebSocketListener() {
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
                        teamList = response.getTeamList();
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
//		BaseVolley.getRemoteData(Statics.SERVLET_TEAM, r, ctx,
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
//
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						response = r;
//						teamList = response.getTeamList();
//						setList();
//					}
//				});

	}

	private void setList() {
		adapter = new TeamAdapter(ctx, R.layout.team_item, teamList);
		if (teamList == null || teamList.isEmpty()) {
			Log.e(LOG, "TeamList is empty or null!");
			return;
		}

		txtCount.setText("" + teamList.size());
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				team = teamList.get(arg2);
				teamListener.onTeamPicked(team);
			}
		});
	}

	public void addTeamMembers(List<TeamMemberDTO> list) {
		Log.w(LOG, "addTeamMembers, list : " + list.size());
		if (!list.isEmpty()) {
			for (TeamDTO t : teamList) {
				if (list.get(0).getTeamID() == t.getTeamID()
						) {
					t.getTeamMemberList().addAll(0, list);
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	private void setFields() {
		editTeamName = (EditText) view.findViewById(R.id.TEAM_editTeamName);
		btnSave = (Button) view.findViewById(R.id.TEAM_btnSave);
		txtCount = (TextView) view.findViewById(R.id.TEAM_count);
		listView = (ListView) view.findViewById(R.id.TEAM_list);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}

	private void submit() {
		if (editTeamName.getText().toString().isEmpty()) {
			ToastUtil.toast(ctx,
					ctx.getResources().getString(R.string.enter_team_name));
			return;
		}
		TeamDTO t = new TeamDTO();
		t.setTeamName(editTeamName.getText().toString());
		t.setTrainingClassID(SharedUtil.getTrainingClass(ctx)
				.getTrainingClassID());
		t.setTeamMemberList(new ArrayList<TeamMemberDTO>());

		TeamMemberDTO tm = new TeamMemberDTO();
		tm.setTraineeID(SharedUtil.getTrainee(ctx).getTraineeID());
		t.getTeamMemberList().add(tm);

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_TEAM);
		r.setTeam(t);
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
		BaseVolley.getRemoteData(Statics.SERVLET_TEAM, r, ctx,
				new BaseVolley.BohaVolleyListener() {

					@Override
					public void onVolleyError(VolleyError error) {
						busyListener.setNotBusy();
						ToastUtil.errorToast(
								ctx,
								ctx.getResources().getString(
										R.string.error_server_comms));

					}

					@Override
					public void onResponseReceived(ResponseDTO r) {
						busyListener.setNotBusy();
						if (r.getStatusCode() > 0) {
							ToastUtil.errorToast(ctx, r.getMessage());
							return;
						}
						TeamDTO t = r.getTeam();

						if (teamList == null) {
							teamList = new ArrayList<TeamDTO>();
						}
						teamList.add(0, t);
						if (adapter != null) {
							adapter.notifyDataSetChanged();
						} else {
							setList();
						}

					}
				});
	}

	List<TeamDTO> teamList;
	TeamDTO team;
	TeamAdapter adapter;
	EditText editTeamName;
	Button btnSave;
	TextView txtCount;
	ListView listView;

	static final String LOG = "TeamListFragment";
}
