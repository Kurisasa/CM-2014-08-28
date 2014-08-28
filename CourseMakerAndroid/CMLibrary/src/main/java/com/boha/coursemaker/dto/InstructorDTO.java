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
public class InstructorDTO  implements Serializable, CourseMakerData, Comparable<InstructorDTO> {
	   
		private static final long serialVersionUID = 1L;
	private int instructorID;
    private String firstName;    
    private String lastName;
    private String email;
    private String cellphone, password;
    private long dateRegistered;
    private int activeFlag;
    private String cityName, companyName;
    private int cityID, companyID;
    private List<GcmDeviceDTO> gcmDeviceList;
    private List<InstructorClassDTO> instructorClassList;
    private List<HelpResponseDTO> helpResponseList;
    @Override
	public int compareTo(InstructorDTO arg0) {
		
		String thisName = this.lastName + this.firstName;
		String anotherName = arg0.lastName + arg0.firstName;
		return thisName.compareTo(anotherName);
	}
    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(int activeFlag) {
		this.activeFlag = activeFlag;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public List<GcmDeviceDTO> getGcmDeviceList() {
		return gcmDeviceList;
	}

	public void setGcmDeviceList(List<GcmDeviceDTO> gcmDeviceList) {
		this.gcmDeviceList = gcmDeviceList;
	}

	public List<InstructorClassDTO> getInstructorClassList() {
		return instructorClassList;
	}

	public void setInstructorClassList(List<InstructorClassDTO> instructorClassList) {
		this.instructorClassList = instructorClassList;
	}

	public List<HelpResponseDTO> getHelpResponseList() {
		return helpResponseList;
	}

	public void setHelpResponseList(List<HelpResponseDTO> helpResponseList) {
		this.helpResponseList = helpResponseList;
	}
    
}
