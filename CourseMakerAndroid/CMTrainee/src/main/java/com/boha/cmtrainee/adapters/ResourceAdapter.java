package com.boha.cmtrainee.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.boha.cmtrainee.R;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.util.Statics;

public class ResourceAdapter extends ArrayAdapter<LessonResourceDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<LessonResourceDTO> mList;
	private Context ctx;

	public ResourceAdapter(Context context, int textViewResourceId,
			List<LessonResourceDTO> list) {
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
		
		TextView name = (TextView) view
				.findViewById(R.id.CRSITEM_name);
		TextView url = (TextView) view
				.findViewById(R.id.CRSITEM_url);		
		
		final LessonResourceDTO p = mList.get(position);
		name.setText(p.getResourceName());
		url.setText(p.getUrl());
		Statics.setRobotoFontRegular(ctx, url);
		Statics.setRobotoFontBold(ctx, name);

		animateView(view);
		
		return (view);
	}
	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in_center);
		a.setDuration(500);			
		view.startAnimation(a);
	}
	
}
