package com.boha.coursemaker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.boha.coursemaker.listeners.DiskImageListener;

import java.io.File;

public class AsyncImageUtil {

	static DiskImageListener listener;
	static Bitmap bitmap;
	static File file;
	static Uri mUri;
	static Context ctx;
	static int w, h;
	static Bitmaps bitmaps;
	
	
	
	public static void getBitmapFromUri(Uri uri, Context context, DiskImageListener diskImageListener) {
		mUri = uri;
		listener = diskImageListener;
		ctx = context;
		new BitmapTask().execute();
	}
	
		
	
	static class BitmapTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			
			try {
				bitmap = ImageUtil.getBitmapFromUri(ctx, mUri);
			} catch (Exception e) {
				Log.e(LOG, "Failure on a grand scale", e);
				return 1;
			}
			return 0;
		}
		@Override
		protected void onPostExecute(Integer res) {
			if (res > 0) {
				listener.onError();
				return;
			}
			listener.onBitmapReturned(bitmap);
		}
		
	}
	
	static final String LOG = "AsyncImageUtil";
}
