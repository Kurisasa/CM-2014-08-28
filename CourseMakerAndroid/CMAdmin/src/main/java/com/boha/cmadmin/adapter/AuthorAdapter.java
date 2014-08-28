package com.boha.cmadmin.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class AuthorAdapter extends ArrayAdapter<AuthorDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<AuthorDTO> mList;
	private Context ctx;

	private ImageLoader imageLoader;

	public AuthorAdapter(Context context, int textViewResourceId,
			List<AuthorDTO> list, ImageLoader imageLoader) {
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
		ViewHolderItem v;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutRes, null);
			v = new ViewHolderItem();
			v.txtName = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtName);
			v.txtEmail = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtEmail);

			v.txtCity = (TextView) convertView
					.findViewById(R.id.TR_ITEM_txtCity);
			v.txtCount = (TextView) convertView
					.findViewById(R.id.TR_ITEM_courseCount);

			v.image = (NetworkImageView) convertView
					.findViewById(R.id.TR_ITEM_image);
			convertView.setTag(v);
		} else {
			v = (ViewHolderItem) convertView.getTag();
		}

		AuthorDTO author = mList.get(position);
		v.txtName.setText(author.getFirstName() + " "
				+ author.getLastName());

		v.txtEmail.setText(author.getEmail());

			v.txtCity.setText("");
		if (author.getCourseList() != null) {
			if (author.getCourseList().size() < 10) {
				v.txtCount.setText("0" + author.getCourseList().size());
			} else {
				v.txtCount.setText("" + author.getCourseList().size());
			}
		}
		

		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(author.getCompanyID()).append("/author/");
		sb.append(author.getAuthorID()).append(".jpg");
		v.image.setDefaultImageResId(R.drawable.boy);
		v.image.setImageUrl(sb.toString(), imageLoader);

		Statics.setRobotoFontRegular(ctx, v.txtName);

		animateText(v.txtCount);

		return (convertView);
	}
    private void animateText(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(200);
        an.setRepeatMode(ValueAnimator.REVERSE);
        an.start();
    }
	
	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
		a.setDuration(1000);
		if (view == null)
			return;
		view.startAnimation(a);
	}

}
