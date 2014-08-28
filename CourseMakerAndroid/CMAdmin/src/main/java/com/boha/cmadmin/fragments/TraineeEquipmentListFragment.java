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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.boha.cmadmin.R;
import com.boha.cmadmin.adapter.TraineeEquipmentAdapter;
import com.boha.cmadmin.listeners.ContextMenuInterface;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.EquipmentDTO;
import com.boha.coursemaker.dto.InventoryDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeEquipmentDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.List;

public class TraineeEquipmentListFragment extends Fragment implements
		PageInterface {

	Context ctx;
	View view;

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
		super.onAttach(a);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_trainee_equipment_list,
				container, false);
		setFields();	
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO)b.getSerializable("response");
		}
		return view;
	}

	private void setList() {
		
		adapter = new TraineeEquipmentAdapter(getActivity(),
				R.layout.trainee_equipment_item, traineeEquipmentList,
				imageLoader, true);
		if (traineeEquipmentList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
		}
		txtCount.setText("" + traineeEquipmentList.size());
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		if (txtHeader != null) {
			txtHeader.setText(equipment.getEquipmentName());
		}

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
		
		txtCount = (TextView) view.findViewById(R.id.TREQ_count);
		txtHeader = (TextView) view.findViewById(R.id.TREQ_header);
		listView = (ListView) view.findViewById(R.id.TREQ_list);
	}
		
	public void refresh() {
		Log.w(LOG, "refresh , traineeEquipmnt list by equipment: " + equipment.getEquipmentName());
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_TRAINEE_EQUIPMENT_LIST_BY_EQUPMENTID);
		req.setEquipmentID(equipment.getEquipmentID());
		
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

//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx,
//                new BohaVolleyListener() {
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        busyListener.setNotBusy();
//                        //btnSave.setEnabled(true);
//                        ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.error_server_comms));
//                    }
//
//                    @Override
//                    public void onResponseReceived(ResponseDTO response) {
//                        busyListener.setNotBusy();
//                        if (response.getStatusCode() > 0) {
//                            ToastUtil.errorToast(ctx, response.getMessage());
//                            return;
//                        }
//                        traineeEquipmentList = response
//                                .getTraineeEquipmentList();
//                        setList();
//                    }
//                });
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		traineeEquipment = traineeEquipmentList.get(info.position);
		/*if (traineeEquipment.getDateReturned() > 0) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.record_closed));
			return;
		}*/

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.trainee_device_context_menu, menu);

		menu.setHeaderTitle(traineeEquipment.getTrainee().getFullName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.info_48));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(LOG, "onContextItemSelected - " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_list_equipment:
			ToastUtil.toast(ctx, "Under construction. Please watch the space!");
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
	static final String LOG = "TREQListFrag";
	
	ImageLoader imageLoader;

	@Override
	public void dummy() {
	}

	List<TrainingClassDTO> trainingClassList;
	TrainingClassDTO trainingClass;
	List<TraineeDTO> traineeList;
	TraineeDTO trainee;
	TextView txtTraineeName;
	RadioButton radioOK, radioLost, radioBroken;
	Button btnReturn;
	View returnLayout;
	EquipmentDTO equipment;


	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}


	public void setResponse(ResponseDTO response) {
		this.response = response;
		traineeEquipmentList = response.getTraineeEquipmentList();
		setList();
	}

	public static final int OK = 0, BROKEN = 1, LOST = 3;

	public EquipmentDTO getEquipment() {
		return equipment;
	}

	public void setEquipment(EquipmentDTO equipment) {
		this.equipment = equipment;
		if (txtHeader != null) {
			txtHeader.setText(equipment.getEquipmentName());
		}
	}
}
