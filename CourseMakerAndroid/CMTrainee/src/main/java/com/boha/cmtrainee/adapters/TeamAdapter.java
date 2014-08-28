package com.boha.cmtrainee.adapters;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.boha.cmtrainee.R;
import com.boha.coursemaker.dto.TeamDTO;
import com.boha.coursemaker.util.Statics;

public class TeamAdapter extends ArrayAdapter<TeamDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TeamDTO> mList;
	private Context ctx;

	public TeamAdapter(Context context, int textViewResourceId,
			List<TeamDTO> list) {
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

		TextView name = (TextView) view.findViewById(R.id.TI_name);
		TextView priority = (TextView) view.findViewById(R.id.TI_positionCounter);
		TextView txtCount = (TextView) view.findViewById(R.id.TI_count);
		TextView tClass = (TextView) view.findViewById(R.id.TI_class);
		TextView date = (TextView) view.findViewById(R.id.TI_date);
		final TeamDTO p = mList.get(position);
		if (p == null) {
			name.setText("Team name is null. Needs debugging");
			return view;
		} else {
			name.setText(p.getTeamName());
		}
		tClass.setText(p.getTrainingClassName());
		date.setText(sdf.format(new Date(p.getDateRegistered())));
		txtCount.setText(df1.format(p.getTeamMemberList().size()));

		Statics.setRobotoFontBold(ctx, name);
		Statics.setRobotoFontBold(ctx, tClass);
		Statics.setRobotoFontRegular(ctx, priority);
		if ((position + 1) < 10) {
			priority.setText("0" + (position + 1));
		} else {
			priority.setText("" + (position + 1));
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
