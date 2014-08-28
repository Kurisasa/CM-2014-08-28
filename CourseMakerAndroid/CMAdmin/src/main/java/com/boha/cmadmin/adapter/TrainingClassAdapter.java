package com.boha.cmadmin.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.util.ZeroUtil;

public class TrainingClassAdapter extends ArrayAdapter<TrainingClassDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TrainingClassDTO> mList;
	private Context ctx;

	public TrainingClassAdapter(Context context, int textViewResourceId,
			List<TrainingClassDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;

		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(mLayoutRes, parent, false);

		if (convertView == null) {
			view = mInflater.inflate(mLayoutRes, parent, false);
		} else {
			view = convertView;
		}
		
		//ImageView logo = (ImageView) view
		//		.findViewById(R.id.TC_image);
		TextView className = (TextView) view
				.findViewById(R.id.TC_className);
		TextView startDate = (TextView) view
				.findViewById(R.id.TC_startDate);
		TextView endDate = (TextView) view
				.findViewById(R.id.TC_endDate);
		TextView admin = (TextView) view
				.findViewById(R.id.TC_admin);
		TextView courses = (TextView) view
				.findViewById(R.id.TC_coursesenrolled);
		TextView trainees = (TextView) view
				.findViewById(R.id.TC_traineesEnrolled);
		

		final TrainingClassDTO p = mList.get(position);

		className.setText(p.getTrainingClassName());
		admin.setText(p.getAdministrator().getFirstName() 
				+ " " + p.getAdministrator().getLastName());
		startDate.setText(sdf.format(new Date(p.getStartDate())));
		endDate.setText(sdf.format(new Date(p.getEndDate())));
		ZeroUtil.formatPadWithZero(courses, p.getTrainingClassCourseList().size());
		ZeroUtil.formatPadWithZero(trainees, p.getTraineeList().size());
		
		animateView(view);

		return (view);
	}
	private void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);			
		view.startAnimation(a);
	}
	@SuppressLint("SimpleDateFormat")
	static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
}
