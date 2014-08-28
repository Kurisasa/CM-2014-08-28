package com.boha.coursemaker.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.PhotoUploadedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PictureUtil {

	public static void uploadPhoto(Integer companyID, Integer id, String uri,
			int type, final Context ctx, final PhotoUploadedListener listener) {
		File imageFile = new File(Uri.parse(uri).getPath());
		Log.d(LOG, "File to be uploaded - " + imageFile.getAbsolutePath());
		List<File> files = new ArrayList<File>();
		if (imageFile.exists()) {
			files.add(imageFile);
			PhotoUploadDTO dto = new PhotoUploadDTO();
			switch (type) {
			case PhotoUploadDTO.TRAINEE:
				dto.setType(PhotoUploadDTO.TRAINEE);
				dto.setTraineeID(id);
				break;
			case PhotoUploadDTO.INSTRUCTOR:
				dto.setType(PhotoUploadDTO.INSTRUCTOR);
				dto.setInstructorID(id);
				break;
			case PhotoUploadDTO.ADMINISTRATOR:
				dto.setType(PhotoUploadDTO.ADMINISTRATOR);
				dto.setAdministratorID(id);
				break;
			case PhotoUploadDTO.AUTHOR:
				dto.setType(PhotoUploadDTO.AUTHOR);
				dto.setAuthorID(id);
				break;

			default:
				break;
			}

			dto.setCompanyID(companyID);
			dto.setNumberOfImages(1);
			ImageUpload.upload(dto, files,
					new ImageUpload.ImageUploadListener() {

						@Override
						public void onUploadError() {
							ToastUtil.errorToast(ctx, ctx.getResources()
									.getString(R.string.error_profile_upload));
							listener.onPhotoUploadFailed();
						}

						@Override
						public void onImageUploaded(ResponseDTO response) {
							if (response.getStatusCode() == 0) {
								ToastUtil
										.toast(ctx,
												ctx.getResources()
														.getString(
																R.string.profile_picture_uploaded));
								listener.onPhotoUploaded();
							} else {
								Log.e(LOG,
										"Error uploading - "
												+ response.getMessage());
								ToastUtil.errorToast(
										ctx,
										ctx.getResources().getString(
												R.string.error_profile_upload));
							}
						}
					});
		}

	}

	public static Intent getCameraIntent(ImageView image, Uri fileUri) {

		int w = image.getWidth();
		int h = image.getWidth();
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra("crop", "true");
		cameraIntent.putExtra("outputX", w);
		cameraIntent.putExtra("outputY", h);
		cameraIntent.putExtra("aspectX", 1);
		cameraIntent.putExtra("aspectY", 1);
		cameraIntent.putExtra("scale", true);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		return cameraIntent;

	}

	public static Uri getImageFileUri() {
		File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (rootDir == null) {
			rootDir = Environment.getRootDirectory();
		}
		File imgDir = new File(rootDir, "coursemaker");
		if (!imgDir.exists()) {
			imgDir.mkdir();
		}
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());

		return Uri.fromFile(new File(imgDir,"CM_" + timeStamp + ".jpg"));

	}



	static final int CAPTURE_IMAGE = 3, PICK_IMAGE = 4;
	private static final String LOG = "PictureUtil";

}
