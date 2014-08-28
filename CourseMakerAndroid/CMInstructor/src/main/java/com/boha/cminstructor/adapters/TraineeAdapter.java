package com.boha.cminstructor.adapters;

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
import com.boha.cminstructor.R;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		TextView txtName, txtNumber;
		TextView txtTotalTasks, txtTotalCompleted, txtPerc, txtDate,
				txtInsRating, txtTraineeRating;
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
			v.txtTotalCompleted = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtTotalComplete);
			v.txtTotalTasks = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtTotalTasks);
			v.txtPerc = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtPerc);
            v.txtNumber = (TextView) convertView
                    .findViewById(R.id.TR_ITEM_txtNumber);
			v.txtDate = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtDate);
			v.txtInsRating = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtInsAvgRating);
			v.txtTraineeRating = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtTraineeAvgRating);

			v.image = (NetworkImageView) convertView
					.findViewById(R.id.TR_ITEM_image);
			convertView.setTag(v);
		} else {
			v = (ViewHolderItem) convertView.getTag();
		}

		TraineeDTO trainee = mList.get(position);
		v.txtName.setText(trainee.getFullName());
        v.txtNumber.setText("" + (position + 1));

		v.txtTotalTasks
				.setText(df1.format(trainee.getTotalTasks()));
		v.txtTotalCompleted.setText(df1.format(trainee
				.getTotalCompleted()));
		if (trainee.getPercComplete() == 0) {
			v.txtPerc.setText("0.0%");
		} else {
			v.txtPerc
					.setText(df2.format(trainee.getPercComplete()) + "%");
		}
		v.txtTraineeRating.setText(df2.format(trainee
				.getAverageTraineeRating()));
		v.txtInsRating.setText(df2.format(trainee
				.getAverageInstructorRating()));

		if (trainee.getLastDate() > 0)
			v.txtDate.setText(sdf.format(new Date(trainee
					.getLastDate())));
		else
			v.txtDate.setText("");
		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(trainee.getCompanyID()).append("/trainee/");
		sb.append(trainee.getTraineeID()).append(".jpg");
		v.image.setDefaultImageResId(R.drawable.boy);
		v.image.setImageUrl(sb.toString(), imageLoader);

		Statics.setRobotoFontBold(ctx, v.txtName);
		Statics.setRobotoFontRegular(ctx, v.txtDate);

		animateView(convertView);

		return (convertView);
	}

	static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###.0");
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE, dd MMMM yyyy HH:mm", loc);

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
		a.setDuration(500);
		if (view == null)
			return;
		view.startAnimation(a);
	}

}
