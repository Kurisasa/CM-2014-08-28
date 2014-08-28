package com.boha.coursemaker.dto;

import com.boha.coursemaker.util.CourseMakerData;

import java.io.Serializable;
import java.util.List;



public class AdministratorDTO  implements Serializable, CourseMakerData, Comparable<AdministratorDTO> {
	   
		private static final long serialVersionUID = 1L;
	

	private int administratorID;
    private String firstName;
    private String lastName;
    private String email;
    private String cellphone, password;

    private long dateRegistered;
    private int activeFlag;
    private int superUserFlag;
    private int companyID;
    private List<GcmDeviceDTO> gcmDeviceList;
    @Override
	public int compareTo(AdministratorDTO arg0) {
		
		String thisName = this.lastName + this.firstName;
		String anotherName = arg0.lastName + arg0.firstName;
		return thisName.compareTo(anotherName);
	}
	public Integer getAdministratorID() {
		return administratorID;
	}

	public void setAdministratorID(Integer administratorID) {
		this.administratorID = administratorID;
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

	public int getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(int activeFlag) {
		this.activeFlag = activeFlag;
	}

	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
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

	public int getSuperUserFlag() {
		return superUserFlag;
	}

	public void setSuperUserFlag(int superUserFlag) {
		this.superUserFlag = superUserFlag;
	}
}
