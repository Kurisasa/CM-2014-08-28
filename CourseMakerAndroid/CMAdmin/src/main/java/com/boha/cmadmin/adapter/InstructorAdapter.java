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
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.util.Statics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class InstructorAdapter extends ArrayAdapter<InstructorDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<InstructorDTO> mList;
	private Context ctx;


	public InstructorAdapter(Context context, int textViewResourceId,
			List<InstructorDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	static class ViewHolderItem {
		TextView txtName;
		TextView txtEmail, txtCity, txtCount;
		ImageView image;
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
					.findViewById(R.id.TR_ITEM_classCount);

			v.image = (ImageView) convertView
					.findViewById(R.id.TR_ITEM_image);
			convertView.setTag(v);
		} else {
			v = (ViewHolderItem) convertView.getTag();
		}

		InstructorDTO instructor = mList.get(position);
		v.txtName.setText(instructor.getFirstName() + " "
				+ instructor.getLastName());

		v.txtEmail.setText(instructor.getEmail());

		if (instructor.getCityName() != null) {
			v.txtCity.setText(instructor.getCityName());
		}

		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(instructor.getCompanyID()).append("/instructor/");
		sb.append(instructor.getInstructorID()).append(".jpg");
		//v.image.setDefaultImageResId(R.drawable.boy);
		//v.image.setImageUrl(sb.toString(), imageLoader);
        ImageLoader.getInstance().displayImage(sb.toString(), v.image, options);

		if (instructor.getInstructorClassList() == null
				|| instructor.getInstructorClassList().isEmpty()) {
			v.txtCount.setText("00");
		} else {
			if (instructor.getInstructorClassList().size() < 10) {
				v.txtCount.setText("0"
						+ instructor.getInstructorClassList().size());
			} else {
				v.txtCount.setText(""
						+ instructor.getInstructorClassList().size());
			}
		}

		Statics.setRobotoFontRegular(ctx, v.txtName);

		animateText(v.txtCount);

		return (convertView);
	}

    private void animateText(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(100);
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
    public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(com.boha.cmlibrary.R.drawable.ic_action_add_person) // resource or drawable
            .showImageForEmptyUri(com.boha.cmlibrary.R.drawable.boy) // resource or drawable
            .showImageOnFail(com.boha.cmlibrary.R.drawable.boy) // resource or drawable
            .resetViewBeforeLoading(false)  // default
            .cacheInMemory(true) // default
            .cacheOnDisk(true) // default
            .build();

}
