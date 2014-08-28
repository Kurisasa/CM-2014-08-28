package com.boha.cminstructor.listeners;

import com.boha.coursemaker.dto.InstructorClassDTO;

public interface ClassListener {

	public void onClassPicked(InstructorClassDTO instructorClass);
    public void onRefreshRequested();
}
