package com.boha.cmadmin.adapter;

import android.content.Context;
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
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TraineeAdapter extends ArrayAdapter<TraineeDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TraineeDTO> mList;
	private Context ctx;

	private ImageLoader imageLoader;

	public TraineeAdapter(Context context, int textViewResourceId,
			List<TraineeDTO> list, ImageLoader imageLoader) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.imageLoader = imageLoader;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	static class ViewHolderItem {
		TextView txtName, txtClass, txtNumber;
		TextView txtCity;
		NetworkImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem v;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutRes, null);
			v = new ViewHolderItem();
			v.txtName = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtName);

			v.txtCity = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtCity);
			v.txtClass = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtClass);
			v.txtNumber = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtNumber);

			v.image = (NetworkImageView) convertView
					.findViewById(R.id.TR_ITEM_image);
			convertView.setTag(v);
		} else {
			v = (ViewHolderItem) convertView.getTag();
		}

		TraineeDTO trainee = mList.get(position);
		v.txtName.setText(trainee.getFullName());
		if (position < 9) {
			v.txtNumber.setText("0" + (position + 1));
		} else {
			v.txtNumber.setText("" + (position + 1));
		}


		v.txtCity.setText("" + trainee.getCityName());
		v.txtClass.setText("" + trainee.getTrainingClassName());

		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(trainee.getCompanyID()).append("/trainee/");
		sb.append(trainee.getTraineeID()).append(".jpg");
		v.image.setDefaultImageResId(R.drawable.boy);
		v.image.setImageUrl(sb.toString(), imageLoader);

		Statics.setRobotoFontRegular(ctx, v.txtName);

		animateView(convertView);

		return (convertView);
	}

	static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###.00");
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE, dd MMMM yyyy HH:mm", loc);

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		if (view == null)
			return;
		view.startAnimation(a);
	}

}
