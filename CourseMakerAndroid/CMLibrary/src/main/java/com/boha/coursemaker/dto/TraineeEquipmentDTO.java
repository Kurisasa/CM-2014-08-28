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
public class TraineeEquipmentDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;
private Integer traineeEquipmentID;
    
    private long dateRegistered;
    
    private long dateReturned;
    
    private String conditionFlag;
        
    private InventoryDTO inventory;
    
    private TraineeDTO trainee;

	public Integer getTraineeEquipmentID() {
		return traineeEquipmentID;
	}

	public void setTraineeEquipmentID(Integer traineeEquipmentID) {
		this.traineeEquipmentID = traineeEquipmentID;
	}

	public long getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(long dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public long getDateReturned() {
		return dateReturned;
	}

	public void setDateReturned(long dateReturned) {
		this.dateReturned = dateReturned;
	}

	public String getConditionFlag() {
		return conditionFlag;
	}

	public void setConditionFlag(String conditionFlag) {
		this.conditionFlag = conditionFlag;
	}

	public InventoryDTO getInventory() {
		return inventory;
	}

	public void setInventory(InventoryDTO inventory) {
		this.inventory = inventory;
	}

	public TraineeDTO getTrainee() {
		return trainee;
	}

	public void setTrainee(TraineeDTO trainee) {
		this.trainee = trainee;
	}
}
