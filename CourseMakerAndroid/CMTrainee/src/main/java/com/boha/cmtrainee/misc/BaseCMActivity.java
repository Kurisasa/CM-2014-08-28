package com.boha.cmtrainee.misc;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.util.Summary;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCMActivity extends FragmentActivity {

	protected Context ctx;
	protected ResponseDTO response;

	public BaseCMActivity() {
	}

	public abstract void getData();

	public abstract void setFields();

	public abstract void setList();

	public Summary getSummary() {
		if (courseTraineeActivityList == null) return new Summary(); 
		summary = new Summary();
		summary.setNumberOfActivites(courseTraineeActivityList.size());
		summary.setNumberOfCourses(trainingClassCourseList.size());
		int cnt = 0, cnt2 = 0;
		for (CourseTraineeActivityDTO cta : courseTraineeActivityList) {
			if (cta.getRating() != null) {
				cnt++;
			}
				if (cta.getCompletedFlag() > 0) {
					cnt2++;
				}

		}
		summary.setNumberOfRatings(cnt);
		summary.setNumberOfCompletedActivities(cnt2);

		if (summary.getNumberOfActivites() > 0) {
			Double dActivities = Double.valueOf(""
					+ summary.getNumberOfActivites());
			Double dCompleted = Double.valueOf(""
					+ summary.getNumberOfCompletedActivities());

			Double pc = (dCompleted / dActivities) * 100;
			summary.setPercCompleted(pc.doubleValue());
		}

		return summary;
	}

	public static final DecimalFormat df = new DecimalFormat("###,###,###.00");
	public static final DecimalFormat dfNoDecimal = new DecimalFormat(
			"###,###,###,###,###");
	private Summary summary;
	protected Integer courseID;
	protected int type;
	protected List<CourseTraineeActivityDTO> courseTraineeActivityList = new ArrayList<CourseTraineeActivityDTO>();;
	protected List<TrainingClassCourseDTO> trainingClassCourseList = new ArrayList<TrainingClassCourseDTO>();

	public static final int GET_COURSE_LIST = 1, GET_ACTIVITY_BY_COURSE = 2,
			GET_ALL_ACTIVITIES = 3, GET_ALL_DATA = 4;
	// gesture detection
	/**
	 * Gesture detection facility for all CM activity classes
	 */
	protected GestureDetector gestureDetector = new GestureDetector(ctx,
			new OnGestureListener() {

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					Log.d(LOG, "onSingleTap fired");
					return false;
				}

				@Override
				public void onShowPress(MotionEvent e) {
					Log.d(LOG, "onShowPress fired");

				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					Log.d(LOG, "onScroll fired distanceX: " + distanceX
							+ " distanceY: " + distanceY);
					if (distanceX > 0) {
						Log.d(LOG, "onScroll - swiped LEFT");
					} else {
						Log.d(LOG, "onScroll - swiped RIGHT");
					}
					return false;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					Log.d(LOG, "onLongPress fired");

				}

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					Log.d(LOG, "onFling fired");

					return false;
				}

				@Override
				public boolean onDown(MotionEvent e) {
					Log.d(LOG, "onDown fired");
					return false;
				}
			});

	static final String LOG = "BaseCM";
}
