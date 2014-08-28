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
public class ActivityDTO implements Serializable, Comparable<ActivityDTO>, CourseMakerData {
   
	private static final long serialVersionUID = 1L;
    private int activityID;
    private Double durationInDays;
    private String activityName;
    private String description;
    private int priorityFlag;
    private long localID;
    private long syncDate;
    private int courseID;

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public void setPriorityFlag(int priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public void setLocalID(long localID) {
        this.localID = localID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getActivityID() {
        return activityID;
    }



    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriorityFlag() {
        return priorityFlag;
    }



	@Override
	public int compareTo(ActivityDTO another) {
		if (this.priorityFlag > another.priorityFlag) {
			return 1;
		}
		if (this.priorityFlag > another.priorityFlag) {
			return 1;
		}
		return 0;
	}

	

	public long getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(long syncDate) {
		this.syncDate = syncDate;
	}

	public Long getLocalID() {
		return localID;
	}

	public void setLocalID(Long localID) {
		this.localID = localID;
	}


	public Double getDurationInDays() {
		return durationInDays;
	}

	public void setDurationInDays(Double durationInDays) {
		this.durationInDays = durationInDays;
	}
}
