package com.boha.coursemaker.dto;

import java.io.Serializable;

public class TrainingClassEventDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int trainingClassEventID;

    private long eventID;
    private String eventName;
    private String description;
    private long dateRegistered;
    private String location;
    private long startDate;
    private long endDate;
    private int trainingClassID, trainingClassCourseID;
    
	public int getTrainingClassEventID() {
		return trainingClassEventID;
	}
	public void setTrainingClassEventID(int trainingClassEventID) {
		this.trainingClassEventID = trainingClassEventID;
	}
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getDateRegistered() {
		return dateRegistered;
	}
	public void setDateRegistered(long dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public int getTrainingClassID() {
		return trainingClassID;
	}
	public void setTrainingClassID(int trainingClassID) {
		this.trainingClassID = trainingClassID;
	}
	public int getTrainingClassCourseID() {
		return trainingClassCourseID;
	}
	public void setTrainingClassCourseID(int trainingClassCourseID) {
		this.trainingClassCourseID = trainingClassCourseID;
	}
}
