package com.boha.cmauthor.interfaces;

import com.boha.coursemaker.dto.LessonResourceDTO;

public interface ResourceListener {

	public void onResourcePicked(LessonResourceDTO resource);
	public void onShowProgressBar();
	public void onRemoveProgressBar();
}
