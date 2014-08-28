package com.boha.coursemaker.dto;

public class DemoAnnouncementDTO {
	private int demoAnnouncementID;
    private long demoDate;
    private int teamID;
    private long dateRequested, cancellationDate;
    private String description;
    private int traineeID;
    private int trainingClassID;
	public int getDemoAnnouncementID() {
		return demoAnnouncementID;
	}
	public void setDemoAnnouncementID(int demoAnnouncementID) {
		this.demoAnnouncementID = demoAnnouncementID;
	}
	public long getDemoDate() {
		return demoDate;
	}
	public void setDemoDate(long demoDate) {
		this.demoDate = demoDate;
	}
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public long getDateRequested() {
		return dateRequested;
	}
	public void setDateRequested(long dateRequested) {
		this.dateRequested = dateRequested;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTraineeID() {
		return traineeID;
	}
	public void setTraineeID(int traineeID) {
		this.traineeID = traineeID;
	}
	public int getTrainingClassID() {
		return trainingClassID;
	}
	public void setTrainingClassID(int trainingClassID) {
		this.trainingClassID = trainingClassID;
	}
	public long getCancellationDate() {
		return cancellationDate;
	}
	public void setCancellationDate(long cancellationDate) {
		this.cancellationDate = cancellationDate;
	}
}
