package com.boha.coursemaker.dto;

import java.io.Serializable;

public class TotalsDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer traineeID, courseID, trainingClassCourseID,
			courseTraineeID, trainingClassID;
	private String courseName, trainingClassName;
	private double percComplete, averageRating;
	private int totalTasks, totalComplete, totalRatings;

	public Integer getTraineeID() {
		return traineeID;
	}

	public void setTraineeID(Integer traineeID) {
		this.traineeID = traineeID;
	}

	public Integer getCourseID() {
		return courseID;
	}

	public void setCourseID(Integer courseID) {
		this.courseID = courseID;
	}

	public Integer getTrainingClassCourseID() {
		return trainingClassCourseID;
	}

	public void setTrainingClassCourseID(Integer trainingClassCourseID) {
		this.trainingClassCourseID = trainingClassCourseID;
	}

	public Integer getCourseTraineeID() {
		return courseTraineeID;
	}

	public void setCourseTraineeID(Integer courseTraineeID) {
		this.courseTraineeID = courseTraineeID;
	}

	public int getTotalTasks() {
		return totalTasks;
	}

	public void setTotalTasks(int totalTasks) {
		this.totalTasks = totalTasks;
	}

	public int getTotalComplete() {
		return totalComplete;
	}

	public void setTotalComplete(int totalComplete) {
		this.totalComplete = totalComplete;
	}

	public int getTotalRatings() {
		return totalRatings;
	}

	public void setTotalRatings(int totalRatings) {
		this.totalRatings = totalRatings;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public double getPercComplete() {
		return percComplete;
	}

	public void setPercComplete(double percComplete) {
		this.percComplete = percComplete;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getTrainingClassID() {
		return trainingClassID;
	}

	public void setTrainingClassID(Integer trainingClassID) {
		this.trainingClassID = trainingClassID;
	}

	public String getTrainingClassName() {
		return trainingClassName;
	}

	public void setTrainingClassName(String trainingClassName) {
		this.trainingClassName = trainingClassName;
	}

}
