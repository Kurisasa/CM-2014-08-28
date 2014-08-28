package com.boha.cmadmin.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.cmadmin.TraineeEquipmentActivity;
import com.boha.cmadmin.adapter.InventoryAdapter;
import com.boha.cmadmin.listeners.ContextMenuInterface;
import com.boha.cmadmin.listeners.InventoryAddedListener;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.cmadmin.listeners.TraineeEquipmentUpdateListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.EquipmentDTO;
import com.boha.coursemaker.dto.InventoryDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.Calendar;
import java.util.List;

public class InventoryListFragment extends Fragment implements PageInterface {

	Context ctx;
	View view;
	TraineeEquipmentUpdateListener equipmentUpdateListener;
	InventoryAddedListener inventoryAddedListener;

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
		if (a instanceof InventoryAddedListener) {
			inventoryAddedListener = (InventoryAddedListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity should implement InventoryAddedListener");
		}
		super.onAttach(a);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_inventory_list, container,
				false);
		setFields();
		company = SharedUtil.getCompany(ctx);
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO)b.getSerializable("response");
		}
		setList();
		return view;
	}

	public void addInventory() {
		openEditForm();
		txtHeader.setText(ctx.getResources().getString(R.string.add_inventory)
				+ " - " + equipment.getEquipmentName());
		txtModel.setText("");
		txtSerial.setText("");
		txtYear.setText("");
		btnSave.setText(ctx.getResources().getString(R.string.save));
		currentOperation = ADD_NEW;
	}

	private void updateInventory() {
		openEditForm();
		txtHeader.setText(ctx.getResources().getString(
				R.string.admin_update_equipment));
		txtModel.setText(inventory.getModel());
		txtSerial.setText(inventory.getSerialNumber());
		txtYear.setText("" + inventory.getYearPurchased());
		btnSave.setText(ctx.getResources().getString(R.string.save));
		currentOperation = UPDATE;
	}

	private void deleteInventory() {
		openEditForm();
		txtHeader.setText(ctx.getResources().getString(
				R.string.admin_delete_equipment));
		txtModel.setText(inventory.getModel());
		txtSerial.setText(inventory.getSerialNumber());
		txtYear.setText("" + inventory.getYearPurchased());
		btnSave.setText(ctx.getResources().getString(R.string.delete));
		currentOperation = DELETE;
	}
	
	InventoryDTO newInventory;

	private void send() {
		if (txtModel.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_model));
			return;
		}
		if (txtSerial.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_serial));
			return;
		}
		if (txtYear.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_year));
			return;
		}
		try {
			Integer x = Integer.valueOf(txtYear.getText().toString());
			if (x < 2007 || x > Calendar.getInstance().get(Calendar.YEAR)) {
				ToastUtil.errorToast(ctx,
						ctx.getResources().getString(R.string.enter_year));
				return;
			}
		} catch (Exception e) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_year));
			return;
		}
		RequestDTO req = new RequestDTO();
		req.setAdministratorID(SharedUtil.getAdministrator(ctx)
				.getAdministratorID());
		newInventory = new InventoryDTO();
		newInventory.setModel(txtModel.getText().toString());
		newInventory.setSerialNumber(txtSerial.getText().toString());
		newInventory.setYearPurchased(Integer.valueOf(txtYear.getText().toString()));
		newInventory.setEquipment(equipment);

		switch (currentOperation) {
		case ADD_NEW:
			req.setRequestType(RequestDTO.ADD_INVENTORY);
			break;
		case UPDATE:
			req.setRequestType(RequestDTO.UPDATE_INVENTORY);
			newInventory.setInventoryID(inventory.getInventoryID());
			break;
		case DELETE:
			req.setRequestType(RequestDTO.DELETE_INVENTORY);
			newInventory.setInventoryID(inventory.getInventoryID());
			break;

		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		req.setInventory(newInventory);
		busyListener.setBusy();
		btnSave.setVisibility(View.GONE);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        btnSave.setVisibility(View.VISIBLE);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        switch (currentOperation) {
                            case ADD_NEW:
                                inventoryList = response.getInventoryList();
                                int i = 0;
                                for (InventoryDTO inv : inventoryList) {
                                    if (inv.getSerialNumber().equalsIgnoreCase(newInventory.getSerialNumber())) {

                                        break;
                                    }
                                    i++;
                                }
                                setList();
                                listView.setSelection(i);
                                inventoryAddedListener.onInventoryAdded(response.getInventory());
                                break;
                            case UPDATE:
                                int x = getIndex(response.getInventory()
                                        .getInventoryID());
                                if (x > -1) {
                                    inventoryList.remove(x);
                                    inventoryList.add(0, response.getInventory());
                                }
                                break;
                            case DELETE:

                                break;

                            default:
                                break;
                        }
                        adapter.notifyDataSetChanged();
                        txtCount.setText("" + inventoryList.size());
                        closeForm();
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
//						btnSave.setVisibility(View.VISIBLE);
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO response) {
//						busyListener.setNotBusy();
//						btnSave.setVisibility(View.VISIBLE);
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, response.getMessage());
//							return;
//						}
//						switch (currentOperation) {
//						case ADD_NEW:
//							inventoryList = response.getInventoryList();
//							int i = 0;
//							for (InventoryDTO inv : inventoryList) {
//								if (inv.getSerialNumber().equalsIgnoreCase(newInventory.getSerialNumber())) {
//
//									break;
//								}
//								i++;
//							}
//							setList();
//							listView.setSelection(i);
//							inventoryAddedListener.onInventoryAdded(response.getInventory());
//							break;
//						case UPDATE:
//							int x = getIndex(response.getInventory()
//									.getInventoryID());
//							if (x > -1) {
//								inventoryList.remove(x);
//								inventoryList.add(0, response.getInventory());
//							}
//							break;
//						case DELETE:
//
//							break;
//
//						default:
//							break;
//						}
//						adapter.notifyDataSetChanged();
//						txtCount.setText("" + inventoryList.size());
//						closeForm();
//					}
//				});
	}

	private void closeForm() {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.push_up_out);
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
				layoutInventory.setVisibility(View.GONE);
			}
		});
		layoutInventory.startAnimation(a);
	}

	private int getIndex(int id) {
		int index = 0;
		for (InventoryDTO e : inventoryList) {
			if (e.getInventoryID() == id) {
				return index;
			}
			index++;
		}
		return -1;
	}

	private void openEditForm() {
		layoutInventory.setVisibility(View.VISIBLE);
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		layoutInventory.startAnimation(a);
	}

	private void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		adapter = new InventoryAdapter(getActivity(), R.layout.inventory_item,
				inventoryList);
		if (inventoryList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
		}
		txtCount.setText("" + inventoryList.size());
		listView.setAdapter(adapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				inventory = inventoryList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				inventory = inventoryList.get(arg2);
				return false;
			}
		});
	}

	private void setFields() {
		txtModel = (EditText) view.findViewById(R.id.INV_model);
		txtSerial = (EditText) view.findViewById(R.id.INV_serial);
		txtYear = (EditText) view.findViewById(R.id.INV_year);

		txtEquipmentName = (TextView) view.findViewById(R.id.INV_label);
		txtHeader = (TextView) view.findViewById(R.id.INV_header);
		if (equipment != null) {
			txtEquipmentName.setText(equipment.getEquipmentName());
			txtHeader.setText(equipment.getEquipmentName());
		}

		if (equipment != null) {
			txtEquipmentName.setText(equipment.getEquipmentName());
		}
		txtCount = (TextView) view.findViewById(R.id.INV_count);
		listView = (ListView) view.findViewById(R.id.INV_listView);
		layoutInventory = (LinearLayout) view.findViewById(R.id.INV_editLayout);
		btnCancel = (Button) view.findViewById(R.id.INV_btnCancel);
		btnSave = (Button) view.findViewById(R.id.INV_btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				send();

			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Animation a = AnimationUtils.loadAnimation(ctx,
						R.anim.push_up_out);
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
						layoutInventory.setVisibility(View.GONE);
					}
				});
				layoutInventory.startAnimation(a);

			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.inventory_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		inventory = inventoryList.get(info.position);
		menu.setHeaderTitle(inventory.getModel());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.clipboard32));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(LOG, "onContextItemSelected - " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_update_inventory:
			updateInventory();
			return true;
		case R.id.menu_delete_inventory:
			deleteInventory();
			return true;
		case R.id.menu_assign_inventory:
			Intent i = new Intent(ctx, TraineeEquipmentActivity.class);
			i.putExtra("inventory", inventory);
			i.putExtra("response", response);
			startActivityForResult(i, REQUEST_TRAINEE_EQUIPMENT_ACTIVITY);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(LOG, "onActivityResult - resultCode: " + resultCode
				+ " - should be: " + Activity.RESULT_OK);
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TRAINEE_EQUIPMENT_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				Log.i(LOG,
						"Telling host equipmentUpdateListener.onTraineeEquipmentUpdated");
				boolean updated = data.getExtras().getBoolean("updated");
				if (updated)
					equipmentUpdateListener.onTraineeEquipmentUpdated();
			}
		}
	}

	private ContextMenuInterface contextListener;

	public ContextMenuInterface getContextListener() {
		return contextListener;
	}

	public void setContextListener(ContextMenuInterface contextListener) {
		this.contextListener = contextListener;
	}

	private static final int REQUEST_TRAINEE_EQUIPMENT_ACTIVITY = 1;
	private BusyListener busyListener;
	InventoryAdapter adapter;
	ResponseDTO response;
	List<InventoryDTO> inventoryList;
	InventoryDTO inventory;
	CompanyDTO company;
	int type;
	EditText editEquipName;
	Button btnSave, btnCancel;
	LinearLayout layoutInventory;
	TextView txtCount, txtHeader, txtEquipmentName;
	EditText txtModel, txtSerial, txtYear;
	ListView listView;
	static final String LOG = "InventoryListFragment";
	private static final int ADD_NEW = 1, UPDATE = 2, DELETE = 3;
	private int currentOperation;
	private EquipmentDTO equipment;

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

	public EquipmentDTO getEquipment() {
		return equipment;
	}

	public void setEquipment(EquipmentDTO equipment) {
		this.equipment = equipment;
		inventoryList = equipment.getInventoryList();
	}

	public ResponseDTO getResponse() {
		return response;
	}

	public void setResponse(ResponseDTO response) {
		this.response = response;
	}

}
