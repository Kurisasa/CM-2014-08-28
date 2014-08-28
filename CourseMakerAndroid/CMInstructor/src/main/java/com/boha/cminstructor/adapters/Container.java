package com.boha.cminstructor.adapters;

import java.io.Serializable;
import java.util.List;

import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;

public class Container implements Serializable {


	private static final long serialVersionUID = 1L;
	public Container() {
	}
	private List<CourseTraineeActivityDTO> courseTraineeActivityList;
	private List<TrainingClassCourseDTO> trainingClassCourseList;
	private List<LessonResourceDTO> resourceList;
	public List<CourseTraineeActivityDTO> getCourseTraineeActivityList() {
		return courseTraineeActivityList;
	}
	public void setCourseTraineeActivityList(
			List<CourseTraineeActivityDTO> courseTraineeActivityList) {
		this.courseTraineeActivityList = courseTraineeActivityList;
	}
	public List<TrainingClassCourseDTO> getTrainingClassCourseList() {
		return trainingClassCourseList;
	}
	public void setTrainingClassCourseList(
			List<TrainingClassCourseDTO> trainingClassCourseList) {
		this.trainingClassCourseList = trainingClassCourseList;
	}
	public List<LessonResourceDTO> getResourceList() {
		return resourceList;
	}
	public void setResourceList(List<LessonResourceDTO> resourceList) {
		this.resourceList = resourceList;
	}

}
