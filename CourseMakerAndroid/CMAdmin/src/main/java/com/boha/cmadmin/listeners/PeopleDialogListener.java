package com.boha.cmadmin.listeners;

import com.boha.coursemaker.dto.ResponseDTO;

public interface PeopleDialogListener {

	public void onRequestFinished(ResponseDTO response, int index);
	public void onError();
}
