package com.boha.cmauthor.interfaces;

import com.boha.coursemaker.dto.ActivityDTO;

public interface TotalTapListener {

	public void onActivitiesTotalTap(ActivityDTO a);
	public void onObjectivesTotalTap(ActivityDTO a);
	public void onResourcesTotalTap(ActivityDTO a);
	public void onUpArrowTapped(int index);
	public void onDownArrowTapped(int index);
}
