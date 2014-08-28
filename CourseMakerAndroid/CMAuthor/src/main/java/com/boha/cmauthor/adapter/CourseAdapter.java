package com.boha.cmauthor.adapter;

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

import com.boha.cmauthor.R;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<CourseDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<CourseDTO> mList;
	private Context ctx;
	private CourseDTO course;

	public CourseAdapter(Context context, int textViewResourceId,
			List<CourseDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.CRSITEM_name);
            v.txtDesc = (TextView) convertView
                    .findViewById(R.id.CRSITEM_desc);
            v.txtCount = (TextView) convertView
                    .findViewById(R.id.CRSITEM_count);
            v.txtPriority = (TextView) convertView
                    .findViewById(R.id.CRSITEM_priority);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


		course = mList.get(position);

        v.txtName.setText(course.getCourseName());
        v.txtDesc.setText(course.getDescription());
		Statics.setRobotoFontBold(ctx, v.txtName);
		Statics.setRobotoFontLight(ctx, v.txtDesc);
		if (course.getActivityList()
                != null) {
			if (course.getActivityList().size() < 10) {
                v.txtCount.setText("0" + course.getActivityList().size());
			} else {
				v.txtCount.setText("" + course.getActivityList().size());
			}
		}
		v.txtPriority.setText("" + course.getPriorityFlag());

		//RowColor.setColor(view, position);
        animatePriority(v.txtPriority);
		//animateView(view);



		return (convertView);
	}

    private void animatePriority(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(100);
        an.setRepeatMode(ValueAnimator.REVERSE);
       an.start();
    }
	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);
		view.startAnimation(a);
	}

    static class ViewHolderItem {
        TextView txtName, txtDesc;
        TextView txtPriority, txtCount;

    }


}
