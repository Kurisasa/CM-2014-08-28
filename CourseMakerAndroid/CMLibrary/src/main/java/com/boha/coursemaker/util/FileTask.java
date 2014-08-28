package com.boha.coursemaker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import com.boha.coursemaker.listeners.FileListener;

import java.io.File;

public class FileTask {

	static Bitmap bitmap;
	static Uri u;
	static Context ctx;
	static FileListener fileListener;
	
	public static void saveFileFromBitmap(Bitmap bm, Context context,FileListener listener) {
		ctx = context;
		fileListener = listener;
		bitmap = bm;
		new DTask().execute();
	}
	
	static class DTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				
				Bitmap thumb = ImageUtil.getResizedBitmap(bitmap, 160, 160);
				Bitmap reg = ImageUtil.getResizedBitmap(bitmap, 320, 200);
				
				File fileM = ImageUtil.getFileFromBitmap(reg, "picM" + System.currentTimeMillis() + ".jpg");
				File fileT = ImageUtil.getFileFromBitmap(thumb, "picT" + System.currentTimeMillis() + ".jpg");
				SharedUtil.saveImageUri(ctx, Uri.fromFile(fileM));
				SharedUtil.saveThumbUri(ctx, Uri.fromFile(fileT));
				
			} catch (Exception e) {
				e.printStackTrace();
				return 1;
			}
			return 0;
		}
		@Override
		protected void onPostExecute(Integer res) {
			if (res > 0) {
				fileListener.onError();
				return;
			}
			fileListener.onFileSaved();
		}
		
	}
}
