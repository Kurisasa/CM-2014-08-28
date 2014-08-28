package com.boha.coursemaker.dto;

import com.boha.coursemaker.listeners.ResponseInterface;

import java.io.Serializable;
import java.util.List;

public class StatsResponseDTO implements Serializable, ResponseInterface{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int statusCode;
	    private String message, logString;
	    private List<CompanyStatsDTO> statsList;
	    private List<ErrorStoreDTO> errorStoreList;
        private List<ErrorStoreAndroidDTO> errorStoreAndroidList;

    public List<ErrorStoreAndroidDTO> getErrorStoreAndroidList() {
        return errorStoreAndroidList;
    }

    public void setErrorStoreAndroidList(List<ErrorStoreAndroidDTO> errorStoreAndroidList) {
        this.errorStoreAndroidList = errorStoreAndroidList;
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
		public List<CompanyStatsDTO> getStatsList() {
			return statsList;
		}
		public void setStatsList(List<CompanyStatsDTO> statsList) {
			this.statsList = statsList;
		}
		public List<ErrorStoreDTO> getErrorStoreList() {
			return errorStoreList;
		}
		public void setErrorStoreList(List<ErrorStoreDTO> errorStoreList) {
			this.errorStoreList = errorStoreList;
		}
		public String getLogString() {
			return logString;
		}
		public void setLogString(String logString) {
			this.logString = logString;
		}
}
