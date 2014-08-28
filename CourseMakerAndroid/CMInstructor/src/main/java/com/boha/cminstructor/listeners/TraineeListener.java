package com.boha.cminstructor.listeners;

import com.boha.coursemaker.dto.TraineeDTO;

public interface TraineeListener {

	public void onTraineePicked(TraineeDTO trainee);	
	public void setBusy();
	public void setNotBusy();
}
