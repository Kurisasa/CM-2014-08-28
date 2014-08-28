package com.boha.cmlibrary.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.boha.cmlibrary.R;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.listeners.BitmapListener;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.DiskImageListener;
import com.boha.coursemaker.listeners.FileListener;
import com.boha.coursemaker.listeners.PhotoUploadedListener;
import com.boha.coursemaker.util.AsyncImageUtil;
import com.boha.coursemaker.util.Bitmaps;
import com.boha.coursemaker.util.FileTask;
import com.boha.coursemaker.util.ImageTask;
import com.boha.coursemaker.util.PictureUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.volley.toolbox.BohaVolley;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * Fragment that manages the profiles of Administratiors, Authors and Instructors. It manages the User Interface to edit
 * the data and manages the communications to the cloud server
 * @author aubreyM
 * @see com.boha.coursemaker.dto.AdministratorDTO
 * @see com.boha.coursemaker.dto.AuthorDTO
 * @see com.boha.coursemaker.dto.InstructorDTO
 */
public class ProfileFragment extends Fragment {

	public ProfileFragment() {
	}

	static final String LOG = "ProfileFragment";
	ImageLoader imageLoader;
	BusyListener busyListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException(
					"Host activity must implement BusyListener");
		}
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		Log.w(LOG, "onCreateView ...");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.profile, container, false);
		setFields();
		animate();

		return view;
	}

	private void setProfilePicture() {
		Log.d(LOG, "*** attempting to set profile picture");
		image = (ImageView) view.findViewById(R.id.PROF_image);
		imageLoader = BohaVolley.getImageLoader(getActivity());

		String uri = SharedUtil.getImageUri(ctx);
		if (uri != null) {
			AsyncImageUtil.getBitmapFromUri(Uri.parse(uri), ctx,
					new DiskImageListener() {

						@Override
						public void onError() {
							ToastUtil.errorToast(ctx, ctx.getResources()
									.getString(R.string.error_image_get));
							return;
						}

						@Override
						public void onBitmapReturned(Bitmap bitmap) {
							Log.i(LOG,
									"pic from disk, width: "
											+ bitmap.getWidth() + " height: "
											+ bitmap.getHeight());
							image.setImageBitmap(bitmap);
							return;
						}
					});
			return;
		}

		image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.boy));
		StringBuilder sb = new StringBuilder();
		switch (type) {
		case ADMIN:
			sb.append(Statics.IMAGE_URL).append("company")
					.append(administrator.getCompanyID()).append("/admin/");
			sb.append(administrator.getAdministratorID()).append(".jpg");
			break;
		case AUTHOR:
			sb.append(Statics.IMAGE_URL).append("company")
					.append(author.getCompanyID()).append("/author/");
			sb.append(author.getAuthorID()).append(".jpg");
			break;
		case INSTRUCTOR:
			sb.append(Statics.IMAGE_URL).append("company")
					.append(instructor.getCompanyID()).append("/instructor/");
			sb.append(instructor.getInstructorID()).append(".jpg");
			break;

		default:
			break;
		}
		busyListener.setBusy();
		imageLoader.get(sb.toString(), new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.w(LOG,
						"No photo was found for download " + error.toString());
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				image.setImageBitmap(response.getBitmap());
				FileTask.saveFileFromBitmap(response.getBitmap(), ctx,
						new FileListener() {

							@Override
							public void onFileSaved() {
								ToastUtil.toast(ctx, ctx.getResources()
										.getString(R.string.data_saved));

							}

							@Override
							public void onError() {
								ToastUtil.errorToast(ctx, ctx.getResources()
										.getString(R.string.error_image_save));

							}
						});
			}
		});

	}

	public void setType(int type) {
		this.type = type;
		Log.w(LOG, "Type has been set to " + type);
		getActors();
		setProfilePicture();
	}

	@Override
	public void onResume() {
		Log.e(LOG, "############### resuming in " + LOG);
		super.onResume();
		animate();
	}

	private void animate() {
		Animation an = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		an.setDuration(1000);
		view.startAnimation(an);
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
		super.onSaveInstanceState(state);
	}

	private void setFields() {
		btnSave = (Button) view.findViewById(R.id.PROF_submit);
		editPassword = (EditText) view.findViewById(R.id.PROF_address);
		editFirstName = (EditText) view.findViewById(R.id.PROF_firstName);
		editLastName = (EditText) view.findViewById(R.id.PROF_lastName);
		editCell = (EditText) view.findViewById(R.id.PROF_cell);
		image = (ImageView) view.findViewById(R.id.PROF_image);
		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImageSource();
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submitProfile();
			}
		});

	}

    /**
     * Submit profile data to cloud server
     */
	private void submitProfile() {
		Log.e(LOG, "validate - pictureChanged: " + pictureChanged);

		String uri = SharedUtil.getThumbUri(ctx);
		if (uri != null) {
			int companyID = 0, actorID = 0;
			int pType = 0;
			switch (type) {
			case ADMIN:
				companyID = administrator.getCompanyID();
				actorID = administrator.getAdministratorID();
				pType = PhotoUploadDTO.ADMINISTRATOR;
				break;
			case AUTHOR:
				companyID = author.getCompanyID();
				actorID = author.getAuthorID();
				pType = PhotoUploadDTO.AUTHOR;
				break;
			case INSTRUCTOR:
				companyID = instructor.getCompanyID();
				actorID = instructor.getInstructorID();
				pType = PhotoUploadDTO.INSTRUCTOR;
				break;

			default:
				break;
			}
			PictureUtil.uploadPhoto(companyID, actorID, uri, pType, ctx,
					new PhotoUploadedListener() {

						@Override
						public void onPhotoUploaded() {
							pictureChanged = false;
							Log.i(LOG, "picture has been uploaded OK");

						}

						@Override
						public void onPhotoUploadFailed() {
							pictureChanged = true;
							Log.e(LOG, "picture upload failed");

						}
					});
		}

		if (editCell.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_cell));
			return;
		}

		if (editFirstName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_firstname));
			return;
		}
		if (editLastName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_lastname));
			return;
		}
		if (editPassword.getText().toString().isEmpty()) {
			ToastUtil.errorToast(ctx,
					ctx.getResources().getString(R.string.enter_password));
			return;
		}

		RequestDTO request = new RequestDTO();
		//
		String suffix = Statics.SERVLET_ADMIN;
		switch (type) {
		case ADMIN:
			AdministratorDTO admin = new AdministratorDTO();
			admin.setAdministratorID(administrator.getAdministratorID());
			admin.setCellphone(editCell.getText().toString());
			admin.setFirstName(editFirstName.getText().toString());
			admin.setLastName(editLastName.getText().toString());
			admin.setPassword(editPassword.getText().toString());
			request.setRequestType(RequestDTO.UPDATE_ADMIN);
			request.setAdministrator(admin);
			break;
		case AUTHOR:
			AuthorDTO au = new AuthorDTO();
			au.setCellphone(editCell.getText().toString());
			au.setFirstName(editFirstName.getText().toString());
			au.setLastName(editLastName.getText().toString());
			au.setPassword(editPassword.getText().toString());
			au.setAuthorID(author.getAuthorID());
			request.setRequestType(RequestDTO.UPDATE_AUTHOR);
			request.setAuthor(au);
			break;
		case INSTRUCTOR:
			InstructorDTO in = new InstructorDTO();
			in.setCellphone(editCell.getText().toString());
			in.setFirstName(editFirstName.getText().toString());
			in.setLastName(editLastName.getText().toString());
			in.setPassword(editPassword.getText().toString());
			in.setInstructorID(instructor.getInstructorID());
			request.setRequestType(RequestDTO.UPDATE_INSTRUCTOR);
			request.setInstructor(in);
			break;
		case TRAINEE:

			break;
		case EXECUTIVE:
			break;
		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx))
			return;

		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx, suffix, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        switch (type) {
                            case ADMIN:
                                SharedUtil.saveAdmin(ctx,
                                        response.getAdministrator());
                                administrator = SharedUtil.getAdministrator(ctx);
                                break;
                            case AUTHOR:
                                SharedUtil.saveAuthor(ctx, response.getAuthor());
                                author = SharedUtil.getAuthor(ctx);
                                break;
                            case INSTRUCTOR:
                                SharedUtil.saveInstructor(ctx,
                                        response.getInstructor());
                                instructor = SharedUtil.getInstructor(ctx);
                                break;
                            default:
                                break;
                        }
                        ToastUtil.toast(
                                ctx,
                                ctx.getResources().getString(
                                        R.string.data_saved));
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(suffix, request, ctx,
//				new BaseVolley.BohaVolleyListener() {
//					@Override
//					public void onVolleyError(VolleyError error) {
//						busyListener.setNotBusy();
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//						Log.e(LOG, "VolleyError " + error.getMessage());
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO response) {
//						busyListener.setNotBusy();
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, response.getMessage());
//							return;
//						}
//						switch (type) {
//						case ADMIN:
//							SharedUtil.saveAdmin(ctx,
//									response.getAdministrator());
//							administrator = SharedUtil.getAdministrator(ctx);
//							break;
//						case AUTHOR:
//							SharedUtil.saveAuthor(ctx, response.getAuthor());
//							author = SharedUtil.getAuthor(ctx);
//							break;
//						case INSTRUCTOR:
//							SharedUtil.saveInstructor(ctx,
//									response.getInstructor());
//							instructor = SharedUtil.getInstructor(ctx);
//							break;
//						default:
//							break;
//						}
//						ToastUtil.toast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.data_saved));
//					}
//				});
	}

	private void getActors() {
		switch (type) {
		case ADMIN:
			administrator = SharedUtil.getAdministrator(ctx);
			editFirstName.setText(administrator.getFirstName());
			editLastName.setText(administrator.getLastName());
			editCell.setText(administrator.getCellphone());
			editPassword.setText(administrator.getPassword());
			break;
		case AUTHOR:
			author = SharedUtil.getAuthor(ctx);
			editFirstName.setText(author.getFirstName());
			editLastName.setText(author.getLastName());
			editCell.setText(author.getCellphone());
			editPassword.setText(author.getPassword());
			break;
		case INSTRUCTOR:
			instructor = SharedUtil.getInstructor(ctx);
			Log.e(LOG, "--- instrcutorID: " + instructor.getInstructorID());
			editFirstName.setText(instructor.getFirstName());
			editLastName.setText(instructor.getLastName());
			editCell.setText(instructor.getCellphone());
			editPassword.setText(instructor.getPassword());
			break;
		case TRAINEE:
			trainee = SharedUtil.getTrainee(ctx);
			break;

		default:
			break;
		}
	}

    /**
     * Start the device camera
     */
	private void startCameraIntent() {
		fileUri = PictureUtil.getImageFileUri();
		Intent cameraIntent = PictureUtil.getCameraIntent(image, fileUri);
		startActivityForResult(cameraIntent, CAPTURE_IMAGE);
	}

	private void startGalleryIntent() {
		fileUri = PictureUtil.getImageFileUri();
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PICK_IMAGE);

	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		switch (requestCode) {
		case CAPTURE_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				ImageTask.getResizedBitmaps(fileUri, ctx, new BitmapListener() {
					@Override
					public void onError() {
						ToastUtil.errorToast(
								ctx,
								ctx.getResources().getString(
										R.string.error_image_get));
					}

					@Override
					public void onBitmapsResized(Bitmaps bitmaps) {
						image.setImageBitmap(bitmaps.getLargeBitmap());

					}
				});
				pictureChanged = true;
			}
			break;
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				final Uri imageUri = data.getData();
				ImageTask.getResizedBitmaps(imageUri, ctx,
						new BitmapListener() {

							@Override
							public void onError() {
								ToastUtil.errorToast(ctx, ctx.getResources()
										.getString(R.string.error_image_get));
							}

							@Override
							public void onBitmapsResized(Bitmaps bitmaps) {
								image.setImageBitmap(bitmaps.getLargeBitmap());
							}
						});

				pictureChanged = true;

			} else {
				ToastUtil.toast(
						ctx,
						ctx.getResources().getString(
								R.string.image_pick_cancelled));
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void selectImageSource() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(ctx.getResources().getString(R.string.image_source));
		builder.setItems(
				new CharSequence[] {
						ctx.getResources().getString(R.string.gallery),
						ctx.getResources().getString(R.string.camera) },
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							startGalleryIntent();
							break;
						case 1:
							startCameraIntent();
							break;

						default:
							break;
						}

					}
				});

		builder.show();
	}

	boolean pictureChanged;
	static final int CAPTURE_IMAGE = 3, PICK_IMAGE = 4;
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat(
			"EEEE, dd MMMM yyyy HH:mm", loc);

	Context ctx;
	View view;
	AdministratorDTO administrator;
	AuthorDTO author;
	InstructorDTO instructor;
	TraineeDTO trainee;
	ImageView image;
	Spinner classSpinner, provSpinner, citySpinner;
	EditText editEmail, editPassword, editFirstName, editLastName, editCell;
	Button btnCancel, btnSave, btnTakePic;
	private static Uri fileUri;

	int type;

	public static final int ADMIN = 1, AUTHOR = 2, INSTRUCTOR = 3, TRAINEE = 4,
			EXECUTIVE = 5;
}
