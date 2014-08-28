package com.boha.cmauthor.interfaces;

import com.boha.coursemaker.dto.ActivityDTO;

public interface ActivityListener {

	public void onActivityPicked(ActivityDTO activity);
	public void onShowProgressBar();
	public void onRemoveProgressBar();
	public void onActivitiesAddedUpdated();
	public void onLinksAddedUpdated();
}
