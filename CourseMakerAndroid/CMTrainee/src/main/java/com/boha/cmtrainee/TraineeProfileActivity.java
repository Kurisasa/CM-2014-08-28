package com.boha.cmtrainee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmtrainee.fragments.TraineeProfileFragment;
import com.boha.cmtrainee.interfaces.ImageCaptureListener;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.Locale;

public class TraineeProfileActivity extends FragmentActivity implements BusyListener,
ImageCaptureListener{

	TraineeProfileFragment traineeProfileFragment;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trainee_profile);
		ctx = getApplicationContext();
		traineeProfileFragment = (TraineeProfileFragment) getSupportFragmentManager().findFragmentById(R.id.trainee_fragment);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("CA", "*** onPause() - ...");		
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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
	Menu mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profile, menu);
		mMenu = menu;
        getProvinceList();
		return true;
	}
    private void getProvinceList() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_COUNTRY_LIST);
        w.setCountryCode(Locale.getDefault().getCountry());
        Log.w(TraineeProfileActivity.class.getName(), "############ Country Code is: " + w.getCountryCode());

        WebSocketUtil.sendRequest(ctx, Statics.TRAINEE_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx,response.getMessage());
                            return;
                        }
                        traineeProfileFragment.setCountryData(response);
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

    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:
			finish();
			return true;
		case R.id.menu_info:
			
			return true;
		
		case android.R.id.home:
			Intent intent = new Intent(this, MainPagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);
		
	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);
		
	}

	@Override
	public void onCameraRequest(int width, int height) {
		// TODO start camera ....
		
	}

	@Override
	public void onGalleryRequest() {
		// TODO Auto-generated method stub
		
	}
	


}
