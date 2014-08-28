package com.boha.coursemaker.listeners;

import android.graphics.Bitmap;


public interface DiskImageListener {

	public void onBitmapReturned(Bitmap bitmap);
	public void onError();
}
