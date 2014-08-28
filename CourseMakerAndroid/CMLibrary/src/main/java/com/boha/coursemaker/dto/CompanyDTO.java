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
public class CompanyDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private int companyID;
    
    private String companyName;
    
    private String email;
    
    private long dateRegistered;
    private List<HelpTypeDTO> helpTypeList;
    private List<SkillDTO> skillList;
    private List<SkillLevelDTO> skillLevelList;
    private CityDTO city;
    private List<CourseDTO> courseList;
    
   
    

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
        this.city = city;
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

	public List<HelpTypeDTO> getHelpTypeList() {
		return helpTypeList;
	}

	public void setHelpTypeList(List<HelpTypeDTO> helpTypeList) {
		this.helpTypeList = helpTypeList;
	}
}
