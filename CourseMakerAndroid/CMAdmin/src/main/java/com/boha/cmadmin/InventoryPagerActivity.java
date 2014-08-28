package com.boha.cmadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmadmin.fragments.InventoryListFragment;
import com.boha.cmadmin.fragments.TraineeEquipmentListFragment;
import com.boha.cmadmin.listeners.ContextMenuInterface;
import com.boha.cmadmin.listeners.InventoryAddedListener;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.cmadmin.listeners.TraineeEquipmentUpdateListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.EquipmentDTO;
import com.boha.coursemaker.dto.InventoryDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.volley.toolbox.BohaVolley;

import java.util.ArrayList;
import java.util.List;

public class InventoryPagerActivity extends FragmentActivity implements BusyListener,
		ContextMenuInterface, TraineeEquipmentUpdateListener, InventoryAddedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = getApplicationContext();
		setContentView(R.layout.view_pager_dark);
		mPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new PagerAdapter(getSupportFragmentManager());
		administrator = SharedUtil.getAdministrator(ctx);
		
		if (savedInstanceState != null) {
			Log.e(LOG, "restoring saved instance data ...");
			equipment = (EquipmentDTO)savedInstanceState.getSerializable("equipment");
			response = (ResponseDTO) savedInstanceState.getSerializable("response");
			inventoryListFragment.setEquipment(equipment);
		} else {
			getData();
		}
		buildPages();
		initializePager();
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	EquipmentDTO equipment;

	private void getData() {
		response = (ResponseDTO) getIntent().getExtras().getSerializable("response");
		equipment = (EquipmentDTO) getIntent().getExtras().getSerializable("equipment");
		
		if (equipment == null) {
			ToastUtil.errorToast(ctx, "Equipment is NULL! - Fix this!");
			finish();
		}
		getRemoteData();
	}
	@Override
	public void onPause() {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d(LOG, "onResume ...");
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		Log.d(LOG, "onSaveInstanceState ...");
		b.putSerializable("equipment", equipment);
		super.onSaveInstanceState(b);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.inventory, menu);
		mMenu = menu;		
		return true;
	}

	private void getRemoteData() {

		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_TRAINEE_EQUIPMENT_LIST_BY_EQUPMENTID);
		req.setEquipmentID(equipment.getEquipmentID());

		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,req,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        traineeEquipmentListFragment.setResponse(r);
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
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req,
//				getApplicationContext(), new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setRefreshActionButtonState(false);
//						ToastUtil.errorToast(
//								getApplicationContext(),
//								getResources().getString(
//										R.string.error_server_comms));
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						setRefreshActionButtonState(false);
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, response.getMessage());
//							return;
//						}
//						traineeEquipmentListFragment.setResponse(r);
//					}
//				});
	}


	ResponseDTO response;
	int currentPageIndex = 0;

	public void initializePager() {
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentPageIndex = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void buildPages() {
		inventoryListFragment = new InventoryListFragment();
		inventoryListFragment.setContextListener(this);
		inventoryListFragment.setEquipment(equipment);
		inventoryListFragment.setResponse(response);
		
		traineeEquipmentListFragment = new TraineeEquipmentListFragment();
		traineeEquipmentListFragment.setContextListener(this);
		traineeEquipmentListFragment.setImageLoader(BohaVolley.getImageLoader(ctx));
		traineeEquipmentListFragment.setEquipment(equipment);
		
		pageList.add(inventoryListFragment);
		pageList.add(traineeEquipmentListFragment);
		

	}

	private class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {

			return (Fragment) pageList.get(i);
		}

		@Override
		public int getCount() {
			return pageList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String title = "";
			switch (position) {
			case 0:
				title = ctx.getResources().getString(R.string.inventory_header);
				break;
			case 1:
				title = ctx.getResources().getString(R.string.trainee_equipment_list);
				break;
			

			default:
				break;
			}
			return title;
		}
	}

	Menu mMenu;

	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_add);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e(LOG, "onOptionsItemSelected - " + item.getTitle()
				+ " pageIndex: " + currentPageIndex);
		switch (item.getItemId()) {
		

		case R.id.menu_add:
			switch (currentPageIndex) {
			case 0:
				inventoryListFragment.addInventory();
				break;

			default:
				break;
			}
			
			return true;

		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	ViewPager mPager;
	List<PageInterface> pageList = new ArrayList<PageInterface>();
	Context ctx;
	PagerAdapter mAdapter;
	InventoryListFragment inventoryListFragment;
	TraineeEquipmentListFragment traineeEquipmentListFragment;
	

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
	}

	@Override
	public void redirectMenuItem(MenuItem menuItem) {
		Log.w(LOG, "Redirecting menu selection: " + menuItem.getTitle());
		switch (menuItem.getItemId()) {
		

		default:
			break;
		}

	}

	AdministratorDTO administrator;
	static final String LOG = "InventoryActivity";

	List<InventoryDTO> inventoryList = new ArrayList<InventoryDTO>();
	@Override
	public void onTraineeEquipmentUpdated() {
		Log.w(LOG, "onTraineeEquipmentUpdated - ask traineeEquipmentListFragment to refresh");
		traineeEquipmentListFragment.refresh();
		
	}
	@Override
	public void onInventoryAdded(InventoryDTO inventory) {
		inventoryList.add(inventory);
		
	}
	@Override
	public void onBackPressed() {
		Log.d(LOG, "onBackPressed inventoryList has: " + inventoryList.size());
		if (inventoryList.size() > 0) {
			ResponseDTO resp = new ResponseDTO();
			resp.setInventoryList(inventoryList);
			Intent data = new Intent();
			data.putExtra("response", resp);
			data.putExtra("equipmentID", equipment.getEquipmentID());
			setResult(Activity.RESULT_OK, data);
		} else {
			setResult(Activity.RESULT_CANCELED);
		}
		finish();
		super.onBackPressed();
	}
	
}
