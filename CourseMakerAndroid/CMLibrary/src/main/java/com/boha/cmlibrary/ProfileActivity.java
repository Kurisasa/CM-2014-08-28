package com.boha.cmlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.boha.cmlibrary.fragments.ProfileFragment;
import com.boha.coursemaker.listeners.BusyListener;

/**
 * A FragmentActivity that hosts a ProfileFragment
 * @author aubreyM
 */
public class ProfileActivity extends FragmentActivity implements BusyListener {

	ProfileFragment profileFragment;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ctx = getApplicationContext();
		profileFragment = (ProfileFragment) getSupportFragmentManager()
				.findFragmentById(R.id.profile_fragment);
		type = getIntent().getExtras().getInt("type", 0);

		profileFragment.setType(type);
		
		switch (type) {
		case ProfileFragment.ADMIN:
			setTitle(ctx.getResources().getString(R.string.admin_profile));
			break;
		case ProfileFragment.AUTHOR:
			setTitle(ctx.getResources().getString(R.string.auth_profile));
			break;
		case ProfileFragment.INSTRUCTOR:
			setTitle(ctx.getResources().getString(R.string.instructor_profile));
			break;

		default:
			break;
		}

	}

	int type;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profiler, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_back) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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
	public void onPause() {
		super.onPause();
		Log.i("CA", "*** onPause() - ...");
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

	}

	@Override
	public void setBusy() {
		setRefreshActionButtonState(true);

	}

	@Override
	public void setNotBusy() {
		setRefreshActionButtonState(false);

	}

	Menu mMenu;
	static final String LOG = "ProfileActivity";
}
