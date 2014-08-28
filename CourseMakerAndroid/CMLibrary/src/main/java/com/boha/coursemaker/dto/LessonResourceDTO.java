/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.coursemaker.dto;

import com.boha.coursemaker.util.CourseMakerData;

import java.io.Serializable;

/**
 * 
 * @author aubreyM
 */
public class LessonResourceDTO implements Serializable, CourseMakerData {

	private static final long serialVersionUID = 1L;
    private int lessonResourceID;
    private String url, resourceName;
    private long syncDate;
    private long dateUpdated;
    private int courseID, instructorID, traineeID, authorID;

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getLessonResourceID() {
		return lessonResourceID;
	}

	public void setLessonResourceID(int lessonResourceID) {
		this.lessonResourceID = lessonResourceID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public long getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(long dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public int getInstructorID() {
		return instructorID;
	}

	public void setInstructorID(int instructorID) {
		this.instructorID = instructorID;
	}

	public int getTraineeID() {
		return traineeID;
	}

	public void setTraineeID(int traineeID) {
		this.traineeID = traineeID;
	}

	public int getAuthorID() {
		return authorID;
	}

	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}

	

	public long getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(long syncDate) {
		this.syncDate = syncDate;
	}


}
