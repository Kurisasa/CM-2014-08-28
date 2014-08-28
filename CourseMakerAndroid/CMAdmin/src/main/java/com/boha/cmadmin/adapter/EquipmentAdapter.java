package com.boha.cmadmin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.coursemaker.dto.EquipmentDTO;

public class EquipmentAdapter extends ArrayAdapter<EquipmentDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<EquipmentDTO> mList;
	private Context ctx;

	public EquipmentAdapter(Context context, int textViewResourceId,
			List<EquipmentDTO> list) {
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
		TextView equipment = (TextView) view
				.findViewById(R.id.EQP_name);
		TextView items = (TextView) view
				.findViewById(R.id.EQP_count);
		
		

		final EquipmentDTO p = mList.get(position);

		equipment.setText(p.getEquipmentName());
		if (p.getInventoryList() == null || p.getInventoryList().size() == 0) {
			items.setText("00");
		} else {
			if (p.getInventoryList().size() < 10) {
				items.setText("0" + p.getInventoryList().size());
			} else {
				items.setText("" + p.getInventoryList().size());
			}
		}
		
		animateView(view);

		int x = position % 2;
		if (x > 0) {
			view.setBackgroundColor(getContext().getResources().getColor(
					R.color.grey_light));
		} else {
			view.setBackgroundColor(getContext().getResources().getColor(
					R.color.white));
		}
		

		return (view);
	}
	private void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);			
		view.startAnimation(a);
	}
	
}
