package com.boha.cmadmin.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.boha.cmadmin.adapter.AdminAdapter;
import com.boha.cmadmin.listeners.CameraRequestListener;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.cmadmin.listeners.PasswordRequestListener;
import com.boha.cmadmin.listeners.PeopleDialogListener;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.email.AsyncMailSender;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.MailSenderListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.volley.toolbox.BohaVolley;

import java.io.File;
import java.util.List;

public class AdminListFragment extends Fragment implements PageInterface,
		BusyListener, PasswordRequestListener {

	Context ctx;
	View view, space;
	BusyListener busyListener;
	CameraRequestListener cameraRequestListener;
	public AdminListFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host activity "
					+ a.getLocalClassName()
					+ " must implement BusyListener");
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
		view = inflater.inflate(R.layout.fragment_admin_list, container, false);
		setFields();
		administrator = SharedUtil.getAdministrator(ctx);
		imageLoader = BohaVolley.getImageLoader(ctx);
		Bundle b = getArguments();
		response = (ResponseDTO)b.getSerializable("response");
		adminList = response.getAdministratorList();
		setList();
		return view;
	}


	public void addAdministrator() {
		PeopleDialog dg = new PeopleDialog();
		dg.setCtx(ctx);
		dg.setType(PeopleDialog.ADMIN);
		dg.setAction(PeopleDialog.ADD_NEW);
		dg.setPeopleDialogListener(new PeopleDialogListener() {
			
			@Override
			public void onRequestFinished(ResponseDTO response, int index) {
				adminList = response.getAdministratorList();
				setList();
				listView.setSelection(index);				
			}
			
			@Override
			public void onError() {
				
			}
		});
		dg.show(getFragmentManager(), "admin");
	}

	List<AdministratorDTO> adminList;
	ImageLoader imageLoader;

	private void setList() {
		if (imageLoader == null) {
			imageLoader = BohaVolley.getImageLoader(getActivity());
		}
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		adapter = new AdminAdapter(getActivity(), R.layout.admin_item, adminList,
				imageLoader);
		if (adminList == null) {
			Log.w(LOG, "setList - adminList is NULL");
			return;
		}
		if (adminList.size() == 0) {
			addAdministrator();
		}
		txtCount.setText("" + adminList.size());
		listView.setAdapter(adapter);
		listView.setDividerHeight(2);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedAdministrator = adminList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectedAdministrator = adminList.get(arg2);
				return false;
			}
		});
	}

	static final String LOG = "AdminListFragment";

	private void setFields() {
		
		txtCount = (TextView) view.findViewById(R.id.CLS_count);
		listView = (ListView) view.findViewById(R.id.CLS_list);
		

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.w(LOG, "onCreateContextMenu ...");
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.admin_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		selectedAdministrator = adminList.get(info.position);
		menu.setHeaderTitle(selectedAdministrator.getFirstName() + " "
				+ selectedAdministrator.getLastName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.users32));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected: " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_takepic_admin:
			//TODO dynamic sizes
			cameraRequestListener.onCameraRequested(160, 160, PhotoUploadDTO.ADMINISTRATOR, selectedAdministrator.getAdministratorID());
			return true;
		case R.id.menu_update_admin:
			PeopleDialog dg = new PeopleDialog();
			dg.setCtx(ctx);
			dg.setAdministrator(selectedAdministrator);
			dg.setType(PeopleDialog.ADMIN);
			dg.setAction(PeopleDialog.UPDATE);
			dg.setPeopleDialogListener(new PeopleDialogListener() {
				
				@Override
				public void onRequestFinished(ResponseDTO response, int index) {
					selectedAdministrator = response.getAdministrator();
					adapter.notifyDataSetChanged();
				}
				
				@Override
				public void onError() {
					
				}
			});
			dg.show(getFragmentManager(), "admin");
			return true;
		case R.id.menu_delete_admin:
			ToastUtil.toast(ctx, "Under Construction ...");
			return true;
		case R.id.menu_send_admin_password:
			PasswordRequestUtil.sendAdminPasswordRequest(
                    selectedAdministrator.getAdministratorID(), ctx, this, this);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
		
	}

	AdministratorDTO admin;
	private void sendMail(String password) {
		admin = SharedUtil.getAdministrator(ctx);
		
		String subject = ctx.getResources().getString(R.string.password_email_subject);
		StringBuilder sb = new StringBuilder();
		sb.append(administrator.getFirstName()).append(" ").append(administrator.getLastName());
		sb.append("\n\n");
		sb.append(ctx.getResources().getString(R.string.body)).append("\n\n");
		sb.append(ctx.getResources().getString(R.string.welcome_aboard)).append("\n\n\n");
		sb.append("\t").append(password).append("\n\n\n");
		sb.append(ctx.getResources().getString(R.string.regards)).append("\n");
		sb.append(ctx.getResources().getString(R.string.coursemaker_community)).append("\n");
		String body = sb.toString();
		
		Log.i(LOG, body);
		
		AsyncMailSender.sendMail(admin.getEmail(), admin.getPassword(), subject, body, administrator.getEmail(), new MailSenderListener() {
			
			@Override
			public void onMailSent() {
				ToastUtil.toast(ctx, ctx.getResources().getString(R.string.password_email_sent));
			}
			
			@Override
			public void onMailError() {
				ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_email));				
			}
		});
	}
	@Override
	public void dummy() {
	}


	private AdminAdapter adapter;
	private AdministratorDTO selectedAdministrator;
	private ResponseDTO response;
	private AdministratorDTO administrator;
	
	private TextView txtCount;
	private ListView listView;


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
		sendMail(password);
		
	}
	Bitmap bitmap;
	public void setBitmap(Bitmap bms) {
		bitmap = bms;
	}
	File imageFile;
	Uri imageUri;


}
