package com.boha.coursemaker.util.actor;

import android.content.Context;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.RequestDTO;

import java.util.List;

public class InstructorUtil {

	public static RequestDTO getHelpRequestList(Context ctx,
			Integer trainingClassID, long startDate, long endDate) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_HELP_REQUEST_LIST);
		r.setTrainingClassID(trainingClassID);
		r.setStartDate(startDate);
		r.setEndDate(endDate);
		//
		return r;
	}
	public static RequestDTO getTraineeActivityTotalsByInstructor(Context ctx,
			Integer instructorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_TOTALS_BY_INSTRUCTOR);
		r.setInstructorID(instructorID);
		//
		return r;
	}
	public static RequestDTO getTraineeActivityTotalsByClass(Context ctx,
			Integer trainingClassID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_TOTALS);
		r.setTrainingClassID(trainingClassID);
		//
		return r;
	}

	public static RequestDTO getTraineeActivityTotalsByCompany(Context ctx,
			Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_TOTALS_BY_COMPANY);
		r.setCompanyID(companyID);
		//
		return r;
	}

	public static RequestDTO getCompanyCourseList(Context ctx, Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_TRAINING_COMPANY);
		r.setCompanyID(companyID);
		//
		return r;
	}

	public static RequestDTO registerInstructor(Context ctx,
			InstructorDTO instructor) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_INSTRUCTOR);
		r.setInstructor(instructor);
		//
		return r;
	}

	public static RequestDTO addInstructorRating(Context ctx,
			Integer instructorID, List<CourseTraineeActivityDTO> list) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_INSTRUCTOR_RATINGS);
		r.setInstructorID(instructorID);
		r.setCourseTraineeActivityList(list);
		//
		return r;
	}

	public static RequestDTO getClassActivityList(Context ctx,
			Integer trainingClassID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_CLASS_ACTIVITY_LIST);
		r.setTrainingClassID(trainingClassID);
		//
		return r;
	}

	public static RequestDTO getTraineeActivityList(Context ctx,
			Integer traineeID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_LIST);
		r.setTraineeID(traineeID);
		//
		return r;
	}

	public static RequestDTO getCompanyClassList(Context ctx, Integer companyID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_COMPANY_CLASS_LIST);
		r.setCompanyID(companyID);
		//
		return r;
	}

	public static RequestDTO loginInstructor(Context ctx, String email,
			String password) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.LOGIN_INSTRUCTOR);
		r.setEmail(email);
		r.setPassword(password);
		//
		return r;
	}

}
