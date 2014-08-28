package com.boha.cmadmin.fragments;

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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmadmin.R;
import com.boha.cmadmin.adapter.TraineeEquipmentAdapter;
import com.boha.cmadmin.listeners.ContextMenuInterface;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.cmadmin.listeners.TraineeEquipmentUpdateListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.base.BaseVolley.BohaVolleyListener;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.InventoryDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeEquipmentDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class TraineeEquipmentProvisioningFragment extends Fragment implements
		PageInterface {

	Context ctx;
	View view;
	TraineeEquipmentUpdateListener equipmentUpdateListener;

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity should implement BusyListener");
		}
		if (a instanceof TraineeEquipmentUpdateListener) {
			equipmentUpdateListener = (TraineeEquipmentUpdateListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity should implement TraineeEquipmentUpdateListener");
		}
		
		super.onAttach(a);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_trainee_equipment_prov,
				container, false);
		setFields();
		company = SharedUtil.getCompany(ctx);
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO)b.getSerializable("response");
		}
		return view;
	}

	private boolean isDeviceAssigned() {
		for (TraineeEquipmentDTO te : traineeEquipmentList) {
			if (te.getTrainee().getTraineeID() == trainee
					.getTraineeID()) {
				if (te.getInventory().getInventoryID() == inventory
						.getInventoryID()) {
					if (te.getDateReturned() == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isDeviceAssignedToAnyOne() {
		for (TraineeEquipmentDTO te : traineeEquipmentList) {

			if (te.getInventory().getInventoryID() == inventory
					.getInventoryID()) {
				if (te.getDateReturned() == 0) {
					return true;
				}
			}

		}
		return false;
	}

	private void send() {
		if (trainingClass == null) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.select_class));
			return;
		}
		if (trainee == null) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.select_trainee));
			return;
		}

		// check if already assigned
		if (isDeviceAssigned()) {
			ToastUtil.errorToast(
					ctx,
					ctx.getResources().getString(
							R.string.trainee_equipment_already_assigned));
			return;
		}
		if (isDeviceAssignedToAnyOne()) {
			ToastUtil.errorToast(
					ctx,
					ctx.getResources().getString(
							R.string.trainee_equipment_already_assigned));
			return;
		}

		RequestDTO req = new RequestDTO();
		req.setAdministratorID(SharedUtil.getAdministrator(ctx)
				.getAdministratorID());
		req.setTraineeID(trainee.getTraineeID());
		req.setInventoryID(inventory.getInventoryID());

		switch (currentOperation) {
		case ADD_NEW:
			req.setRequestType(RequestDTO.ADD_TRAINEE_EQUPIMENT);
			break;
		case UPDATE:
			req.setRequestType(RequestDTO.UPDATE_TRAINEE_EQUPIMENT);
			break;

		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();
		btnSave.setVisibility(View.GONE);
		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx,
				new BohaVolleyListener() {

					@Override
					public void onVolleyError(VolleyError error) {
						busyListener.setNotBusy();
						btnSave.setVisibility(View.VISIBLE);
						ToastUtil.errorToast(
								ctx,
								ctx.getResources().getString(
										R.string.error_server_comms));
					}

					@Override
					public void onResponseReceived(ResponseDTO response) {
						busyListener.setNotBusy();
						btnSave.setVisibility(View.VISIBLE);
						if (response.getStatusCode() > 0) {
							ToastUtil.errorToast(ctx, response.getMessage());
							return;
						}
						classSpinner.setSelection(0);
						traineeSpinner.setSelection(0);
						traineeEquipmentList = response.getTraineeEquipmentList();
						setList();
						
						equipmentUpdateListener.onTraineeEquipmentUpdated();
					}
				});
	}

	
	private void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		adapter = new TraineeEquipmentAdapter(getActivity(),
				R.layout.trainee_equipment_item, traineeEquipmentList,
				imageLoader, false);
		if (traineeEquipmentList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
		}
		txtCount.setText("" + traineeEquipmentList.size());
		listView.setAdapter(adapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				traineeEquipment = traineeEquipmentList.get(arg2);
				trainee = traineeEquipment.getTrainee();

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				traineeEquipment = traineeEquipmentList.get(arg2);
				trainee = traineeEquipment.getTrainee();
				return false;
			}
		});
	}

	private void setFields() {
		txtModel = (TextView) view.findViewById(R.id.TREQ_inventoryName);
		txtSerial = (TextView) view.findViewById(R.id.TREQ_serial);
		txtHeader = (TextView) view.findViewById(R.id.TREQ_header);

		classSpinner = (Spinner) view.findViewById(R.id.TREQ_spinnerClass);
		traineeSpinner = (Spinner) view.findViewById(R.id.TREQ_spinnerTrainee);
		txtCount = (TextView) view.findViewById(R.id.TREQ_count);
		listView = (ListView) view.findViewById(R.id.TREQ_list);
		btnSave = (Button) view.findViewById(R.id.TREQ_btnSave);

		txtTraineeName = (TextView) view.findViewById(R.id.RE_txtName);
		radioBroken = (RadioButton) view.findViewById(R.id.RE_radioBroken);
		radioLost = (RadioButton) view.findViewById(R.id.RE_radioLost);
		radioOK = (RadioButton) view.findViewById(R.id.RE_radioOK);
		btnReturn = (Button) view.findViewById(R.id.RE_btnSave);
		returnLayout = view.findViewById(R.id.RE_layout);
		returnLayout.setVisibility(View.GONE);

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentOperation = ADD_NEW;
				send();

			}
		});
		btnReturn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				returnEquipment();
			}
		});
		Button btnReturnCancel = (Button) view.findViewById(R.id.RE_btnCancel);
		btnReturnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnReturn.setEnabled(true);
				closeReturnLayout();
			}
		});
	}

	private void returnEquipment() {
		if (!radioOK.isChecked() && !radioBroken.isChecked()
				&& !radioLost.isChecked()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.select_condition));
			return;
		}
		if (traineeEquipment == null || traineeEquipment.getTraineeEquipmentID() == null) {
			ToastUtil.errorToast(ctx, "Trainee Equipment ID is null");
			Log.e(LOG, "Cannot update trainee equipment - null issues");
			return;
		}
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.UPDATE_TRAINEE_EQUPIMENT);
		req.setTraineeEquipmentID(traineeEquipment.getTraineeEquipmentID());
		req.setAdministratorID(SharedUtil.getAdministrator(ctx)
				.getAdministratorID());
		if (radioOK.isChecked())
			req.setConditionFlag(Integer.valueOf(OK));
		if (radioBroken.isChecked())
			req.setConditionFlag(Integer.valueOf(BROKEN));
		if (radioLost.isChecked())
			req.setConditionFlag(Integer.valueOf(LOST));
		req.setReturnEquipment(true);
		btnReturn.setEnabled(false);
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		busyListener.setBusy();

        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
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
                        ToastUtil.toast(
                                ctx,
                                ctx.getResources().getString(
                                        R.string.data_saved));
                        closeReturnLayout();
                        traineeEquipmentList = response.getTraineeEquipmentList();
                        setList();

                        equipmentUpdateListener.onTraineeEquipmentUpdated();
                        classSpinner.setSelection(0);
                        traineeSpinner.setSelection(0);
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

//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx,
//                new BohaVolleyListener() {
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        busyListener.setNotBusy();
//                        btnReturn.setEnabled(true);
//                        ToastUtil.errorToast(
//                                ctx,
//                                ctx.getResources().getString(
//                                        R.string.error_server_comms));
//
//                    }
//
//                    @Override
//                    public void onResponseReceived(ResponseDTO response) {
//                        busyListener.setNotBusy();
//                        if (response.getStatusCode() > 0) {
//                            ToastUtil.errorToast(ctx, response.getMessage());
//                            return;
//                        }
//                        ToastUtil.toast(
//                                ctx,
//                                ctx.getResources().getString(
//                                        R.string.data_saved));
//                        closeReturnLayout();
//                        traineeEquipmentList = response.getTraineeEquipmentList();
//                        setList();
//
//                        equipmentUpdateListener.onTraineeEquipmentUpdated();
//                        classSpinner.setSelection(0);
//                        traineeSpinner.setSelection(0);
//
//                    }
//                });
    }

	private void openReturnLayout() {
		returnLayout.setVisibility(View.VISIBLE);
		if (trainee != null)
			txtTraineeName.setText(trainee.getFullName());
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		returnLayout.startAnimation(a);
	}

	private void closeReturnLayout() {
		btnReturn.setEnabled(true);
		radioOK.setChecked(false);
		radioBroken.setChecked(false);
		radioLost.setChecked(false);
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.options_panel_exit);
		a.setDuration(1000);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				returnLayout.setVisibility(View.GONE);

			}
		});
		returnLayout.startAnimation(a);
	}

	private void getHistory() {
		Log.w(LOG, "getHistory , inventoryID: " + inventory.getInventoryID());
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_TRAINEE_EQUIPMENT_LIST_BY_INVENTORYID);
		req.setInventoryID(inventory.getInventoryID());
		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
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
                        traineeEquipmentList = response
                                .getTraineeEquipmentList();
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
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx,
//				new BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						busyListener.setNotBusy();
//						btnSave.setEnabled(true);
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO response) {
//						busyListener.setNotBusy();
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, response.getMessage());
//							return;
//						}
//						traineeEquipmentList = response
//								.getTraineeEquipmentList();
//						setList();
//					}
//				});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		traineeEquipment = traineeEquipmentList.get(info.position);
		if (traineeEquipment.getDateReturned() > 0) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.record_closed));
			return;
		}

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.trainee_equipment_context_menu, menu);

		menu.setHeaderTitle(traineeEquipment.getTrainee().getFullName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.clipboard32));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(LOG, "onContextItemSelected - " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_return_equipment:
			if (traineeEquipment.getDateReturned() == 0) {
				openReturnLayout();
			} else {
				ToastUtil
						.toast(ctx,
								ctx.getResources().getString(
										R.string.already_returned));
			}
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	private ContextMenuInterface contextListener;

	public ContextMenuInterface getContextListener() {
		return contextListener;
	}

	public void setContextListener(ContextMenuInterface contextListener) {
		this.contextListener = contextListener;
	}

	private BusyListener busyListener;
	TraineeEquipmentAdapter adapter;
	ResponseDTO response;
	List<TraineeEquipmentDTO> traineeEquipmentList;
	TraineeEquipmentDTO traineeEquipment;
	InventoryDTO inventory;
	CompanyDTO company;
	int type;
	EditText editEquipName;
	Button btnSave, btnCancel;
	LinearLayout layoutInventory;
	TextView txtCount, txtHeader, txtModel, txtSerial;
	Spinner classSpinner, traineeSpinner;
	ListView listView;
	static final String LOG = "TREQProvisioningFrag";
	private static final int ADD_NEW = 1, UPDATE = 2;
	private int currentOperation;
	ImageLoader imageLoader;

	@Override
	public void dummy() {
		// TODO Auto-generated method stub

	}

	public BusyListener getBusyListener() {
		return busyListener;
	}

	public void setBusyListener(BusyListener busyListener) {
		this.busyListener = busyListener;
	}

	List<TrainingClassDTO> trainingClassList;
	TrainingClassDTO trainingClass;
	List<TraineeDTO> traineeList;
	TraineeDTO trainee;
	TextView txtTraineeName;
	RadioButton radioOK, radioLost, radioBroken;
	Button btnReturn;
	View returnLayout;

	private void setClassSpinner() {
		Log.d(LOG, "setting classSpinner ...");
		final ArrayList<String> tarList = new ArrayList<String>();
		trainingClassList = response.getTrainingClassList();
		if (trainingClassList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_class));
			for (TrainingClassDTO p : trainingClassList) {
				tarList.add(p.getTrainingClassName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			classSpinner.setAdapter(dataAdapter);
			classSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == 0) {
								trainingClass = null;
								return;
							}
							trainingClass = trainingClassList.get(arg2 - 1);
							setTraineeSpinner();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
	}

	private void setTraineeSpinner() {
		Log.d(LOG, "setting traineeSpinner ...");
		final ArrayList<String> tarList = new ArrayList<String>();
		traineeList = trainingClass.getTraineeList();
		if (traineeList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_trainee));
			for (TraineeDTO p : traineeList) {
				tarList.add(p.getFullName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			traineeSpinner.setAdapter(dataAdapter);
			traineeSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == 0) {
								trainee = null;
								return;
							}
							trainee = traineeList.get(arg2 - 1);

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public void setInventory(InventoryDTO inventory) {
		this.inventory = inventory;
		if (txtModel != null) {
			txtSerial.setText(inventory.getSerialNumber());
			txtModel.setText(inventory.getModel());
		}
	}

	public void setResponse(ResponseDTO response) {
		this.response = response;
		setClassSpinner();
		getHistory();
	}

	public static final int OK = 0, BROKEN = 1, LOST = 3;
}
