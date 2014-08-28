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
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.InstructorRatingDTO;
import com.boha.coursemaker.dto.TraineeRatingDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends ArrayAdapter<CourseTraineeActivityDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<CourseTraineeActivityDTO> mList;
	private Context ctx;
	private boolean hideDescription;

	public ActivityAdapter(Context context, int textViewResourceId,
			List<CourseTraineeActivityDTO> list, boolean hideDescription) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.hideDescription = hideDescription;
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

		TextView name = (TextView) view.findViewById(R.id.AR_txtActName);
		TextView desc = (TextView) view.findViewById(R.id.AR_txtActDesc);
		TextView priority = (TextView) view.findViewById(R.id.AR_txtPriority);
		TextView cb = (TextView) view.findViewById(R.id.AR_completeMsg);
		TextView date = (TextView) view.findViewById(R.id.AR_completionDate);
		TextView traineeRating = (TextView) view
				.findViewById(R.id.AR_txtAvgTraineeRating);
		TextView insRating = (TextView) view
				.findViewById(R.id.AR_txtAvgInsRating);

		final CourseTraineeActivityDTO p = mList.get(position);

		name.setText(p.getActivity().getActivityName());
		//nameGreen.setText(p.getActivity().getActivityName());
		if (hideDescription) {
			desc.setVisibility(View.GONE);
		} else {
			desc.setText(p.getActivity().getDescription());
			desc.setVisibility(View.VISIBLE);
		}
		if (p.getActivity().getDescription() == null ||p.getActivity().getDescription().isEmpty()) {
			desc.setVisibility(View.GONE);
		}
		if (p.getCompletedFlag() > 0) {
			cb.setText(ctx.getResources().getString(R.string.task_complete));
			cb.setTextColor(ctx.getResources().getColor(R.color.green_two));
			
			priority.setVisibility(View.GONE);
			name.setVisibility(View.GONE);
			
			if (p.getCompletionDate() > 0) {
				Date d = new Date(p.getCompletionDate());
				date.setText(sdf.format(d));
				date.setVisibility(View.VISIBLE);
				date.setTextColor(ctx.getResources()
						.getColor(R.color.green_two));
			} else {
				date.setVisibility(View.GONE);
			}
			Statics.setRobotoFontBold(ctx, cb);
		} else {
			cb.setText(ctx.getResources().getString(R.string.task_incomplete));
			cb.setTextColor(ctx.getResources()
					.getColor(R.color.translucent_red));
			//priorityGreen.setVisibility(View.GONE);
			//nameGreen.setVisibility(View.GONE);
			priority.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			
			date.setVisibility(View.GONE);
			Statics.setRobotoItalic(ctx, cb);
		}

		Statics.setRobotoFontBold(ctx, name);
		Statics.setRobotoFontBold(ctx, priority);
		Statics.setRobotoFontRegular(ctx, desc);
		if (position < 10) {
			priority.setText("0" + (position + 1));
			//priorityGreen.setText("0" + (position + 1));
		} else {
			priority.setText((position + 1));
			//priorityGreen.setText((position + 1));
		}
		if (p.getInstructorRatingList() != null) {
			double total = 0;
			for (InstructorRatingDTO ir : p.getInstructorRatingList()) {
				total += ir.getRating().getRatingNumber();
			}
			if (p.getInstructorRatingList().size() > 0) {
				Double d = Double.valueOf(total);
				Double a = d
						/ Double.valueOf(p.getInstructorRatingList().size());
				insRating.setText(df2.format(a.doubleValue()));
			} else {
				insRating.setText(df2.format(0));
			}
		}
		if (p.getTraineeRatingList() != null) {
			double total = 0;
			for (TraineeRatingDTO ir : p.getTraineeRatingList()) {
				total += ir.getRating().getRatingNumber();
			}
			if (p.getTraineeRatingList().size() > 0) {
				Double d = Double.valueOf(total);
				Double a = d / Double.valueOf(p.getTraineeRatingList().size());
				traineeRating.setText(df2.format(a.doubleValue()));
			} else {
				traineeRating.setText(df2.format(0));
			}
		}
		animateView(view);
		return (view);
	}

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(500);

		view.startAnimation(a);
	}

	static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###.00");
	private static final Locale locale = Locale.getDefault();
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE, dd MMMM yyyy HH:mm", locale);
	public static final int SLIDE_IN_LEFT = 1, SLIDE_OUT_RIGHT = 2,
			PUSH_UP = 3, PUSH_DOWN = 4;
}
