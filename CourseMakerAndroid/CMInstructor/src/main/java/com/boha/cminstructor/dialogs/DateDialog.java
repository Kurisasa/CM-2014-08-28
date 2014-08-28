package com.boha.cminstructor.dialogs;

import com.boha.cminstructor.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;

public class DateDialog extends DialogFragment {
	public interface DateDialogListener {
		public void onDateSet(int year, int month, int day);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.dialog_date,
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
		datePicker = (DatePicker)view.findViewById(R.id.DATE_datePicker);
		btnSetDate = (Button)view.findViewById(R.id.DATE_btnSetDate);
		btnCancel = (Button)view.findViewById(R.id.DATE_btnCancel);
		btnSetDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				listener.onDateSet(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
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
	DatePicker datePicker;
	DateDialogListener listener;
	Button btnSetDate, btnCancel;
	public DateDialogListener getListener() {
		return listener;
	}
	public void setListener(DateDialogListener listener) {
		this.listener = listener;
	}
	
}
