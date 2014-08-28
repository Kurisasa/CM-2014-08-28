package com.boha.cmadmin.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmadmin.R;
import com.boha.cmadmin.adapter.TraineeAdapter;
import com.boha.cmadmin.listeners.CameraRequestListener;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.cmadmin.listeners.PasswordRequestListener;
import com.boha.cmadmin.listeners.PeopleDialogListener;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PhotoUploadedListener;
import com.boha.coursemaker.util.*;
import com.boha.volley.toolbox.BohaVolley;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.boha.coursemaker.util.email.AsyncMailSender;

public class TraineeListFragment extends Fragment implements PageInterface,
		BusyListener, PasswordRequestListener {

	Context ctx;
	View view, space;
	BusyListener busyListener;
	boolean isUpdate, isDelete;
	CameraRequestListener cameraRequestListener;

	public TraineeListFragment() {
	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host activity "
					+ a.getLocalClassName() + " must implement BusyListener");
		}

		if (a instanceof CameraRequestListener) {
			cameraRequestListener = (CameraRequestListener) a;
		} else {
			throw new UnsupportedOperationException("Host activity "
					+ a.getLocalClassName()
					+ " must implement CameraRequestListener");
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
		Log.i(LOG, "-- onCreateView ............");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_trainee_list, container,
				false);
		setFields();
		imageLoader = BohaVolley.getImageLoader(ctx);
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO) b.getSerializable("response");
			if (response != null) {
				setObjects();
			}
		}
		return view;
	}

	private void setObjects() {
		setTraineeList();
		setList();
		provinceList = response.getProvinceList();
		trainingClassList = response.getTrainingClassList();
	}

	@Override
	public void onResume() {
		Log.d(LOG, "---- onResume - nothing to be done");
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		Log.i(LOG, "onSaveInstanceState");
		b.putSerializable("response", response);
		super.onSaveInstanceState(b);
	}

	public void addTrainee() {
		PeopleDialog dg = new PeopleDialog();
		dg.setCtx(ctx);
		dg.setType(PeopleDialog.TRAINEE);
		dg.setTrainee(null);
		dg.setAction(PeopleDialog.ADD_NEW);
		dg.setProvinceList(provinceList);
		dg.setTrainingClassList(trainingClassList);
		dg.setPeopleDialogListener(new PeopleDialogListener() {

			@Override
			public void onRequestFinished(ResponseDTO response, int index) {
				if (response.getStatusCode() == 0) {
					traineeList = response.getTraineeList();					
					setList();
					listView.setSelection(index);
					
				}

			}

			@Override
			public void onError() {

			}
		});
		dg.show(getFragmentManager(), "trainee_update");
	}

	List<TraineeDTO> traineeList;
	ImageLoader imageLoader;
	Bitmaps bitmaps;

	public void setBitmaps(Bitmaps bms) {
		bitmaps = bms;
		new GetUriTask().execute();

	}

	File imageFile;
	Uri imageUri;

	class GetUriTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			try {
				imageFile = ImageUtil.getFileFromBitmap(bitmaps.getThumbNail(),
						"CM" + System.currentTimeMillis() + ".jpg");
			} catch (Exception e) {
				e.printStackTrace();
				return 9;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer res) {

			if (res > 0) {
				Log.e(LOG, "Problem getting file from bitmap");
				return;
			}
			imageUri = Uri.fromFile(imageFile);
			PhotoUploadDTO p = new PhotoUploadDTO();
			p.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
			p.setTraineeID(trainee.getTraineeID());
			p.setNumberOfImages(1);
			p.setType(PhotoUploadDTO.TRAINEE);

			Log.w(LOG, "...requesting trainee photo upload");
			PictureUtil.uploadPhoto(p.getCompanyID(), p.getTraineeID(),
					imageUri.toString(), p.getType(), ctx,
					new PhotoUploadedListener() {

						@Override
						public void onPhotoUploaded() {
							Log.i(LOG,
									"trainee photo has been uploaded for "
											+ trainee.getFirstName() + " "
											+ trainee.getLastName() + " id: "
											+ trainee.getTraineeID());
							int i = 0;
							for (TraineeDTO t : traineeList) {
								if (t.getTraineeID() == trainee
										.getTraineeID()) {
									break;
								}
								i++;
							}
							setList();
							listView.setSelection(i);

						}

						@Override
						public void onPhotoUploadFailed() {
							Log.e(LOG, "Problem uploading trainee photo");

						}
					});

		}
	}

	private void setTraineeList() {
		traineeList = new ArrayList<TraineeDTO>();
		for (TrainingClassDTO tc : response.getTrainingClassList()) {
			traineeList.addAll(tc.getTraineeList());
		}

	}

	private void setList() {

		adapter = new TraineeAdapter(getActivity(), R.layout.trainee_item,
				traineeList, imageLoader);
		if (traineeList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
		}
		if (traineeList.size() == 0) {
			addTrainee();
		}

		Collections.sort(traineeList);
		txtCount.setText("" + traineeList.size());
		listView.setAdapter(adapter);
		listView.setDividerHeight(0);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				trainee = traineeList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				trainee = traineeList.get(arg2);
				return false;
			}
		});
	}

	static final String LOG = "TraineeListFragment";

	private void setFields() {

		listView = (ListView) view.findViewById(R.id.CLS_list);
		txtCount = (TextView) view.findViewById(R.id.CLS_count);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.w(LOG, "onCreateContextMenu ...");
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.trainee_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		trainee = traineeList.get(info.position);
		menu.setHeaderTitle(trainee.getFullName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.users32));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected");
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		trainee = traineeList.get(info.position);

		switch (item.getItemId()) {
		case R.id.menu_takepic_trainee:
			// TODO - make sizes dynamic depending on device
			cameraRequestListener.onCameraRequested(160, 160,
					PhotoUploadDTO.TRAINEE, trainee.getTraineeID());

			return true;
		case R.id.menu_equipment_trainee:
			underConstruction();
			return true;
		case R.id.menu_update_trainee:
			PeopleDialog dg = new PeopleDialog();
			dg.setCtx(ctx);
			dg.setType(PeopleDialog.TRAINEE);
			dg.setTrainee(trainee);
			dg.setAction(PeopleDialog.UPDATE);
			dg.setProvinceList(provinceList);
			dg.setCityList(cityList);
			dg.setTrainingClassList(trainingClassList);
			dg.setPeopleDialogListener(new PeopleDialogListener() {

				@Override
				public void onRequestFinished(ResponseDTO response, int index) {
					if (index > -1) {
						traineeList = response.getTraineeList();
						setList();
						listView.setSelection(index);
					} else {
						trainee = response.getTrainee();
						adapter.notifyDataSetChanged();
					}

				}

				@Override
				public void onError() {

				}
			});
			dg.show(getFragmentManager(), "trainee");
			return true;
		case R.id.menu_delete_trainee:

			return true;
		case R.id.menu_send_trainee_password:
			PasswordRequestUtil.sendTraineePasswordRequest(trainee.getTraineeID(),
                    ctx, this, this);
			return true;
		case R.id.menu_trainee_class:
			underConstruction();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	AdministratorDTO admin;
	FragmentManager fragmentManager;

	public void setFragmentManager(FragmentManager fm) {
		fragmentManager = fm;
	}

	private void underConstruction() {
		ToastUtil.toast(ctx, "Feature under construction. Watch the space!");
	}

	@Override
	public void dummy() {
		// TODO Auto-generated method stub

	}

	public void addTrainingClassToList(TrainingClassDTO tc) {
		if (response.getTrainingClassList() != null) {
			response.getTrainingClassList().add(0, tc);
		} else {
			response.setTrainingClassList(new ArrayList<TrainingClassDTO>());
			response.getTrainingClassList().add(tc);
		}
	}

	private TraineeAdapter adapter;
	private TraineeDTO trainee;
	private ResponseDTO response;

	private TextView txtCount;
	private ListView listView;
	private List<ProvinceDTO> provinceList;
	private List<CityDTO> cityList;
	private List<TrainingClassDTO> trainingClassList;
	private TrainingClassDTO trainingClass;

	@Override
	public void setBusy() {
		busyListener.setBusy();
	}

	@Override
	public void setNotBusy() {
		busyListener.setNotBusy();
	}

	@Override
	public void onPasswordReturned(String password) {

	}

	public void setResponse(ResponseDTO response) {
		this.response = response;
		trainingClass = response.getTrainingClass();
		traineeList = trainingClass.getTraineeList();
		setList();
		provinceList = response.getProvinceList();
		trainingClassList = response.getTrainingClassList();
	}
}
