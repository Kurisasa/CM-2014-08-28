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
public class InstitutionDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;
	private int institutionID;
   
    private String institutionName;
    private CityDTO city;
    
   

    public int getInstitutionID() {
        return institutionID;
    }

    public void setInstitutionID(int institutionID) {
        this.institutionID = institutionID;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
        this.city = city;
    }
}
