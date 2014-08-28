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
public class ImportCourseDTO implements Serializable, CourseMakerData {

    private static final long serialVersionUID = 1L;

    private int courseID, activeFlag, shareFlag;
    private long dateUpdated;
    private String courseName, description;
    private int categoryID;
    private String categoryName;
    private int companyID;
    private boolean selected;
    private long syncDate;
    private List<ActivityDTO> activityList;
    private List<ObjectiveDTO> objectiveList;
    private List<LessonResourceDTO> lessonResourceList;


    public List<ObjectiveDTO> getObjectiveList() {
        return objectiveList;
    }

    public void setObjectiveList(List<ObjectiveDTO> objectiveList) {
        this.objectiveList = objectiveList;
    }


    public List<LessonResourceDTO> getLessonResourceList() {
        return lessonResourceList;
    }

    public void setLessonResourceList(List<LessonResourceDTO> lessonResourceList) {
        this.lessonResourceList = lessonResourceList;
    }

    public long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }


    public int getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(int activeFlag) {
        this.activeFlag = activeFlag;
    }

    public int getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(int shareFlag) {
        this.shareFlag = shareFlag;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public List<ActivityDTO> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityDTO> activityList) {
        this.activityList = activityList;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }


    public Long getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Long syncDate) {
        this.syncDate = syncDate;
    }


    public void setSyncDate(long syncDate) {
        this.syncDate = syncDate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}
