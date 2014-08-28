/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.coursemaker.dto;

import com.boha.coursemaker.util.CourseMakerData;

import java.io.Serializable;
import java.util.List;


/**
 * @author aubreyM
 */
public class TrainingClassCourseDTO implements Serializable, CourseMakerData {

    private static final long serialVersionUID = 1L;
    private int trainingClassCourseID;
    private long dateUpdated;
    private int numberOfActivities;
    private int trainingClassID, priorityFlag;
    private CourseDTO course;
    private List<ActivityDTO> activityList;
    private List<TrainingClassEventDTO> trainingClassEventList;
    private List<CourseTraineeActivityDTO> courseTraineeActivityList;

    public int getTrainingClassCourseID() {
        return trainingClassCourseID;
    }

    public void setTrainingClassCourseID(int trainingClassCourseID) {
        this.trainingClassCourseID = trainingClassCourseID;
    }

    public int getNumberOfActivities() {
        return numberOfActivities;
    }

    public void setNumberOfActivities(int numberOfActivities) {
        this.numberOfActivities = numberOfActivities;
    }

    public long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public int getTrainingClassID() {
        return trainingClassID;
    }

    public void setTrainingClassID(int trainingClassID) {
        this.trainingClassID = trainingClassID;
    }

    public int getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(int priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public List<ActivityDTO> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityDTO> activityList) {
        this.activityList = activityList;
    }

    public List<TrainingClassEventDTO> getTrainingClassEventList() {
        return trainingClassEventList;
    }

    public void setTrainingClassEventList(List<TrainingClassEventDTO> trainingClassEventList) {
        this.trainingClassEventList = trainingClassEventList;
    }

    public List<CourseTraineeActivityDTO> getCourseTraineeActivityList() {
        return courseTraineeActivityList;
    }

    public void setCourseTraineeActivityList(List<CourseTraineeActivityDTO> courseTraineeActivityList) {
        this.courseTraineeActivityList = courseTraineeActivityList;
    }
}
