package com.boha.cmauthor.adapter;

import java.util.List;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.cmauthor.R;
import com.boha.cmauthor.interfaces.UpDownArrrowListener;
import com.boha.cmauthor.misc.RowColor;
import com.boha.coursemaker.dto.ObjectiveDTO;
import com.boha.coursemaker.util.Statics;

public class ObjectiveAdapter extends ArrayAdapter<ObjectiveDTO> {

	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	List<ObjectiveDTO> mList;
	UpDownArrrowListener listener;
	Vibrator vb;
	private Context ctx;

	public ObjectiveAdapter(Context context, int textViewResourceId,
			List<ObjectiveDTO> list, UpDownArrrowListener listener) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.listener = listener;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vb = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(mLayoutRes, parent, false);

		if (convertView == null) {
			view = mInflater.inflate(mLayoutRes, parent, false);
		} else {
			view = convertView;
		}

		
		TextView name = (TextView) view.findViewById(R.id.CRSITEM_name);
		TextView desc = (TextView) view.findViewById(R.id.CRSITEM_desc);
		TextView priority = (TextView) view.findViewById(R.id.CRSITEM_priority);
		ImageView btnDown = (ImageView) view.findViewById(R.id.CRSITEM_btnDown);
		ImageView btnUp = (ImageView) view.findViewById(R.id.CRSITEM_btnUo);

		final ObjectiveDTO p = mList.get(position);
		final int index = position;
		name.setText(p.getObjectiveName());
		desc.setText(p.getDescription());
		Statics.setRobotoFontRegular(ctx, desc);
		Statics.setRobotoFontBold(ctx, name);
		if (position > 9) {
			priority.setText("" + (position + 1));
		} else {
			priority.setText("0" + (position + 1));
		}
		btnDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vb.vibrate(100);
				listener.onDownArrowTapped(index);
			}
		});
		btnUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vb.vibrate(100);
				listener.onUpArrowTapped(index);
			}
		});
		
		RowColor.setColor(view, position);
		animateView(view);

		return (view);
	}

	public void animateView(final View view) {
		Animation a = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in);
		a.setDuration(500);			
		view.startAnimation(a);
	}

}
