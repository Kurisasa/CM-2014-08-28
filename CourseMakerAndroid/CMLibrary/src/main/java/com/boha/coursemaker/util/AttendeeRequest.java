package com.boha.coursemaker.util;


public class AttendeeRequest {

	private long eventID;
	private String email,  attendeeName;
	
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAttendeeName() {
		return attendeeName;
	}
	public void setAttendeeName(String attendeeName) {
		this.attendeeName = attendeeName;
	}
}
