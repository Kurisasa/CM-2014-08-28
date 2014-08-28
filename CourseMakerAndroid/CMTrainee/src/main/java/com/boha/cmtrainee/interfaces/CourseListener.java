package com.boha.cmtrainee.interfaces;

import com.boha.coursemaker.dto.TrainingClassCourseDTO;

public interface CourseListener {

	public void onCoursePicked(TrainingClassCourseDTO course);
	public void setBusy();
	public void setNotBusy();
}
