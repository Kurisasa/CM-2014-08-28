package com.boha.coursemaker.util;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

public class CalendarRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int ADD_CALENDAR = 1, DELETE_CALENDAR = 2, UPDATE_CALENDAR = 3,
			ADD_EVENT = 4, UPDATE_EVENT = 5, DELETE_EVENT = 6,
			ADD_ATTENDEES = 7, GET_EVENT_BY_ID = 8, ADD_REMINDER = 9, QUERY_CALENDARS = 10;
	private String accountName, calendarName;
	private long calendarID, eventID, minutes;
	private String title;
	private String description;
	private String location; 
	private Integer trainingClassID;
	long dateStart,  dateEnd;
	Context context;
	private boolean removed;
	private int requestType;
	List<AttendeeRequest> attendeeRequestList;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getCalendarName() {
		return calendarName;
	}
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}
	public long getCalendarID() {
		return calendarID;
	}
	public void setCalendarID(long calendarID) {
		this.calendarID = calendarID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getDateStart() {
		return dateStart;
	}
	public void setDateStart(long dateStart) {
		this.dateStart = dateStart;
	}
	public long getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(long dateEnd) {
		this.dateEnd = dateEnd;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public int getRequestType() {
		return requestType;
	}
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public List<AttendeeRequest> getAttendeeRequestList() {
		return attendeeRequestList;
	}
	public void setAttendeeRequestList(List<AttendeeRequest> attendeeRequestList) {
		this.attendeeRequestList = attendeeRequestList;
	}
	public long getMinutes() {
		return minutes;
	}
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	public Integer getTrainingClassID() {
		return trainingClassID;
	}
	public void setTrainingClassID(Integer trainingClassID) {
		this.trainingClassID = trainingClassID;
	}
	public boolean isRemoved() {
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}
