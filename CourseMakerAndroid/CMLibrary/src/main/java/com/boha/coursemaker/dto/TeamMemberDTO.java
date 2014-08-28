package com.boha.coursemaker.dto;

import java.io.Serializable;

public class TeamMemberDTO  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer teamMemberID, companyID;
	private long dateRegistered;
	private Integer activeFlag;
	private Integer traineeID;
	private Integer teamID;
	 private String firstName, lastName, email, cellphone;

	public Integer getTeamMemberID() {
		return teamMemberID;
	}

	public void setTeamMemberID(Integer teamMemberID) {
		this.teamMemberID = teamMemberID;
	}

	public long getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(long dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Integer getTraineeID() {
		return traineeID;
	}

	public void setTraineeID(Integer traineeID) {
		this.traineeID = traineeID;
	}

	public Integer getTeamID() {
		return teamID;
	}

	public void setTeamID(Integer teamID) {
		this.teamID = teamID;
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

	public Integer getCompanyID() {
		return companyID;
	}

	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}
}
