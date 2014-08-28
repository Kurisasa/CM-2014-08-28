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
public class LessonScheduleDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

private int lessonScheduleID;
    
    private long scheduleDate;
    
    private long endDate;
    
    private int lessonID;
    
    private int trainingClassID;   
    
  

    public int getLessonScheduleID() {
        return lessonScheduleID;
    }

    public void setLessonScheduleID(int lessonScheduleID) {
        this.lessonScheduleID = lessonScheduleID;
    }

    public long getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(long scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public int getTrainingClassID() {
        return trainingClassID;
    }

    public void setTrainingClassID(int trainingClassID) {
        this.trainingClassID = trainingClassID;
    }
}
