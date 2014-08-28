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
public class TrainingClassDTO implements Serializable, CourseMakerData {

	private static final long serialVersionUID = 1L;

	private int trainingClassID, isOpen;
	private String trainingClassName;
	private long calendarID;
	private long startDate;
	private long endDate;
	private int totalTasks, totalComplete;
	private double percComplete;
	private AdministratorDTO administrator;
	private List<TrainingClassCourseDTO> trainingClassCourseList;
	private List<TraineeDTO> traineeList;
	private List<TrainingClassEventDTO> trainingClassEventList;
	private int companyID;


	public int getTrainingClassID() {
		return trainingClassID;
	}

	public void setTrainingClassID(int trainingClassID) {
		this.trainingClassID = trainingClassID;
	}

	public String getTrainingClassName() {
		return trainingClassName;
	}

	public void setTrainingClassName(String trainingClassName) {
		this.trainingClassName = trainingClassName;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public List<TrainingClassCourseDTO> getTrainingClassCourseList() {
		return trainingClassCourseList;
	}

	public void setTrainingClassCourseList(
			List<TrainingClassCourseDTO> trainingClassCourseList) {
		this.trainingClassCourseList = trainingClassCourseList;
	}

	public AdministratorDTO getAdministrator() {
		return administrator;
	}

	public void setAdministrator(AdministratorDTO administrator) {
		this.administrator = administrator;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public int getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}

	public List<TraineeDTO> getTraineeList() {
		return traineeList;
	}

	public void setTraineeList(List<TraineeDTO> traineeList) {
		this.traineeList = traineeList;
	}

	public int getTotalTasks() {
		return totalTasks;
	}

	public void setTotalTasks(int totalTasks) {
		this.totalTasks = totalTasks;
	}

	public int getTotalComplete() {
		return totalComplete;
	}

	public void setTotalComplete(int totalComplete) {
		this.totalComplete = totalComplete;
	}

	public double getPercComplete() {
		return percComplete;
	}

	public void setPercComplete(double percComplete) {
		this.percComplete = percComplete;
	}

	public long getCalendarID() {
		return calendarID;
	}

	public void setCalendarID(long calendarID) {
		this.calendarID = calendarID;
	}

	public List<TrainingClassEventDTO> getTrainingClassEventList() {
		return trainingClassEventList;
	}

	public void setTrainingClassEventList(
			List<TrainingClassEventDTO> trainingClassEventList) {
		this.trainingClassEventList = trainingClassEventList;
	}
}
