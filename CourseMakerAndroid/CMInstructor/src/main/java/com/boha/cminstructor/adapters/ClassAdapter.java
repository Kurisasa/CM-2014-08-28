package com.boha.cminstructor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boha.cminstructor.R;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends ArrayAdapter<InstructorClassDTO> {

    public interface ClassListListener {
        public void onCalendarRequest(InstructorClassDTO ic);
        public void onRefreshRequest(InstructorClassDTO ic);
        public void onAssignCoursesRequest(InstructorClassDTO ic);
    }
	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<InstructorClassDTO> mList;
	private Context ctx;
    private ClassListListener classListListener;

	public ClassAdapter(Context context, int textViewResourceId,
			List<InstructorClassDTO> list, ClassListListener classListListener) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
        this.classListListener = classListListener;
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

		TextView className = (TextView) view.findViewById(R.id.AR_txtClassName);
		TextView start = (TextView) view.findViewById(R.id.AR_startDate);
		TextView compTot = (TextView) view.findViewById(R.id.AR_totalComplete);
		TextView end = (TextView) view.findViewById(R.id.AR_endDate);
		TextView taskTot = (TextView) view.findViewById(R.id.AR_totalTasks);
        TextView trTot = (TextView) view.findViewById(R.id.AR_totalTrainees);
		TextView perc = (TextView) view.findViewById(R.id.AR_percComplete);

        ImageView imgCal = (ImageView) view.findViewById(R.id.AR_imgCalendar);
        ImageView imgRefresh= (ImageView) view.findViewById(R.id.AR_imgRefresh);
        ImageView imgAssign= (ImageView) view.findViewById(R.id.AR_imgAssignCourses);
		

		final InstructorClassDTO ic = mList.get(position);
        imgCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classListListener.onCalendarRequest(ic);
            }
        });
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classListListener.onRefreshRequest(ic);
            }
        });
        imgAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classListListener.onAssignCoursesRequest(ic);
            }
        });

		className.setText(ic.getTrainingClassName());
		Date s = new Date(ic.getStartDate());
		Date e = new Date(ic.getEndDate());
		start.setText(sdf.format(s));
		end.setText(sdf.format(e));
		taskTot.setText(df1.format( ic.getTotalTasks()));
		compTot.setText(df1.format( ic.getTotalComplete()));
		perc.setText(df2.format(ic.getPercComplete()) + " %");
        trTot.setText(""+ic.getNumberOfTrainees());
		
		Statics.setRobotoFontRegular(ctx, className);
		Statics.setRobotoFontRegular(ctx, start);
		Statics.setRobotoFontRegular(ctx, end);
//		int x = position % 2;
//		if (x == 0) {
//			view.setBackgroundColor(getContext().getResources().getColor(
//					R.color.blue_pale));
//		} else {
//			view.setBackgroundColor(getContext().getResources().getColor(
//					R.color.white));
//
//		}
		
		
		animateView(view);

		return (view);
	}

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);
		view.startAnimation(a);
	}


	static final Locale locale = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", locale);
	static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###,##0.00");

    static class Holder {

    }
}
