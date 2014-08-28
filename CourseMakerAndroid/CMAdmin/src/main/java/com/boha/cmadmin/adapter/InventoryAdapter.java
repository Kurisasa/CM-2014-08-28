package com.boha.cmadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.boha.cmadmin.R;
import com.boha.coursemaker.dto.InventoryDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InventoryAdapter extends ArrayAdapter<InventoryDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<InventoryDTO> mList;
	private Context ctx;

	public InventoryAdapter(Context context, int textViewResourceId,
			List<InventoryDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	static class ViewHolderItem {
		TextView txtMakeModel, txtSerial, txtYear;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolderItem;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutRes, null);
			viewHolderItem = new ViewHolderItem();
			viewHolderItem.txtMakeModel = (TextView) convertView
					.findViewById(R.id.INV_model);
			viewHolderItem.txtSerial = (TextView) convertView
					.findViewById(R.id.INV_serial);
			viewHolderItem.txtYear = (TextView) convertView
					.findViewById(R.id.INV_year);
			
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}

		InventoryDTO inventory = mList.get(position);
		viewHolderItem.txtMakeModel.setText(inventory.getModel());

		viewHolderItem.txtSerial
				.setText(inventory.getSerialNumber());
		if (inventory.getYearPurchased() != 0)
			viewHolderItem.txtYear.setText("" + inventory.getYearPurchased());
		else 
			viewHolderItem.txtYear.setText("" );
		Statics.setRobotoFontRegular(ctx, viewHolderItem.txtMakeModel);
		
//		int x = position % 2;
//		if (x > 0) {
//			convertView.setBackgroundColor(getContext().getResources().getColor(
//					R.color.blue_pale));
//		} else {
//			convertView.setBackgroundColor(getContext().getResources().getColor(
//					R.color.white));
//		}

		animateView(convertView);

		return (convertView);
	}

	static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###.00");
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE, dd MMMM yyyy HH:mm", loc);

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);
		if (view == null)
			return;
		view.startAnimation(a);
	}

}
