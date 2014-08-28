package com.boha.coursemaker.dto;

import com.boha.coursemaker.util.CourseMakerData;

import java.io.Serializable;
import java.util.List;

public class InventoryDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private int inventoryID;
    private String serialNumber;
    private long dateRegistered;
    private int conditionFlag;
    private long dateUpdated;
    private String model;
    private int yearPurchased;
    private List<TraineeDTO> traineeList;
   
    private EquipmentDTO equipment;

	public int getInventoryID() {
		return inventoryID;
	}

	public void setInventoryID(int inventoryID) {
		this.inventoryID = inventoryID;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(long dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public int getConditionFlag() {
		return conditionFlag;
	}

	public void setConditionFlag(int conditionFlag) {
		this.conditionFlag = conditionFlag;
	}

	public long getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(long dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getYearPurchased() {
		return yearPurchased;
	}

	public void setYearPurchased(int yearPurchased) {
		this.yearPurchased = yearPurchased;
	}

	public EquipmentDTO getEquipment() {
		return equipment;
	}

	public void setEquipment(EquipmentDTO equipment) {
		this.equipment = equipment;
	}

	public List<TraineeDTO> getTraineeList() {
		return traineeList;
	}

	public void setTraineeList(List<TraineeDTO> traineeList) {
		this.traineeList = traineeList;
	}
}
