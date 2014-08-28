package com.boha.cminstructor.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.boha.cminstructor.R;
import com.boha.coursemaker.dto.TrainingClassEventDTO;
import com.boha.coursemaker.util.Statics;

public class EventAdapter extends ArrayAdapter<TrainingClassEventDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TrainingClassEventDTO> mList;
	private Context ctx;

	public EventAdapter(Context context, int textViewResourceId,
			List<TrainingClassEventDTO> list) {
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

		
		TextView title = (TextView) view.findViewById(R.id.ET_txtTitle);
		TextView start = (TextView) view.findViewById(R.id.ET_txtStart);
		TextView end = (TextView) view.findViewById(R.id.ET_txtEnd);
		TextView num = (TextView) view.findViewById(R.id.ET_txtNumber);
		TextView summary = (TextView) view.findViewById(R.id.ET_txtSummary);

		final TrainingClassEventDTO p = mList.get(position);
		title.setText(p.getEventName());
		summary.setText(p.getDescription());
		start.setText(sdf.format(new Date(p.getStartDate())));
		end.setText(sdf.format(new Date(p.getEndDate())));
		int pos = position + 1;
		if (pos < 10) {
			num.setText("0" + pos);
		} else {
			num.setText("" + pos);
		}
		
		int x = position % 2;
		if (x == 0) {
			view.setBackgroundColor(getContext().getResources().getColor(
					R.color.beige_pale));
		} else {
			view.setBackgroundColor(getContext().getResources().getColor(
					R.color.white));
		
		}
		Statics.setRobotoFontBold(ctx, title);
		Statics.setRobotoFontBold(ctx, start);
		Statics.setRobotoFontBold(ctx, end);
		
		animateView(view);
		return (view);
	}

	@SuppressWarnings("unused")
	private int isWeekColor(long date) {		
		Calendar c = GregorianCalendar.getInstance();
		Calendar c2 = GregorianCalendar.getInstance();
		c2.setTimeInMillis(date);
		
		int wk = c.get(Calendar.WEEK_OF_YEAR);
		int wk2 = c2.get(Calendar.WEEK_OF_YEAR);
		if (wk == wk2) {
			return RED;
		}
		if (wk > wk2) {
			return GRAY;
		}
		if (wk < wk2) {
			return GREEN;
		}
		
		return GRAY;
	}
	
	static final int GRAY = 1, GREEN = 2, RED = 3;
	static final long DAY = 1000 * 60 * 60 * 24;
	public void animateView(final View view) {
		Animation a  = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);		
		view.startAnimation(a);
	}

	private static final Locale locale = Locale.getDefault();
	private static final SimpleDateFormat sdf = new  SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", locale);
	
}
