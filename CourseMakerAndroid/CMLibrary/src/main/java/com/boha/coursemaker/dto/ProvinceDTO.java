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
public class ProvinceDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private int provinceID, countryID;   
    private String provinceName;
    private List<CityDTO> cityList;
    
   

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<CityDTO> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityDTO> cityList) {
        this.cityList = cityList;
    }
}
