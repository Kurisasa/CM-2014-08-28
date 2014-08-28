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
public class CourseTraineeDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private int courseTraineeID;
    private long dateEnrolled;
    private long ratingDate;
    private String comment, courseName, traineeName;
    private RatingDTO rating;
    private int traineeID;  
    private int courseID;
    
  
    public int getCourseTraineeID() {
        return courseTraineeID;
    }

    public void setCourseTraineeID(int courseTraineeID) {
        this.courseTraineeID = courseTraineeID;
    }

    public long getDateEnrolled() {
        return dateEnrolled;
    }

    public void setDateEnrolled(long dateEnrolled) {
        this.dateEnrolled = dateEnrolled;
    }

    public long getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(long ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public RatingDTO getRating() {
        return rating;
    }

    public void setRating(RatingDTO rating) {
        this.rating = rating;
    }

    public int getTraineeID() {
        return traineeID;
    }

    public void setTraineeID(int traineeID) {
        this.traineeID = traineeID;
    }

   

	public String getTraineeName() {
		return traineeName;
	}

	public void setTraineeName(String traineeName) {
		this.traineeName = traineeName;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}
}
