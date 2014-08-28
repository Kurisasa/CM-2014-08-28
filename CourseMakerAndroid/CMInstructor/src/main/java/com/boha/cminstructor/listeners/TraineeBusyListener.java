package com.boha.cminstructor.listeners;

import com.boha.coursemaker.dto.TraineeDTO;

public interface TraineeBusyListener {
	public void onTraineePicked(TraineeDTO trainee);

	public void setBusy();

	public void setNotBusy();
}
