package com.boha.cmauthor.interfaces;

import com.boha.coursemaker.dto.ObjectiveDTO;

public interface ObjectiveListener {

	public void onObjectivePicked(ObjectiveDTO objective);
	public void onShowProgressBar();
	public void onRemoveProgressBar();
}
