package com.boha.cmadmin.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.coursemaker.dto.RatingDTO;
import com.boha.coursemaker.util.Statics;

public class RatingAdapter extends ArrayAdapter<RatingDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<RatingDTO> mList;
	private Context ctx;

	public RatingAdapter(Context context, int textViewResourceId,
			List<RatingDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	static class ViewHolderItem {
		TextView txtName, txtNumber;
		ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolderItem;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutRes, null);
			viewHolderItem = new ViewHolderItem();
			viewHolderItem.txtName = (TextView) convertView
					.findViewById(R.id.LK_ITEM_txtName);
			viewHolderItem.txtNumber = (TextView) convertView
					.findViewById(R.id.LK_ITEM_txtNumber);

			viewHolderItem.image = (ImageView) convertView
					.findViewById(R.id.LK_ITEM_image);
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}

		RatingDTO r = mList.get(position);
		viewHolderItem.txtName.setText(r.getRatingName());
		viewHolderItem.txtNumber.setText(df.format(r.getRatingNumber()));

		Statics.setRobotoFontBold(ctx, viewHolderItem.txtName);
		animateView(convertView);

		return (convertView);
	}

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		if (view == null)
			return;
		view.startAnimation(a);
	}

	static final DecimalFormat df = new DecimalFormat("###,###");
}
