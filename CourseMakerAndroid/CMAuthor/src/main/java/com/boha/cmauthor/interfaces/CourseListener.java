package com.boha.cmauthor.interfaces;

import com.boha.coursemaker.dto.CourseDTO;

public interface CourseListener {

	public void onCoursePicked(CourseDTO course);
	public void setBusy();
	public void setNotBusy();
	public void onCourseAdded(CourseDTO course);
}
