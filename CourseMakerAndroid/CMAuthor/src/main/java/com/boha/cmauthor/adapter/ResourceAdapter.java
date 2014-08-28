package com.boha.cmauthor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.boha.cmauthor.R;
import com.boha.cmauthor.misc.RowColor;
import com.boha.coursemaker.dto.LessonResourceDTO;

import java.util.List;

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
        TextView num = (TextView) view
                .findViewById(R.id.CRSITEM_image);
		
		final LessonResourceDTO p = mList.get(position);
		name.setText(p.getResourceName());
		url.setText(p.getUrl());
		if (position < 9) {
            num.setText("0" + (position + 1));
        } else {
            num.setText("" + (position + 1));
        }
		RowColor.setColor(view, position);
		animateView(view);
		
		return (view);
	}
	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in);
		a.setDuration(500);			
		view.startAnimation(a);
	}
	
}
