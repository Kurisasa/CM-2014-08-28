package com.boha.coursemaker.util.actor;

import android.content.Context;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.util.exception.CommsException;
import com.boha.coursemaker.util.exception.NetworkUnavailableException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class AdminUtil {

	public static RequestDTO getCompanyEquipmentList(Context ctx,
			Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_COMPANY_EQUIPMENT_LIST);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO registerClass(Context ctx, Integer companyID,
			Integer administratorID, TrainingClassDTO trainingClass) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_TRAINING_CLASS);
		r.setAdministratorID(administratorID);
		r.setTrainingClass(trainingClass);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO registerAdministrator(Context ctx,
			Integer administratorID, AdministratorDTO administrator) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_ADMINISTRATOR);
		r.setAdministrator(administrator);
		r.setAdministratorID(administratorID);

		return r;
	}

	public static RequestDTO getHelpRequestList(Context ctx,
			Integer trainingClassID, long startDate, long endDate) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_HELP_REQUEST_LIST);
		r.setTrainingClassID(trainingClassID);
		r.setStartDate(startDate);
		r.setEndDate(endDate);

		return r;
	}

	public static RequestDTO getInventoryListByClass(Context ctx,
			Integer trainingClassID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_INVENTORY_LIST_BY_CLASS);
		r.setTrainingClassID(trainingClassID);

		return r;
	}

	public static RequestDTO getInventoryList(Context ctx, Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_INVENTORY_LIST);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO updateInventory(Context ctx,
			InventoryDTO inventory, Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.UPDATE_INVENTORY);
		r.setInventory(inventory);
		r.setAdministratorID(administratorID);

		return r;
	}

	public static RequestDTO addInventory(Context ctx, InventoryDTO inventory,
			Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_INVENTORY);
		r.setInventory(inventory);
		r.setAdministratorID(administratorID);

		return r;
	}

	public static RequestDTO getClassCourseTraineeActivityList(Context ctx,
			Integer trainingClassCourseID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_CLASS_COURSE_TRAINEE_ACTIVITY_LIST);
		// r.setTrainingClassCourseID(trainingClassCourseID);

		return r;
	}

	public static RequestDTO getClassTraineeEquipmentList(Context ctx,
			Integer trainingClassID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_CLASS_TRAINEE_EQUIPMENT_LIST);
		r.setTrainingClassID(trainingClassID);

		return r;
	}

	public static RequestDTO getClassCourseList(Context ctx,
			Integer trainingClassID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_CLASS_COURSE_LIST);
		r.setTrainingClassID(trainingClassID);

		return r;
	}

	public static RequestDTO getClassTraineeList(Context ctx,
			Integer trainingClassID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_CLASS_TRAINEE_LIST);
		r.setTrainingClassID(trainingClassID);

		return r;
	}

	public static RequestDTO getInstructorList(Context ctx, Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_INSTRUCTOR_LIST);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO addEquipment(Context ctx, EquipmentDTO equipment,
			Integer companyID, Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_EQUIPMENT);
		r.setAdministratorID(administratorID);
		r.setEquipment(equipment);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO updateEquipment(Context ctx,
			TraineeEquipmentDTO traineeEquipment, Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.UPDATE_TRAINEE_EQUPIMENT);
		r.setAdministratorID(administratorID);
		r.setTraineeEquipment(traineeEquipment);

		return r;
	}

	public static RequestDTO handOutEquipment(Context ctx,
			TraineeEquipmentDTO traineeEquipment, Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_TRAINEE_EQUPIMENT);
		r.setAdministratorID(administratorID);
		r.setTraineeEquipment(traineeEquipment);

		return r;
	}

	public static RequestDTO enrollClassTrainees(Context ctx,
			Integer administratorID, Integer trainingClassID,
			List<TraineeDTO> list) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ENROLL_TRAINEES_INTO_CLASS);
		r.setTrainingClassID(trainingClassID);
		r.setAdministratorID(administratorID);
		// r.setTraineeList(list);

		return r;
	}

	public static RequestDTO addCoursesToClass(Context ctx,
			Integer trainingClassID, List<CourseDTO> list,
			List<Integer> priorityFlags) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_COURSES_TO_CLASS);
		r.setTrainingClassID(trainingClassID);
		// r.setFlags(priorityFlags);
		// r.setCompanyCourseList(list);

		return r;
	}

	/**
	 * Request to add a trainingCompany class
	 * 
	 * @param ctx
	 * @param companyID
	 * @param trainingClass
	 * @param administratorID
	 * @return
	 * @throws CommsException
	 * @throws NetworkUnavailableException
	 * @throws UnsupportedEncodingException
	 */
	public static RequestDTO addTrainingClass(Context ctx, Integer companyID,
			TrainingClassDTO trainingClass, Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_TRAINING_CLASS);
		r.setCompanyID(companyID);
		r.setAdministratorID(administratorID);
		r.setTrainingClass(trainingClass);

		return r;
	}

	public static RequestDTO getCompanyCourseList(Context ctx, Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_TRAINING_COMPANY);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO getCompanyClassList(Context ctx, Integer companyID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_COMPANY_CLASS_LIST);
		r.setCompanyID(companyID);

		return r;
	}

	public static RequestDTO registerCompany(Context ctx, CompanyDTO company,
			AdministratorDTO admin) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_TRAINING_COMPANY);
		r.setCompany(company);
		r.setAdministrator(admin);

		return r;
	}

	public static RequestDTO getCountryList(Context ctx) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_COUNTRY_LIST);
		//

		return r;
	}

	public static RequestDTO loginAdministrator(Context ctx, String email,
			String password) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.LOGIN_ADMINISTRATOR);
		r.setEmail(email);
		r.setPassword(password);
		//

		return r;
	}

	public static RequestDTO deactivateInstructor(Context ctx,
			InstructorDTO instructor, Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DEACTIVATE_TRAINEE);
		r.setInstructor(instructor);

		return r;
	}

	public static RequestDTO deactivateTrainee(Context ctx, TraineeDTO trainee,
			Integer administratorID) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DEACTIVATE_TRAINEE);
		r.setTrainee(trainee);

		return r;
	}

	public static final int TRAINEE = 1, INSTRUCTOR = 2, AUTHOR = 3, ADMIN = 4;
}
