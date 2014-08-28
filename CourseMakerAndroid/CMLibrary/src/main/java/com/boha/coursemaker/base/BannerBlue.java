package com.boha.coursemaker.base;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.boha.cmlibrary.R;


public class BannerBlue extends View {

	  private final Drawable logo;

	public BannerBlue(Context context, int id) {
	    super(context);
	    logo = context.getResources().getDrawable(R.drawable.banner_blue2);
	    setBackgroundDrawable(logo);
	  }

	public BannerBlue(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    logo = context.getResources().getDrawable(R.drawable.banner_blue2);
	    setBackgroundDrawable(logo);
	  }

	public BannerBlue(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    logo = context.getResources().getDrawable(R.drawable.banner_blue2);
	    setBackgroundDrawable(logo);
	  }

	  @Override protected void onMeasure(int widthMeasureSpec,
	      int heightMeasureSpec) {
	    int width = MeasureSpec.getSize(widthMeasureSpec);
	    int height = width * logo.getIntrinsicHeight() / logo.getIntrinsicWidth();
	    setMeasuredDimension(width, height);
	  }
	}