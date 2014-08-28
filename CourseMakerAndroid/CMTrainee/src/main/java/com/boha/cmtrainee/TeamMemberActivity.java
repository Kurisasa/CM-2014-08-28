package com.boha.cmtrainee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.boha.cmtrainee.fragments.TeamMemberListFragment;
import com.boha.cmtrainee.interfaces.TeamMemberAddedListener;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TeamMemberDTO;
import com.boha.coursemaker.listeners.BusyListener;

import java.util.ArrayList;
import java.util.List;

public class TeamMemberActivity extends FragmentActivity implements
		BusyListener, TeamMemberAddedListener {

	TeamMemberListFragment teamMemberListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_member);
		teamMemberListFragment = (TeamMemberListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.teamMemberListFragment);
		response = (ResponseDTO) getIntent().getSerializableExtra("response");
		teamMemberListFragment.setTeam(response.getTeam());
		teamMemberListFragment.setTraineeList(response.getTraineeList());
		setTitle(response.getTeam().getTeamName());

        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	ResponseDTO response;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.team_member, menu);
		mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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

	private List<TeamMemberDTO> teamMemberList = new ArrayList<TeamMemberDTO>();
	Menu mMenu;

	@Override
	public void onTeamMemberAdded(List<TeamMemberDTO> list) {
		Log.w(LOG, "onTeamMemberAdded fired, memebers: " + list.size());
		teamMemberList = list;
        teamMemberListFragment.refresh(list);
	}
	@Override
	public void onBackPressed() {
		Log.w(LOG, "onBackPressed------------");
		if (teamMemberList.isEmpty()) {
			setResult(Activity.RESULT_CANCELED);
		} else {
			Intent i = new Intent();
			ResponseDTO r = new ResponseDTO();
			r.setTeamMemberList(teamMemberList);
			i.putExtra("response", r);
			setResult(Activity.RESULT_OK, i);
			Log.w(LOG, "have setResult with teamMembers added: " + r.getTeamMemberList().size());
		}
		finish();
		
		super.onBackPressed();
	}
	static final String LOG = "TeamMemberActivity";
}
