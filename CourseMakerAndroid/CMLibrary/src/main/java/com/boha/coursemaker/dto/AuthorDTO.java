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
public class AuthorDTO implements Serializable, CourseMakerData,
		Comparable<AuthorDTO> {
	@Override
	public int compareTo(AuthorDTO arg0) {

		String thisName = this.lastName + this.firstName;
		String anotherName = arg0.lastName + arg0.firstName;
		return thisName.compareTo(anotherName);
	}

	private static final long serialVersionUID = 1L;

	private List<GcmDeviceDTO> gcmDeviceList;
	private int authorID;

	private String firstName;

	private String lastName;

	private String email;

	private String cellphone;

	private String password;

	private long dateRegistered;

	private int companyID, activeFlag;
	private List<CourseDTO> courseList;

	public int getAuthorID() {
		return authorID;
	}

	public void setAuthorID(int authorID) {
		this.authorID = authorID;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(long dateRegistered) {
		this.dateRegistered = dateRegistered;
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

	public List<GcmDeviceDTO> getGcmDeviceList() {
		return gcmDeviceList;
	}

	public void setGcmDeviceList(List<GcmDeviceDTO> gcmDeviceList) {
		this.gcmDeviceList = gcmDeviceList;
	}

	public int getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(int activeFlag) {
		this.activeFlag = activeFlag;
	}

}
