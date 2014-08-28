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
public class TraineeStatusTypeDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private int traineeStatusTypeID;
    private String traineeStatusTypeName;
    
   

    public int getTraineeStatusTypeID() {
        return traineeStatusTypeID;
    }

    public void setTraineeStatusTypeID(int traineeStatusTypeID) {
        this.traineeStatusTypeID = traineeStatusTypeID;
    }

    public String getTraineeStatusTypeName() {
        return traineeStatusTypeName;
    }

    public void setTraineeStatusTypeName(String traineeStatusTypeName) {
        this.traineeStatusTypeName = traineeStatusTypeName;
    }
}
