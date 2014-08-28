package com.boha.cmadmin.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmadmin.R;
import com.boha.coursemaker.dto.TraineeEquipmentDTO;
import com.boha.coursemaker.util.Statics;

public class TraineeEquipmentAdapter extends ArrayAdapter<TraineeEquipmentDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TraineeEquipmentDTO> mList;
	private Context ctx;
	private boolean showDevice;

	private ImageLoader imageLoader;

	public TraineeEquipmentAdapter(Context context, int textViewResourceId,
			List<TraineeEquipmentDTO> list, ImageLoader imageLoader, boolean showDevice) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		this.showDevice = showDevice;
		mList = list;
		ctx = context;
		this.imageLoader = imageLoader;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	static class ViewHolderItem {
		TextView txtName, txtStart, txtReturn, txtClass;
		TextView txtEmail, txtModel, txtSerial;
		NetworkImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolderItem;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutRes, null);
			viewHolderItem = new ViewHolderItem();
			viewHolderItem.txtName = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtName);
			viewHolderItem.txtStart = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtStartDate);
			viewHolderItem.txtReturn = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtEndDate);
			
			viewHolderItem.txtModel = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtDevice);
			viewHolderItem.txtSerial = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtSerial);
			viewHolderItem.txtClass = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtClassname);

			viewHolderItem.image = (NetworkImageView) convertView
					.findViewById(R.id.TR_ITEM_image);
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}

		TraineeEquipmentDTO te = mList.get(position);
		viewHolderItem.txtName.setText(te.getTrainee().getFullName());
		viewHolderItem.txtModel.setText(te.getInventory().getModel());
		viewHolderItem.txtSerial.setText(te.getInventory().getSerialNumber());

		viewHolderItem.txtClass.setText(te.getTrainee().getTrainingClassName());
		viewHolderItem.txtStart.setText(sdf.format(new Date(te.getDateRegistered())));
		if (te.getDateReturned() == 0) {
			viewHolderItem.txtReturn.setText(ctx.getResources().getString(R.string.booked_out));
			viewHolderItem.txtReturn.setTextColor(Color.RED);
		} else {
			viewHolderItem.txtReturn.setText(sdf.format(new Date(te.getDateReturned())));
			viewHolderItem.txtReturn.setTextColor(Color.BLACK);
		}
		if (showDevice) {
			viewHolderItem.txtModel.setText(te.getInventory().getModel());
			viewHolderItem.txtSerial.setText(te.getInventory().getSerialNumber());
		} else {
			viewHolderItem.txtModel.setVisibility(View.GONE);
			viewHolderItem.txtSerial.setVisibility(View.GONE);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(te.getTrainee().getCompanyID()).append("/trainee/");
		sb.append(te.getTrainee().getTraineeID()).append(".jpg");
		viewHolderItem.image.setDefaultImageResId(R.drawable.boy);
		viewHolderItem.image.setImageUrl(sb.toString(), imageLoader);

		Statics.setRobotoFontBold(ctx, viewHolderItem.txtName);

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
