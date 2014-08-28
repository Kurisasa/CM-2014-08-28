package com.boha.cmlibrary.listeners;

import com.boha.coursemaker.dto.CourseTraineeActivityDTO;


public interface RatingListener {

	public void onRatingCompleted(CourseTraineeActivityDTO courseTraineeActivity);
	public void onCancelRating();
}
