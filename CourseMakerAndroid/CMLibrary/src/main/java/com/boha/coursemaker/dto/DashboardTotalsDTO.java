package com.boha.coursemaker.dto;

import java.io.Serializable;

public class DashboardTotalsDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private int totalClasses, totalCourses, totalActivities, totalComplete;
    private double percComplete;
    private long lastDate;
	public int getTotalClasses() {
		return totalClasses;
	}
	public void setTotalClasses(int totalClasses) {
		this.totalClasses = totalClasses;
	}
	public int getTotalCourses() {
		return totalCourses;
	}
	public void setTotalCourses(int totalCourses) {
		this.totalCourses = totalCourses;
	}
	public int getTotalActivities() {
		return totalActivities;
	}
	public void setTotalActivities(int totalActivities) {
		this.totalActivities = totalActivities;
	}
	public int getTotalComplete() {
		return totalComplete;
	}
	public void setTotalComplete(int totalComplete) {
		this.totalComplete = totalComplete;
	}
	public double getPercComplete() {
		return percComplete;
	}
	public void setPercComplete(double percComplete) {
		this.percComplete = percComplete;
	}
	public long getLastDate() {
		return lastDate;
	}
	public void setLastDate(long lastDate) {
		this.lastDate = lastDate;
	}

}
