package com.boha.coursemaker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import com.boha.coursemaker.listeners.BitmapListener;

import java.io.File;

public class ImageTask {

	static Bitmaps bitmaps;
	static Uri u;
	static Context ctx;
	static BitmapListener bitmapListener;
	
	public static void getResizedBitmaps(Uri uri, Context context,BitmapListener listener) {
		ctx = context;
		bitmapListener = listener;
		u = uri;
		new DTask().execute();
	}
	
	static class DTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				Bitmap bm = ImageUtil.getBitmapFromUri(ctx, u);
				Bitmap thumb = ImageUtil.getResizedBitmap(bm, 160, 160);
				Bitmap reg = ImageUtil.getResizedBitmap(bm, 400, 300);
				bitmaps = new Bitmaps();
				bitmaps.setLargeBitmap(reg);
				bitmaps.setThumbNail(thumb);
				//save bitmaps on disk as files
				File fReg = ImageUtil.getFileFromBitmap(reg, "picM"+System.currentTimeMillis() + ".jpg");
				File fThumb = ImageUtil.getFileFromBitmap(thumb, "picT"+System.currentTimeMillis() + ".jpg");
				
				//save uri's in SharedPrefs
				SharedUtil.saveImageUri(ctx, Uri.fromFile(fReg));
				SharedUtil.saveThumbUri(ctx, Uri.fromFile(fThumb));
			} catch (Exception e) {
				e.printStackTrace();
				return 1;
			}
			return 0;
		}
		@Override
		protected void onPostExecute(Integer res) {
			if (res > 0) {
				bitmapListener.onError();
				return;
			}
			bitmapListener.onBitmapsResized(bitmaps);
		}
		
	}
}
