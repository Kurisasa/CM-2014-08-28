package com.boha.coursemaker.util;

import java.io.Serializable;

public class Summary implements Serializable{

	private static final long serialVersionUID = 1L;
	public Summary() {
		
	}
	private int numberOfCourses, numberOfActivites, numberOfRatings, 
		numberOfCompletedActivities;
	
	private double percCompleted;
	
	public int getNumberOfCourses() {
		return numberOfCourses;
	}
	public void setNumberOfCourses(int numberOfCourses) {
		this.numberOfCourses = numberOfCourses;
	}
	public int getNumberOfActivites() {
		return numberOfActivites;
	}
	public void setNumberOfActivites(int numberOfActivites) {
		this.numberOfActivites = numberOfActivites;
	}
	public int getNumberOfRatings() {
		return numberOfRatings;
	}
	public void setNumberOfRatings(int numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}
	public int getNumberOfCompletedActivities() {		
		return numberOfCompletedActivities;
	}
	public void setNumberOfCompletedActivities(int numberOfCompletedActivities) {
		this.numberOfCompletedActivities = numberOfCompletedActivities;
	}
	public double getPercCompleted() {
		return percCompleted;
	}
	public void setPercCompleted(double percCompleted) {
		this.percCompleted = percCompleted;
	}

}
