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
public class EquipmentDTO  implements Serializable,CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private int equipmentID;
   
    private String equipmentName;
    
    private int companyID;
    
    private List<InventoryDTO> inventoryList;

    public int getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(int equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    

	public List<InventoryDTO> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<InventoryDTO> inventoryList) {
		this.inventoryList = inventoryList;
	}

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
}
