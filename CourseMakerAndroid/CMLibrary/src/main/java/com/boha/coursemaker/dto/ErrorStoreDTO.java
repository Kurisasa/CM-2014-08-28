package com.boha.coursemaker.dto;

import java.io.Serializable;

public class ErrorStoreDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorStoreID;
    private int statusCode;
    private String message, origin;
    private long dateOccured;
	public int getErrorStoreID() {
		return errorStoreID;
	}
	public void setErrorStoreID(int errorStoreID) {
		this.errorStoreID = errorStoreID;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getDateOccured() {
		return dateOccured;
	}
	public void setDateOccured(long dateOccured) {
		this.dateOccured = dateOccured;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
