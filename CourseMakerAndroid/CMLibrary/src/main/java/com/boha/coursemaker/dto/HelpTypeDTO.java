package com.boha.coursemaker.dto;

import com.boha.coursemaker.util.CourseMakerData;

import java.io.Serializable;

public class HelpTypeDTO implements Serializable, CourseMakerData, LookupInterface {

	private static final long serialVersionUID = 1L;

	private int helpTypeID, companyID;
	private String helpTypeName;

	public int getHelpTypeID() {
		return helpTypeID;
	}

	public void setHelpTypeID(int helpTypeID) {
		this.helpTypeID = helpTypeID;
	}

	public String getHelpTypeName() {
		return helpTypeName;
	}

	public void setHelpTypeName(String helpTypeName) {
		this.helpTypeName = helpTypeName;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
}
