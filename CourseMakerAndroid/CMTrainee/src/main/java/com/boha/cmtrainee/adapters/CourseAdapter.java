package com.boha.cmtrainee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.boha.cmtrainee.R;
import com.boha.cmtrainee.misc.RowColor;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<TrainingClassCourseDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TrainingClassCourseDTO> mList;
	private Context ctx;
	private TrainingClassCourseDTO course;

	public CourseAdapter(Context context, int textViewResourceId,
			List<TrainingClassCourseDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		view = mInflater.inflate(mLayoutRes, parent, false);

		if (convertView == null) {
			view = mInflater.inflate(mLayoutRes, parent, false);
		} else {
			view = convertView;
		}

		TextView name = (TextView) view.findViewById(R.id.CRSITEM_name);
		TextView desc = (TextView) view.findViewById(R.id.CRSITEM_desc);
		TextView cnt = (TextView) view.findViewById(R.id.CRSITEM_count);
		TextView pr = (TextView) view.findViewById(R.id.CRSITEM_priority);

		course = mList.get(position);

		name.setText(course.getCourse().getCourseName());
		desc.setText(course.getCourse().getDescription());
		if (course.getCourseTraineeActivityList().size() < 10) {
			cnt.setText("0" + course.getCourseTraineeActivityList().size());
		} else {
			cnt.setText("" + course.getCourseTraineeActivityList().size());
		}
		Statics.setRobotoFontRegular(ctx, name);
		Statics.setRobotoFontRegular(ctx, desc);

		if (course.getPriorityFlag() > 0) {
			if (course.getPriorityFlag() > 9) {
				pr.setText("" + (course.getPriorityFlag()));
			} else {
				pr.setText("0" + (course.getPriorityFlag()));
			}
		} else {
			if (position < 10) {
				pr.setText("0" + (position + 1));
			} else {
				pr.setText("" + (position + 1));
			}
		}

		RowColor.setColor(view, position);
		animateView(view);

		return (view);
	}

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(500);
		view.startAnimation(a);
	}

}
