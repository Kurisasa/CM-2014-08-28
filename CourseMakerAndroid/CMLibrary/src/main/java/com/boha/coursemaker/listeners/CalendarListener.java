package com.boha.coursemaker.listeners;

import com.boha.coursemaker.util.CalendarRequest;

import java.util.List;

public interface CalendarListener {

	public void onCalendarTaskDone(CalendarRequest calendarRequest);
	public void onCalendarQueryComplete(List<CalendarRequest> calendarRequests);
	public void onError();
}
