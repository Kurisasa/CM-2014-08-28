package com.boha.coursemaker.util;

import android.widget.TextView;

import java.text.DecimalFormat;

public class ZeroUtil {

	public static void formatPadWithZero(TextView txt, int number) {
		if (number < 10) {
			txt.setText("0" + number);
		} else {
			txt.setText("" + number);
		}
		
	}
	public static void formatPadWithBlank(TextView txt, int number) {
		if (number < 10) {
			txt.setText(" " + number);
		} else {
			txt.setText("" + number);
		}
		
	}
	public static void formatNoDecimal(TextView txt, double number) {
		txt.setText(dfOne.format(number));
		
	}
	public static void formatDecimal(TextView txt, double number) {
		txt.setText(dfTwo.format(number));
		
	}
	
	
	public static final DecimalFormat dfOne = new DecimalFormat("###,###,###,###,###,###,###,###,###");
	public static final DecimalFormat dfTwo = new DecimalFormat("###,###,###,###,###,###,###,###,##0.00");
	public static final int NO_DECIMAL = 1, DECIMAL = 2;
}
