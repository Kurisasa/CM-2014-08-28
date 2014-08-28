package com.boha.coursemaker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.google.gson.Gson;

import java.util.Date;

public class SharedUtil {

	public static CourseDTO getLastCourse(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String j = sp.getString(Constants.LAST_COURSE_ID, null);
		Gson g = new Gson();
		if (j != null) {
			return g.fromJson(j, CourseDTO.class);
		}

		return null;
	}

	public static void setSessionID(Context ctx, String sessionID) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		Editor ed = sp.edit();
		ed.putString(Statics.SESSION_ID, sessionID);
		ed.commit();

        Log.w("SharedUtil", "#### web socket session id saved: " + sessionID);

	}
    public static String getSessionID(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String j = sp.getString(Statics.SESSION_ID, null);

        return j;
    }

    public static void setLastCourse(Context ctx, CourseDTO course) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        Gson g = new Gson();
        String json = g.toJson(course);
        Editor ed = sp.edit();
        ed.putString(Constants.LAST_COURSE_ID, json);
        ed.commit();

    }

	public static Date getLastCompletionDate(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		long cnt = sp.getLong(Constants.LAST_COMPLETION_DATE, 0);
		if (cnt == 0) return null;
		Date date = new Date(cnt);
		return date;
	}

	public static void setLastCompletionDate(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putLong(Constants.LAST_COMPLETION_DATE, new Date().getTime());
		ed.commit();

	}

	public static long getPhotoUploadDate(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		long cnt = sp.getInt(Constants.PHOTO_UPLOAD_DATE, 0);

		return cnt;
	}

	public static void setPhotoUploadDate(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putLong(Constants.PHOTO_UPLOAD_DATE, new Date().getTime());
		ed.commit();

	}

	public static int getCoursePressHoldCount(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		int cnt = sp.getInt(Constants.PRESS_HOLD_COURSE_COUNT, 0);

		return cnt;
	}

	public static void incrementCoursePressHoldCount(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		int cnt = sp.getInt(Constants.PRESS_HOLD_COURSE_COUNT, 0);
		cnt++;
		Editor ed = sp.edit();
		ed.putInt(Constants.PRESS_HOLD_COURSE_COUNT, cnt);
		ed.commit();

	}

	public static int getCategoryPressHoldCount(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		int cnt = sp.getInt(Constants.PRESS_HOLD_CATEGORY_COUNT, 0);

		return cnt;
	}

	public static void incrementCategoryPressHoldCount(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		int cnt = sp.getInt(Constants.PRESS_HOLD_CATEGORY_COUNT, 0);
		cnt++;
		Editor ed = sp.edit();
		ed.putInt(Constants.PRESS_HOLD_CATEGORY_COUNT, cnt);
		ed.commit();

	}

	public static AdministratorDTO getAdministrator(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.ADMIN_JSON, null);
		AdministratorDTO admin = null;
		if (adm != null) {
			admin = gson.fromJson(adm, AdministratorDTO.class);

		}
		return admin;
	}

	public static TraineeDTO getTrainee(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.TRAINEE_JSON, null);
		TraineeDTO admin = null;
		if (adm != null) {
			admin = gson.fromJson(adm, TraineeDTO.class);

		}
		return admin;
	}

	public static void saveCompany(Context ctx, CompanyDTO dto) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.COMPANY_JSON, x);
		ed.commit();
	}

	public static AuthorDTO getAuthor(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.AUTHOR_JSON, null);
		AuthorDTO admin = null;
		if (adm != null) {
			admin = gson.fromJson(adm, AuthorDTO.class);

		}
		return admin;
	}

	public static CompanyDTO getCompany(Context ctx) {

		if (ctx == null) {
			Log.e("SharedUtil", "ctx is null ....");
		}
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.COMPANY_JSON, null);
		CompanyDTO company = null;
		if (adm != null) {
			company = gson.fromJson(adm, CompanyDTO.class);

		}
		return company;
	}

	public static TrainingClassDTO getTrainingClass(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.TRAINING_CLASS, null);
		TrainingClassDTO cls = null;
		if (adm != null) {
			cls = gson.fromJson(adm, TrainingClassDTO.class);

		}
		return cls;
	}

	public static InstructorDTO getInstructor(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.INSTRUCTOR_JSON, null);
		InstructorDTO cls = null;
		if (adm != null) {
			cls = gson.fromJson(adm, InstructorDTO.class);

		}
		return cls;
	}

	public static String getImageUri(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String s = sp.getString(Constants.IMAGE_URI, null);

		return s;
	}

	public static String getThumbUri(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String s = sp.getString(Constants.THUMB_URI, null);

		return s;
	}

	public static void saveImageUri(Context ctx, Uri uri) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putString(Constants.IMAGE_URI, uri.toString());
		ed.commit();
	}

	public static void saveThumbUri(Context ctx, Uri uri) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putString(Constants.THUMB_URI, uri.toString());
		ed.commit();
	}
	public static void saveCalendarID(Context ctx, long id) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putLong(Constants.CALENDAR_ID, id);
		ed.commit();
	}
	public static long getCalendarID(Context ctx) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		long s = sp.getLong(Constants.CALENDAR_ID, -1);
		return s;
	}

	public static void saveInstructor(Context ctx, InstructorDTO dto) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.INSTRUCTOR_JSON, x);
		ed.commit();
	}

	public static void saveTrainee(Context ctx, TraineeDTO dto) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.TRAINEE_JSON, x);
		ed.commit();
	}

	public static void saveAuthor(Context ctx, AuthorDTO dto) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.AUTHOR_JSON, x);
		ed.commit();
	}

	public static void saveAdmin(Context ctx, AdministratorDTO dto) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.ADMIN_JSON, x);
		ed.commit();
	}

	public static void saveTrainingClass(Context ctx, TrainingClassDTO dto) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.TRAINING_CLASS, x);
		ed.commit();
	}

	private static final Gson gson = new Gson();
}
