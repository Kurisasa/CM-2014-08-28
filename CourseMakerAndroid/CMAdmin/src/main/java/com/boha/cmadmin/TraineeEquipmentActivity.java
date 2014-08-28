package com.boha.cmadmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.toolbox.ImageLoader;
import com.boha.cmadmin.fragments.TraineeEquipmentProvisioningFragment;
import com.boha.cmadmin.listeners.TraineeEquipmentUpdateListener;
import com.boha.coursemaker.dto.InventoryDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.volley.toolbox.BohaVolley;

public class TraineeEquipmentActivity extends FragmentActivity implements
		BusyListener, TraineeEquipmentUpdateListener {

	boolean updateHappened;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trainee_equipment);
		equipmentProvisioningFragment = (TraineeEquipmentProvisioningFragment) getSupportFragmentManager()
				.findFragmentById(R.id.te_fragment);
		imageLoader = BohaVolley.getImageLoader(getApplicationContext());
		getData();
        setTitle(getApplicationContext().getResources().getString(R.string.trainee_equipment));
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getData() {
		inventory = (InventoryDTO) getIntent().getExtras().getSerializable(
				"inventory");
		response = (ResponseDTO) getIntent().getExtras().getSerializable(
				"response");
		equipmentProvisioningFragment.setInventory(inventory);
		equipmentProvisioningFragment.setResponse(response);
		equipmentProvisioningFragment.setImageLoader(imageLoader);
	}

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
		//getMenuInflater().inflate(R.menu.trainee_equipment, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e(LOG, "onOptionsItemSelected - " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_back:
			onBackPressed();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		
		Log.w(LOG, "onBackPressed - updateHappened is currently "
				+ updateHappened);
		Intent i = new Intent();
		i.putExtra("updated", updateHappened);
		setResult(Activity.RESULT_OK, i);
		finish();
		super.onBackPressed();
	}

	int currentPageIndex;
	Menu mMenu;
	TraineeEquipmentProvisioningFragment equipmentProvisioningFragment;
	InventoryDTO inventory;
	ResponseDTO response;
	ImageLoader imageLoader;
	private static final String LOG = "TraineeEquipmentActivity";

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);

	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

	}

	@Override
	public void onTraineeEquipmentUpdated() {
		Log.w(LOG, "onTraineeEquipmentUpdated setting updateHappened true");
		updateHappened = true;

	}
}
