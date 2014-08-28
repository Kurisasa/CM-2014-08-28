package com.boha.cmadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.boha.cmadmin.fragments.TraineeListFragment;
import com.boha.cmadmin.listeners.CameraRequestListener;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BitmapListener;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.Bitmaps;
import com.boha.coursemaker.util.ImageTask;
import com.boha.coursemaker.util.PictureUtil;
import com.boha.coursemaker.util.ToastUtil;

public class ClassTraineeListActivity extends FragmentActivity implements BusyListener,
	CameraRequestListener{

	TraineeListFragment traineeListFragment;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_trainee_list);
		ctx = getApplicationContext();
		traineeListFragment = (TraineeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.classTraineeFragment);
		response = (ResponseDTO)getIntent().getSerializableExtra("response");
		traineeListFragment.setResponse(response);
		setTitle(response.getTrainingClass().getTrainingClassName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.class_trainee_list, menu);
		mMenu = menu;
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_back:
			finish();
			return true;
		
		}
		return false;
	}
	@Override
	public void onPause() {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
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
	private ResponseDTO response;
	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}

	
	Uri fileUri;

	private void startCameraIntent(int width, int height) {
		fileUri = PictureUtil.getImageFileUri();
		Intent cameraIntent = getCameraIntent(width, height, fileUri);	
		startActivityForResult(cameraIntent, CAPTURE_IMAGE);
	}

	private static Intent getCameraIntent(int width, int height, Uri fileUri) {

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra("crop", "true");
		cameraIntent.putExtra("outputX", width);
		cameraIntent.putExtra("outputY", height);
		cameraIntent.putExtra("aspectX", 1);
		cameraIntent.putExtra("aspectY", 1);
		cameraIntent.putExtra("scale", true);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		return cameraIntent;

	}

	public static final int CAPTURE_IMAGE = 309;
	int requestorType, id;
	@Override
	public void onCameraRequested(int width, int height, int requestorType, int id) {
		this.requestorType = requestorType;
        this.id = id;
		Log.i(LOG, "onCameraRequested - requestor type = " + requestorType);
		startCameraIntent(width, height);
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w(LOG, "onActivityResult -- requestCode: " + requestCode
				+ " resultCode: " + resultCode);
		
		if (requestCode == CAPTURE_IMAGE) {
			Log.i(LOG, "back from camera, should resize and store");
			if (resultCode == Activity.RESULT_OK) {
				ImageTask.getResizedBitmaps(fileUri, ctx, new BitmapListener() {
					@Override
					public void onError() {
						Log.e(LOG, "failed to resize resize bitmap");
						ToastUtil.errorToast(
								ctx,
								ctx.getResources().getString(
										R.string.error_image_get));
					}

					@Override
					public void onBitmapsResized(Bitmaps bitmaps) {
						Log.i(LOG, "resizedimage, set on fragment bitmaps");
						switch (requestorType) {
						
						case PhotoUploadDTO.TRAINEE:
							traineeListFragment.setBitmaps(bitmaps);
							break;

						default:
							break;
						}
					}
				});
			} else {
				Log.e(LOG, "back from camera, activity result code not OK");
				ToastUtil.toast(
						ctx,
						ctx.getResources().getString(
								R.string.image_capture_cancelled));
			}
		}

	}


	static final String LOG = "ClassTraineeListActivity";
}
