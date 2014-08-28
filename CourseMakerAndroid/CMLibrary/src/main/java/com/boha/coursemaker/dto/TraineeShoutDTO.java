package com.boha.coursemaker.dto;

public class TraineeShoutDTO {

	private int traineeShoutID;
	private long dateRegistered;
	private String remarks;
	private int traineeID;
	private int helpTypeID;

	public int getTraineeShoutID() {
		return traineeShoutID;
	}

	public void setTraineeShoutID(int traineeShoutID) {
		this.traineeShoutID = traineeShoutID;
	}

	public long getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(long dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getTraineeID() {
		return traineeID;
	}

	public void setTraineeID(int traineeID) {
		this.traineeID = traineeID;
	}

	public int getHelpTypeID() {
		return helpTypeID;
	}

	public void setHelpTypeID(int helpTypeID) {
		this.helpTypeID = helpTypeID;
	}
}
