package com.boha.coursemaker.util;

public interface Constants 
{
	static final int NETWORK_SOURCE =  0;
	static final int DATABASE_SOURCE = 1; 
	
	//
	static final int ERROR_ENCODING = 900; 
	static final int ERROR_DATABASE = 901;
	static final int ERROR_NETWORK_UNAVAILABLE = 902;
	static final int ERROR_SERVER_COMMS = 903;
	static final int ERROR_DUPLICATE = 904;
	static final int ERROR_LOCAL_DATABASE = 910;
	static final int ERROR_IMAGE_UPLOAD = 915;
	
	static final int ERROR_COMPRESSION_FAILURE = 905;
	static final String COMPRESSION_ERROR = "Server unable to compress response";
	
	static final String LAST_COMPLETION_DATE =  "lastCompleteDate";
	
	static final String AUTHOR_ID =  "authorID";
	static final String INSTRUCTOR_ID =  "instructorID";
	static final String AUTHOR_NAME =  "author";
	static final String LAST_COURSE_ID =  "courseID";
	static final String EMAIL =  "EMAIL";
	static final String PASSWORD =  "PSWD";
	static final String CELLPHONE =  "CELLPHONE";
	static final String CALENDAR_ID =  "calendarID";
	static final String CALENDAR_NAME =  "calendarName";
	
	static final String GCM_REGISTRATION_ID =  "GCM_RegID";
	static final String APP_VERSION = "appVersion";
	
	static final String IMAGE_URI = "imageUri";
	static final String THUMB_URI = "thumbUri";
	
	static final String INSTRUCTOR_JSON = "instJSON";
	static final String ADMIN_JSON = "admJSON";
	static final String COMPANY_JSON = "companyJSON";
	static final String AUTHOR_JSON = "authorJSON";
	static final String TRAINEE_JSON = "traineeJSON";
	static final String TRAINING_CLASS = "trainingClassJSON";
	static final String PHOTO_UPLOAD_DATE = "photoUploadDate";
	
	static final String PRESS_HOLD_CATEGORY_COUNT = "pressHoldCat";
	static final String PRESS_HOLD_COURSE_COUNT = "pressHoldCourse";
	
}
