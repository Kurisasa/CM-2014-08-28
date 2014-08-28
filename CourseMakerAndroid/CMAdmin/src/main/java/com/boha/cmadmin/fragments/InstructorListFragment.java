package com.boha.cmadmin.fragments;

import android.app.Activity;
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
import com.boha.cmadmin.adapter.InstructorAdapter;
import com.boha.cmadmin.listeners.*;
import com.boha.cmlibrary.CMApp;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.email.AsyncMailSender;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.MailSenderListener;
import com.boha.coursemaker.listeners.PhotoUploadedListener;
import com.boha.coursemaker.util.*;
import com.boha.volley.toolbox.BohaVolley;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class InstructorListFragment extends Fragment implements PageInterface,
		BusyListener, PasswordRequestListener {

	Context ctx;
	View view, space;
	BusyListener busyListener;
	InstructorClassRequestListener instructorClassRequestListener;
	CameraRequestListener cameraRequestListener;

	public InstructorListFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host "
					+ a.getLocalClassName()
					+ " must implement BusyListener");
		}
		if (a instanceof InstructorClassRequestListener) {
			instructorClassRequestListener = (InstructorClassRequestListener) a;
		} else {
			throw new UnsupportedOperationException("Host "
					+ a.getLocalClassName()
					+ " must implement InstructorClassRequestListener");
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
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_instructor_list, container,
				false);
		setFields();
		imageLoader = BohaVolley.getImageLoader(ctx);
		Bundle b = getArguments();
		response = (ResponseDTO) b.getSerializable("response");
		instructorList = response.getInstructorList();
		setList();
		return view;
	}


	public void addInstructor() {
		PeopleDialog dg = new PeopleDialog();
		dg.setCtx(ctx);
		dg.setType(PeopleDialog.INSTRUCTOR);
		dg.setAction(PeopleDialog.ADD_NEW);
		dg.setPeopleDialogListener(new PeopleDialogListener() {
			
			@Override
			public void onRequestFinished(ResponseDTO response, int index) {
				instructorList = response.getInstructorList();
				setList();
				listView.setSelection(index);				
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		});
		dg.show(getFragmentManager(), "instructor");
	}

	List<InstructorDTO> instructorList;

	ImageLoader imageLoader;

	private void setList() {

		if (imageLoader == null) {
            CMApp app = (CMApp) getActivity().getApplication();
			imageLoader = app.getImageLoader();
		}
		adapter = new InstructorAdapter(getActivity(),
				R.layout.instructor_item, instructorList, imageLoader);
		if (instructorList == null) {
			Log.w(LOG, "setList - instructorList is NULL");
			return;
		}
		if (instructorList.size() == 0) {
			addInstructor();
		}
		txtCount.setText("" + instructorList.size());
		listView.setAdapter(adapter);
		listView.setDividerHeight(2);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				instructor = instructorList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				instructor = instructorList.get(arg2);
				return false;
			}
		});
	}

	static final String LOG = "InstructorListFragment";

	private void setFields() {
		
		txtCount = (TextView) view.findViewById(R.id.CLS_count);
		listView = (ListView) view.findViewById(R.id.CLS_list);
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.w(LOG, "onCreateContextMenu ...");
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.instructor_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		instructor = instructorList.get(info.position);
        if (instructor == null) throw new UnsupportedOperationException("zeInstructor is null");
		menu.setHeaderTitle(instructor.getFirstName() + " "
				+ instructor.getLastName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.users32));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected");
		switch (item.getItemId()) {

		case R.id.menu_takepic_instructor:
			//TODO - make sizes dynamic
			cameraRequestListener.onCameraRequested(160, 160, PhotoUploadDTO.INSTRUCTOR, instructor.getInstructorID());
			return true;
		case R.id.menu_update_instructor:
			PeopleDialog dg = new PeopleDialog();
			dg.setCtx(ctx);
			dg.setInstructor(instructor);
			dg.setType(PeopleDialog.INSTRUCTOR);
			dg.setAction(PeopleDialog.UPDATE);
			dg.setPeopleDialogListener(new PeopleDialogListener() {
				
				@Override
				public void onRequestFinished(ResponseDTO response, int index) {
					instructor = response.getInstructor();
					adapter.notifyDataSetChanged();
					
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					
				}
			});
			dg.show(getFragmentManager(), "instructor");
			return true;
		case R.id.menu_delete_instructor:
			ToastUtil.toast(ctx, "Under contruction ...");
			return true;
		case R.id.menu_send_instr_password:
			PasswordRequestUtil.sendInstructorPasswordRequest(
                    instructor.getInstructorID(), ctx, this, this);
			return true;
		case R.id.menu_instr_classes:
			instructorClassRequestListener
					.onInstructorClassRequested(instructor);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void refreshInstructor(InstructorDTO ins) {
		//
		for (InstructorDTO i : instructorList) {
			if (ins.getInstructorID() == i.getInstructorID()
					) {
				instructorList.remove(i);
				Log.w(LOG, "instructor removed temporarily ...");
				break;
			}
		}
		instructorList.add(ins);
		Collections.sort(instructorList);
		adapter.notifyDataSetChanged();
		Log.i(LOG,
				"instructor classes updated from indirect intent from AdminActivity");
	}

	public static final int REQUEST_INSTRUCTOR_CLASS = 333;

	@Override
	public void setBusy() {
		busyListener.setBusy();
	}

	@Override
	public void setNotBusy() {
		busyListener.setNotBusy();
	}

	@Override
	public void dummy() {
		// TODO Auto-generated method stub

	}

	AdministratorDTO admin;

	private void sendMail(String password) {
		Log.w(LOG, "... sending email ...");
		admin = SharedUtil.getAdministrator(ctx);

		String subject = ctx.getResources().getString(
				R.string.password_email_subject);
		StringBuilder sb = new StringBuilder();
		sb.append(instructor.getFirstName()).append(" ")
				.append(instructor.getLastName());
		sb.append("\n\n");
		sb.append(ctx.getResources().getString(R.string.body)).append("\n\n");
		sb.append(ctx.getResources().getString(R.string.welcome_aboard))
				.append("\n\n\n");
		sb.append("\t").append(password).append("\n\n\n");
		sb.append(ctx.getResources().getString(R.string.regards)).append("\n");
		sb.append(ctx.getResources().getString(R.string.coursemaker_community))
				.append("\n");
		String body = sb.toString();

		Log.i(LOG, body);

		AsyncMailSender.sendMail(admin.getEmail(), admin.getPassword(),
				subject, body, instructor.getEmail(), new MailSenderListener() {

					@Override
					public void onMailSent() {
						ToastUtil.toast(
								ctx,
								ctx.getResources().getString(
										R.string.password_email_sent));
						Log.i(LOG, "... email sent OK!!");
					}

					@Override
					public void onMailError() {
						ToastUtil.errorToast(
								ctx,
								ctx.getResources().getString(
										R.string.error_email));
					}
				});
	}

	
	private InstructorAdapter adapter;
	private InstructorDTO instructor;
	private ResponseDTO response;
	private TextView txtCount;
	private ListView listView;

	@Override
	public void onPasswordReturned(String password) {
		sendMail(password);

	}
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
						"CM"+System.currentTimeMillis()+".jpg");
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
			p.setInstructorID(instructor.getInstructorID());
			p.setNumberOfImages(1);
			p.setType(PhotoUploadDTO.INSTRUCTOR);
			
			Log.w(LOG, "...requesting instructor photo upload");
			PictureUtil.uploadPhoto(p.getCompanyID(), p.getInstructorID(), 
					imageUri.toString(), p.getType(), ctx, new PhotoUploadedListener() {
				
				@Override
				public void onPhotoUploaded() {
					Log.i(LOG, "instructor photo has been uploaded for " 
							+ instructor.getFirstName() + " " 
							+ instructor.getLastName() + " id: " + instructor.getInstructorID());
					int i = 0;
					for (InstructorDTO t : instructorList) {
						if (t.getInstructorID() == instructor.getInstructorID()) {
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
}
