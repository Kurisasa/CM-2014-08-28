package com.boha.cmauthor.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.cmauthor.R;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class AuthorActivityAdapter extends ArrayAdapter<ActivityDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<ActivityDTO> mList;
	private Vibrator vb;
	private Context ctx;
	private boolean hideDescription;

	public AuthorActivityAdapter(Context context, int textViewResourceId,
                                 List<ActivityDTO> list, boolean hideDescription) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.hideDescription = hideDescription;
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

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
            v.txtPriority = (TextView) convertView
                    .findViewById(R.id.CRSITEM_priority);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


		final int index = position;
		final ActivityDTO p = mList.get(position);

		v.txtName.setText(p.getActivityName());
		if (hideDescription) {
			v.txtDesc.setVisibility(View.GONE);
		} else {
            v.txtDesc.setText(p.getDescription());
            v.txtDesc.setVisibility(View.VISIBLE);
		}
		Statics.setRobotoFontLight(ctx, v.txtDesc);
		Statics.setRobotoFontBold(ctx, v.txtPriority);
		Statics.setRobotoFontBold(ctx, v.txtName);
        v.txtPriority.setText("" + p.getPriorityFlag());


		animatePriority(v.txtPriority);
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
		Animation a  = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in_center);
		a.setDuration(500);
			
		view.startAnimation(a);
	}

    static class ViewHolderItem {
        TextView txtName, txtDesc;
        TextView txtPriority;

    }
	public static final int SLIDE_IN_LEFT = 1, SLIDE_OUT_RIGHT = 2,
			PUSH_UP = 3, PUSH_DOWN = 4;
}
