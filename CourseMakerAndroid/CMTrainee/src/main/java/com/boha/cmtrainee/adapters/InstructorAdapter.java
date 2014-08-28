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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmtrainee.R;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.util.Statics;

public class InstructorAdapter extends ArrayAdapter<InstructorDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<InstructorDTO> mList;
	private Context ctx;

	private ImageLoader imageLoader;

	public InstructorAdapter(Context context, int textViewResourceId,
			List<InstructorDTO> list, ImageLoader imageLoader) {
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
		TextView txtName;
		TextView txtEmail, txtCity, txtCount;
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
			viewHolderItem.txtEmail = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtEmail);

			viewHolderItem.txtCity = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtCity);
			viewHolderItem.txtCount = (TextView) convertView
					.findViewById(R.id.TR_ITEM_classCount);

			viewHolderItem.image = (NetworkImageView) convertView
					.findViewById(R.id.TR_ITEM_image);
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}

		InstructorDTO instructor = mList.get(position);
		viewHolderItem.txtName.setText(instructor.getFirstName() + " "
				+ instructor.getLastName());

		viewHolderItem.txtEmail.setText(instructor.getEmail());

		if (instructor.getCityName() != null) {
			viewHolderItem.txtCity.setText(instructor.getCityName());
		}

		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(instructor.getCompanyID()).append("/instructor/");
		sb.append(instructor.getInstructorID()).append(".jpg");
		viewHolderItem.image.setDefaultImageResId(R.drawable.boy);
		viewHolderItem.image.setImageUrl(sb.toString(), imageLoader);

		if (instructor.getInstructorClassList() == null
				|| instructor.getInstructorClassList().isEmpty()) {
			viewHolderItem.txtCount.setText("00");
		} else {
			if (instructor.getInstructorClassList().size() < 10) {
				viewHolderItem.txtCount.setText("0"
						+ instructor.getInstructorClassList().size());
			} else {
				viewHolderItem.txtCount.setText(""
						+ instructor.getInstructorClassList().size());
			}
		}

		Statics.setRobotoFontBold(ctx, viewHolderItem.txtName);

		animateView(convertView);

		return (convertView);
	}

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
		a.setDuration(1000);
		if (view == null)
			return;
		view.startAnimation(a);
	}

}
