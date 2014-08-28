package com.boha.coursemaker.dto;

import java.io.Serializable;

/**
 * 
 * @author aubreyM
 */
public class HelpResponseDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	 private int helpResponseID;
	    private String message;
	    private long dateResponse;
	    private int scheduleMeeting;
	    private int problemSorted, instructorID;
	    private long meetingDate;
	    private HelpRequestDTO helpRequest;
	public int getHelpResponseID() {
		return helpResponseID;
	}

	public void setHelpResponseID(int helpResponseID) {
		this.helpResponseID = helpResponseID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getDateResponse() {
		return dateResponse;
	}

	public void setDateResponse(long dateResponse) {
		this.dateResponse = dateResponse;
	}

	public int getScheduleMeeting() {
		return scheduleMeeting;
	}

	public void setScheduleMeeting(int scheduleMeeting) {
		this.scheduleMeeting = scheduleMeeting;
	}

	public int getProblemSorted() {
		return problemSorted;
	}

	public void setProblemSorted(int problemSorted) {
		this.problemSorted = problemSorted;
	}

	public long getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(long meetingDate) {
		this.meetingDate = meetingDate;
	}

	public HelpRequestDTO getHelpRequest() {
		return helpRequest;
	}

	public void setHelpRequest(HelpRequestDTO helpRequest) {
		this.helpRequest = helpRequest;
	}

	public int getInstructorID() {
		return instructorID;
	}

	public void setInstructorID(int instructorID) {
		this.instructorID = instructorID;
	}
}
