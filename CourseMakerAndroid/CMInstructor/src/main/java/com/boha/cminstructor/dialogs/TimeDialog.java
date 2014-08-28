package com.boha.cminstructor.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TimePicker;

import com.boha.cminstructor.R;

public class TimeDialog extends DialogFragment {
	public interface TimeDialogListener {
		public void onTimeSet(int hour, int minute);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.dialog_time,
				container);
		ctx = getActivity();
		setFields();		
		getDialog().setTitle(ctx.getResources().getString(R.string.back));

		Animation an = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
		an.setDuration(1000);
		view.startAnimation(an);
		
		return view;
	}
	private void setFields() {
		timePicker = (TimePicker)view.findViewById(R.id.DATE_timePicker);
		timePicker.setIs24HourView(true);
		
		btnSetTime = (Button)view.findViewById(R.id.DATE_btnSetTime);
		btnCancel = (Button)view.findViewById(R.id.DATE_btnCancel);
		btnSetTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				listener.onTimeSet(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
				dismiss();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	View view;
	Context ctx;
	TimePicker timePicker;
	TimeDialogListener listener;
	Button btnSetTime, btnCancel;
	public TimeDialogListener getListener() {
		return listener;
	}
	public void setListener(TimeDialogListener listener) {
		this.listener = listener;
	}
	
}
