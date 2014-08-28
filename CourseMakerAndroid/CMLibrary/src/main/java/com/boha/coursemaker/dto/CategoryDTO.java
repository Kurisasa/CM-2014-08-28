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
 *
 * @author aubreyM
 */
public class CategoryDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;


private int categoryID;
    
    private String categoryName;
    private List<CourseDTO> courseList;
    private int companyID, priorityFlag;
    private Long localID;
    private long syncDate;
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(int priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public List<CourseDTO> getCourseList() {
		return courseList;
	}

	public void setCourseList(List<CourseDTO> courseList) {
		this.courseList = courseList;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
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

	
    
   
    
}
