package com.boha.coursemaker.util.actor;

import android.content.Context;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.HelpRequestDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.TraineeDTO;

public class TraineeUtil {

	public static RequestDTO registerTrainee(Context ctx, TraineeDTO trainee,
			Integer trainingClassID, Integer cityID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_TRAINEE);
		r.setTrainee(trainee);
		r.setTrainingClassID(trainingClassID);
		r.setCityID(cityID);
		//
		return r;
	}

	public static RequestDTO enrollInCourse(Context ctx, Integer traineeID,
			Integer trainingClassCourseID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ENROLL_IN_COURSE);
		r.setTraineeID(traineeID);
		// r.setTrainingClassCourseID(trainingClassCourseID);
		return r;
	}

	public static RequestDTO evaluateTraineeActivity(Context ctx,
			CourseTraineeActivityDTO courseTraineeActivity, Integer traineeID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.EVALUATE_TRAINEE_ACTIVITY);
		r.setCourseTraineeActivity(courseTraineeActivity);
		r.setTraineeID(traineeID);
		return r;
	}

	public static RequestDTO addHelpRequest(Context ctx,
			HelpRequestDTO helpRequest, Integer trainingClassID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GCM_SEND_TRAINEE_TO_INSTRUCTOR_MSG);
		r.setHelpRequest(helpRequest);
		r.setTrainingClassID(trainingClassID);
		return r;
	}

	public static RequestDTO getTraineeActivityList(Context ctx,
			Integer traineeID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_TRAINEE_ACTIVITY_LIST);
		r.setTraineeID(traineeID);
		return r;
	}

	public static RequestDTO getCompanyCourseList(Context ctx, Integer companyID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_COMPANY_COURSE_LIST);
		r.setCompanyID(companyID);
		return r;
	}

	public static RequestDTO loginTrainee(Context ctx, String email,
			String password)  {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.LOGIN_TRAINEE);
		r.setEmail(email);
		r.setPassword(password);
		//

		return r;
	}

	public static final int TRAINEE = 1, INSTRUCTOR = 2, AUTHOR = 3, ADMIN = 4;
}
