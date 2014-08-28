package com.boha.cmlibrary;

import com.boha.coursemaker.dto.CourseTraineeActivityDTO;

public interface ActivityListListener {

	public void onActivityCompleted(CourseTraineeActivityDTO cta);
    public void onRatingRequested(CourseTraineeActivityDTO cta, int type);

}
