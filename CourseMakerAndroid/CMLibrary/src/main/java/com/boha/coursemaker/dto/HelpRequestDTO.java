package com.boha.coursemaker.dto;

import com.boha.coursemaker.util.CourseMakerData;

import java.io.Serializable;

public class HelpRequestDTO  implements Serializable, CourseMakerData {
	   
		private static final long serialVersionUID = 1L;

	private Integer helpRequestID;
	private long dateRequested;
	private String comment;
	private CourseTraineeActivityDTO courseTraineeActivity;
	private HelpTypeDTO helpType;

	public Integer getHelpRequestID() {
		return helpRequestID;
	}

	public void setHelpRequestID(Integer helpRequestID) {
		this.helpRequestID = helpRequestID;
	}

	public long getDateRequested() {
		return dateRequested;
	}

	public void setDateRequested(long dateRequested) {
		this.dateRequested = dateRequested;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public CourseTraineeActivityDTO getCourseTraineeActivity() {
		return courseTraineeActivity;
	}

	public void setCourseTraineeActivity(
			CourseTraineeActivityDTO courseTraineeActivity) {
		this.courseTraineeActivity = courseTraineeActivity;
	}

	

	public HelpTypeDTO getHelpType() {
		return helpType;
	}

	public void setHelpType(HelpTypeDTO helpType) {
		this.helpType = helpType;
	}
}
