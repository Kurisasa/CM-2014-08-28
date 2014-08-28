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
public class CountryDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;
    
	private int countryID;  
    private String countryName;   
    private Double latitude;
    private Double longitude;
    private List<ProvinceDTO> provinceList;
	public int getCountryID() {
		return countryID;
	}
	public void setCountryID(int countryID) {
		this.countryID = countryID;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public List<ProvinceDTO> getProvinceList() {
		return provinceList;
	}
	public void setProvinceList(List<ProvinceDTO> provinceList) {
		this.provinceList = provinceList;
	}
    
   
}
