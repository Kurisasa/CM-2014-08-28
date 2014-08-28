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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.cmadmin.adapter.EquipmentAdapter;
import com.boha.cmadmin.listeners.EquipmentListener;
import com.boha.cmadmin.listeners.PageInterface;
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

import java.util.ArrayList;
import java.util.List;

public class EquipmentListFragment extends Fragment implements PageInterface {

	Context ctx;
	View view;
	EquipmentListener equipmentListener;
	View space;

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
		if (a instanceof EquipmentListener) {
			equipmentListener = (EquipmentListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity "+a.getLocalClassName()+" should implement EquipmentListener");
		}
		super.onAttach(a);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_equipment_list, container,
				false);
		setFields();
		company = SharedUtil.getCompany(ctx);
		Bundle b = getArguments();
		response = (ResponseDTO)b.getSerializable("response");
		equipmentList = response.getEquipmentList();
		setList();
		return view;
	}

	public void addEquipment() {
		openEditForm();
		txtHeader.setText(ctx.getResources().getString(
				R.string.admin_add_equipment));
		editEquipName.setText("");
		btnSave.setText(ctx.getResources().getString(R.string.save));
		currentOperation = ADD_NEW;
	}

	private void updateEquipment() {
		openEditForm();
		txtHeader.setText(ctx.getResources().getString(
				R.string.admin_update_equipment));
		editEquipName.setText(equipment.getEquipmentName());
		btnSave.setText(ctx.getResources().getString(R.string.save));
		currentOperation = UPDATE;
	}

	private void deleteEquipment() {
		openEditForm();
		txtHeader.setText(ctx.getResources().getString(
				R.string.admin_delete_equipment));
		editEquipName.setText(equipment.getEquipmentName());
		btnSave.setText(ctx.getResources().getString(R.string.delete));
		currentOperation = DELETE;
	}

	private void send() {
		if (editEquipName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_equipment));
			return;
		}
		RequestDTO req = new RequestDTO();
		req.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
		req.setAdministratorID(SharedUtil.getAdministrator(ctx)
				.getAdministratorID());
		EquipmentDTO e = new EquipmentDTO();
		e.setEquipmentName(editEquipName.getText().toString());

		switch (currentOperation) {
		case ADD_NEW:
			req.setRequestType(RequestDTO.ADD_EQUIPMENT);
			break;
		case UPDATE:
			req.setRequestType(RequestDTO.UPDATE_EQUIPMENT);
			e.setEquipmentID(equipment.getEquipmentID());
			break;
		case DELETE:
			req.setRequestType(RequestDTO.DELETE_EQUIPMENT);
			e.setEquipmentID(equipment.getEquipmentID());
			break;

		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		req.setEquipment(e);
		busyListener.setBusy();
		btnSave.setVisibility(View.GONE);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
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
                                equipmentList.add(0, response.getEquipment());
                                break;
                            case UPDATE:
                                int x = getIndex(response.getEquipment()
                                        .getEquipmentID());
                                if (x > -1) {
                                    equipmentList.remove(x);
                                    equipmentList.add(0, response.getEquipment());
                                }
                                break;
                            case DELETE:

                                break;

                            default:
                                break;
                        }
                        adapter.notifyDataSetChanged();
                        closeForm();
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
//							equipmentList.add(0, response.getEquipment());
//							break;
//						case UPDATE:
//							int x = getIndex(response.getEquipment()
//									.getEquipmentID());
//							if (x > -1) {
//								equipmentList.remove(x);
//								equipmentList.add(0, response.getEquipment());
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
				layoutEquip.setVisibility(View.GONE);
			}
		});
		layoutEquip.startAnimation(a);
	}

	private int getIndex(int id) {
		int index = 0;
		for (EquipmentDTO e : equipmentList) {
			if (e.getEquipmentID() == id) {
				return index;
			}
			index++;
		}
		return -1;
	}

	private void openEditForm() {
		layoutEquip.setVisibility(View.VISIBLE);
		space.setVisibility(View.VISIBLE);
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		layoutEquip.startAnimation(a);
	}


	private void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		adapter = new EquipmentAdapter(getActivity(), R.layout.equipment_item,
				equipmentList);
		if (equipmentList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
		}
		txtCount.setText("" + equipmentList.size());
		listView.setAdapter(adapter);
		listView.setDividerHeight(2);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				equipment = equipmentList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				equipment = equipmentList.get(arg2);
				return false;
			}
		});
	}

	private void setFields() {
		editEquipName = (EditText) view.findViewById(R.id.EQ_equipmentName);
		txtHeader = (TextView) view.findViewById(R.id.EQ_header);
		txtCount = (TextView) view.findViewById(R.id.EQ_count);
		listView = (ListView) view.findViewById(R.id.EQ_listView);
		layoutEquip = (LinearLayout) view.findViewById(R.id.EQ_editLayout);
		space = view.findViewById(R.id.EQ_layoutx);
		space.setVisibility(View.GONE);
		btnCancel = (Button) view.findViewById(R.id.EQ_btnCancel);
		btnSave = (Button) view.findViewById(R.id.EQ_btnSave);
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
						layoutEquip.setVisibility(View.GONE);
						space.setVisibility(View.GONE);
					}
				});
				layoutEquip.startAnimation(a);

			}
		});

	}

	public void upgradeEquipment(int equipmentID, List<InventoryDTO> list) {
		//match this id within list and replace - adapter nudge
		for (EquipmentDTO e : equipmentList) {
			if (e.getEquipmentID() == equipmentID) {
				if (e.getInventoryList() == null) {
					e.setInventoryList(new ArrayList<InventoryDTO>());
				}
				e.getInventoryList().addAll(list);
				adapter.notifyDataSetChanged();
				return;
			}
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.equipment_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		equipment = equipmentList.get(info.position);
		StringBuilder sb = new StringBuilder();
		sb.append(equipment.getEquipmentName()).append("  :  ");
		if (equipment.getInventoryList() != null) {
			sb.append(equipment.getInventoryList().size()).append(" ");
			sb.append(ctx.getResources().getString(R.string.items));
		}
		menu.setHeaderTitle(sb.toString());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.clipboard32));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		equipment = equipmentList.get(info.position);

		switch (item.getItemId()) {

		case R.id.menu_list_inventory:
			equipmentListener.onEquipmentSelected(equipment);
			return true;
		case R.id.menu_update_equipment:
			updateEquipment();
			return true;
		case R.id.menu_delete_equipment:
			deleteEquipment();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}



	private BusyListener busyListener;
	EquipmentAdapter adapter;
	ResponseDTO response;
	List<EquipmentDTO> equipmentList;
	EquipmentDTO equipment;
	CompanyDTO company;
	int type;
	static final int ADD_EQUIPMENT = 1, GET_EQUIPMENT_LIST = 2;
	EditText editEquipName;
	Button btnSave, btnCancel;
	LinearLayout layoutEquip;
	TextView txtCount, txtHeader;
	ListView listView;
	static final String LOG = "EquipmentListFragment";
	private static final int ADD_NEW = 1, UPDATE = 2, DELETE = 3;
	private int currentOperation;

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

	public EquipmentListener getEquipmentListener() {
		return equipmentListener;
	}

	public void setEquipmentListener(EquipmentListener equipmentListener) {
		this.equipmentListener = equipmentListener;
	}

}
