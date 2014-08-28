package com.boha.coursemaker.dto;

public class ObjectiveDTO {

	private int objectiveID;
	private String objectiveName, description;
	private int courseID;

	public int getObjectiveID() {
		return objectiveID;
	}

	public void setObjectiveID(int objectiveID) {
		this.objectiveID = objectiveID;
	}

	public String getObjectiveName() {
		return objectiveName;
	}

	public void setObjectiveName(String objectiveName) {
		this.objectiveName = objectiveName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}
}
