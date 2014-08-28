package com.boha.cminstructor.dialogs;

import java.util.Calendar;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

public class DateDialogFragment extends DialogFragment {
        
	public interface DateDialogFragmentListener {
		public void onDateChanged(int year, int month, int day);
	}
        public static String TAG = "DateDialogFragment";
        static Context mContext; 
        static int mYear;
        static int mMonth;
        static int mDay;
        static DateDialogFragmentListener mListener;
        
        
        public static DateDialogFragment newInstance(Context context, DateDialogFragmentListener listener, Calendar now) {
                DateDialogFragment dialog = new DateDialogFragment();
                mContext = context;
                mListener = listener;
                
                mYear = now.get(Calendar.YEAR);
                mMonth = now.get(Calendar.MONTH);
                mDay = now.get(Calendar.DAY_OF_MONTH);
                /*I dont really see the purpose of the below*/
                Bundle args = new Bundle();
                args.putString("title", "Set Date");
                dialog.setArguments(args);//setArguments can only be called before fragment is attached to an activity, so right after the fragment is created
                
                
                return dialog;
        }
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
                return new DatePickerDialog(mContext, mDateSetListener, mYear, mMonth, mDay);
        }
        
        
        private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        
                        mListener.onDateChanged(year, monthOfYear, dayOfMonth);
                }
        };
        
        
        
        

}
