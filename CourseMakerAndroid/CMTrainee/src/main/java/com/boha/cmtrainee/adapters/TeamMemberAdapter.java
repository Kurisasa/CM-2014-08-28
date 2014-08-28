package com.boha.cmtrainee.adapters;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmtrainee.R;
import com.boha.coursemaker.dto.TeamMemberDTO;
import com.boha.coursemaker.util.Statics;

public class TeamMemberAdapter extends ArrayAdapter<TeamMemberDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TeamMemberDTO> mList;
	private Context ctx;

	private ImageLoader imageLoader;

	public TeamMemberAdapter(Context context, int textViewResourceId,
			List<TeamMemberDTO> list, ImageLoader imageLoader) {
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
		TextView txtName, txtCell;
		TextView txtEmail;
		NetworkImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolderItem;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutRes, null);
			viewHolderItem = new ViewHolderItem();
			viewHolderItem.txtName = (TextView) convertView
					.findViewById(R.id.MEM_txtName);
			viewHolderItem.txtEmail = (TextView) convertView
					.findViewById(R.id.MEM_txtEmail);
			viewHolderItem.txtCell = (TextView) convertView
					.findViewById(R.id.MEM_txtCell);			
			viewHolderItem.image = (NetworkImageView) convertView
					.findViewById(R.id.MEM_image);
			convertView.setTag(viewHolderItem);
		} else {
			viewHolderItem = (ViewHolderItem) convertView.getTag();
		}

		TeamMemberDTO tm = mList.get(position);
		viewHolderItem.txtName.setText(tm.getFirstName() + " " + tm.getLastName());
		viewHolderItem.txtCell.setText(tm.getCellphone());		
		viewHolderItem.txtEmail
				.setText(tm.getEmail());
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(tm.getCompanyID()).append("/trainee/");
		sb.append(tm.getTraineeID()).append(".jpg");
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
