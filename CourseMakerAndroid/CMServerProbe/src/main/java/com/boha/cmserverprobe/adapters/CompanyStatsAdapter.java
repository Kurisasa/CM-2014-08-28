package com.boha.cmserverprobe.adapters;

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

import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.CompanyStatsDTO;

public class CompanyStatsAdapter extends ArrayAdapter<CompanyStatsDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<CompanyStatsDTO> mList;
	private Context ctx;

	public CompanyStatsAdapter(Context context, int textViewResourceId,
			List<CompanyStatsDTO> list) {
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
		//		.findViewById(R.id.COM_image);
		TextView name = (TextView) view
				.findViewById(R.id.COM_companyName);
		TextView admins = (TextView) view
				.findViewById(R.id.COM_admins);
		TextView date = (TextView) view
				.findViewById(R.id.COM_date);
		TextView authors = (TextView) view
				.findViewById(R.id.COM_authors);
		TextView instr = (TextView) view
				.findViewById(R.id.COM_instructors);
		TextView trainees = (TextView) view
				.findViewById(R.id.COM_trainees);

		final CompanyStatsDTO p = mList.get(position);
		
		

		name.setText(p.getCompanyName());
		authors.setText(df.format(p.getNumberOfAuthors()));
		admins.setText(df.format(p.getNumberOfAdmins()));
		instr.setText(df.format(p.getNumberOfInstructors()));
		trainees.setText(df.format(p.getNumberOfTrainees()));
		date.setText(sdf.format(new Date(p.getDateRegistered())));
		animateView(view);


		return (view);
	}
	private void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);			
		view.startAnimation(a);
	}
	static final DecimalFormat df = new DecimalFormat("###,###,###,###,###");
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("EE dd-MM-yyyy HH:mm:ss", loc);
}
