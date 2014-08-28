package com.boha.cmadmin.listeners;

import com.boha.coursemaker.dto.TrainingClassDTO;

public interface ClassAddedUpdatedListener {

	public void onClassAdded(TrainingClassDTO tc);
	public void onClassUpdated(TrainingClassDTO tc);
	public void onClassDeleted(TrainingClassDTO tc);
}
