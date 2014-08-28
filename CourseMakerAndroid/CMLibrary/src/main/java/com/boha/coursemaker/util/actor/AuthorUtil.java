package com.boha.coursemaker.util.actor;

import android.content.Context;
import com.boha.coursemaker.dto.*;

import java.util.List;

public class AuthorUtil {


	public static RequestDTO deleteCategory(Context ctx, CategoryDTO category) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DELETE_CATEGORY);
		r.setCategory(category);
		//

		return r;
	}

	public static RequestDTO updateCategory(Context ctx, CategoryDTO category) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.UPDATE_CATEGORY);
		r.setCategory(category);
		//

		return r;
	}

	public static RequestDTO deleteCourse(Context ctx, Integer courseID,
			Integer authorID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DELETE_COURSE);
		r.setAuthorID(authorID);
		r.setCourseID(courseID);
		//

		return r;
	}

	public static RequestDTO updateCourse(Context ctx, CourseDTO course,
			Integer authorID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.UPDATE_COURSE);
		r.setAuthorID(authorID);
		r.setCourse(course);
		//

		return r;
	}

	public static RequestDTO updateObjectives(Context ctx,
			List<ObjectiveDTO> list) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.UPDATE_OBJECTIVES);
		r.setObjectiveList(list);
		//

		return r;
	}

	public static RequestDTO updateActivities(Context ctx,
			List<ActivityDTO> list) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.UPDATE_ACTIVITIES);
		r.setActivityList(list);
		//

		return r;
	}

	public static RequestDTO getCategoryList(Context ctx, Integer companyID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_CATEGORY_LIST_BY_COMPANY);
		r.setCompanyID(companyID);
		//

		return r;
	}

	public static RequestDTO addCategory(Context ctx, Integer companyID,
			CategoryDTO category) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_CATEGORY);
		r.setCompanyID(companyID);
		r.setCategory(category);
		//

		return r;
	}

	public static RequestDTO getCoursesByCategory(Context ctx,
			Integer categoryID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_COURSE_LIST_BY_CATEGORY);
		r.setCategoryID(categoryID);
		//

		return r;
	}

	public static RequestDTO getLessonsByCourse(Context ctx, Integer courseID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_LESSON_LIST_BY_COURSE);
		r.setCourseID(courseID);
		//

		return r;
	}

	public static RequestDTO getObjectivesByCourse(Context ctx, Integer courseID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_OBJECTIVE_LIST_BY_COURSE);
		r.setCourseID(courseID);
		//

		return r;
	}

	public static RequestDTO getActivitiesByLesson(Context ctx, Integer lessonID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_ACTIVITY_LIST_BY_LESSON);
		r.setLessonID(lessonID);
		//

		return r;
	}

	public static RequestDTO getResourcesByLesson(Context ctx, Integer lessonID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.GET_RESOURCE_LIST_BY_LESSON);
		r.setLessonID(lessonID);
		//

		return r;
	}

	public static RequestDTO registerAuthor(Context ctx, AuthorDTO author,
			Integer companyID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_AUTHOR);
		r.setAuthor(author);
		r.setCompanyID(companyID);
		return r;
	}

	public static RequestDTO deleteResources(Context ctx,
			List<LessonResourceDTO> list, Integer lessonID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DELETE_LESSON_RESOURCES);
		r.setLessonResourceList(list);
		r.setLessonID(lessonID);
		//

		return r;
	}

	public static RequestDTO deleteActivitiess(Context ctx,
			List<ActivityDTO> list, Integer lessonID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DELETE_ACTIVITIES);
		r.setActivityList(list);
		r.setLessonID(lessonID);
		//

		return r;
	}

	public static RequestDTO deleteObjectives(Context ctx,
			List<ObjectiveDTO> list, Integer lessonID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DELETE_OBJECTIVES);
		r.setObjectiveList(list);
		r.setLessonID(lessonID);
		//

		return r;
	}



	public static RequestDTO addResource(Context ctx, LessonResourceDTO rl,
			Integer lessonID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_RESOURCES);
		r.setLessonID(lessonID);
		r.setLessonResource(rl);
		//

		return r;
	}

	public static RequestDTO addActivity(Context ctx, ActivityDTO a,
			Integer lessonID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_ACTIVITIES);
		r.setLessonID(lessonID);
		r.setActivity(a);
		//

		return r;
	}

	public static RequestDTO addObjective(Context ctx, ObjectiveDTO o,
			Integer lessonID) {

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ADD_OBJECTIVES);
		r.setLessonID(lessonID);
		r.setObjective(o);
		//

		return r;
	}


	public static RequestDTO registerCourse(Context ctx, CourseDTO course,
			Integer companyID, Integer authorID)

	{

		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.REGISTER_COURSE);
		r.setCourse(course);
		r.setCompanyID(companyID);
		r.setAuthorID(authorID);
		return r;
	}

	public static RequestDTO loginAuthor(Context ctx, String email,
			String password) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.LOGIN_AUTHOR);
		r.setEmail(email);
		r.setPassword(password);
		//

		return r;
	}

	public static RequestDTO loginTrainee(Context ctx, String email,
			String password) {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.LOGIN_TRAINEE);
		r.setEmail(email);
		r.setPassword(password);
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

	public static final int TRAINEE = 1, INSTRUCTOR = 2, AUTHOR = 3, ADMIN = 4;
}
