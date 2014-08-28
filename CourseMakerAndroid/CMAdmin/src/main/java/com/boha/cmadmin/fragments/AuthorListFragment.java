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
import com.boha.cmadmin.adapter.AuthorAdapter;
import com.boha.cmadmin.listeners.*;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PhotoUploadedListener;
import com.boha.coursemaker.util.*;
import com.boha.volley.toolbox.BohaVolley;

import java.io.File;
import java.util.List;

public class AuthorListFragment extends Fragment implements PageInterface, BusyListener, PasswordRequestListener {

	Context ctx;
	View view;
	BusyListener busyListener;
	CameraRequestListener cameraRequestListener;
	public AuthorListFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException();
		}
		if (a instanceof ContextMenuInterface) {
			contextListener = (ContextMenuInterface) a;
		} else {
			throw new UnsupportedOperationException();
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
		view = inflater
				.inflate(R.layout.fragment_author_list, container, false);
		setFields();
		administrator = SharedUtil.getAdministrator(ctx);
		imageLoader = BohaVolley.getImageLoader(ctx);
		Bundle b = getArguments();
		response = (ResponseDTO)b.getSerializable("response");
		authorList = response.getAuthorList();
		setList();
		return view;
	}

	

	public void addAuthor() {
		PeopleDialog dg = new PeopleDialog();
		dg.setCtx(ctx);
		dg.setType(PeopleDialog.AUTHOR);
		dg.setAction(PeopleDialog.ADD_NEW);
		dg.setPeopleDialogListener(new PeopleDialogListener() {
			
			@Override
			public void onRequestFinished(ResponseDTO response, int index) {
				authorList = response.getAuthorList();
				setList();
				listView.setSelection(index);
				
			}
			
			@Override
			public void onError() {
				
			}
		});
		dg.show(getFragmentManager(), "author");
	}

	List<AuthorDTO> authorList;
	ImageLoader imageLoader;


	private void setList() {
		if (imageLoader == null) {
			imageLoader = BohaVolley.getImageLoader(getActivity());
		}
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		adapter = new AuthorAdapter(getActivity(), R.layout.author_item, authorList,
				imageLoader);
		if (authorList == null) {
			Log.w(LOG, "setList - authorList is NULL");
			return;
		}
		if (authorList.size() == 0) {
			addAuthor();
		}
		txtCount.setText("" + authorList.size());
		listView.setAdapter(adapter);
		listView.setDividerHeight(2);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				author = authorList.get(arg2);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				author = authorList.get(arg2);
				return false;
			}
		});
	}

	static final String LOG = "AuthorListFragment";

	private void setFields() {
		
		txtCount = (TextView) view.findViewById(R.id.CLS_count);

		listView = (ListView) view.findViewById(R.id.CLS_list);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.w(LOG, "onCreateContextMenu ...");
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.author_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		author = authorList.get(info.position);
		menu.setHeaderTitle(author.getFirstName() + " " + author.getLastName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.users32));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected: " + item.getTitle());
		switch (item.getItemId()) {

		case R.id.menu_takepic_author:
			//TODO - dynamic sizes
			cameraRequestListener.onCameraRequested(160, 160, PhotoUploadDTO.AUTHOR, author.getAuthorID());
			return true;
		case R.id.menu_update_author:
			PeopleDialog dg = new PeopleDialog();
			dg.setCtx(ctx);
			AuthorDTO a = new AuthorDTO();
			a.setAuthorID(author.getAuthorID());
			a.setCompanyID(author.getCompanyID());
			a.setFirstName(author.getFirstName());
			a.setLastName(author.getLastName());
			a.setEmail(author.getEmail());
			a.setCellphone(author.getCellphone());
			dg.setAuthor(a);
			dg.setType(PeopleDialog.AUTHOR);
			dg.setAction(PeopleDialog.UPDATE);
			dg.setPeopleDialogListener(new PeopleDialogListener() {
				
				@Override
				public void onRequestFinished(ResponseDTO response, int index) {
					author = response.getAuthor();
					adapter.notifyDataSetChanged();
					
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					
				}
			});
			dg.show(getFragmentManager(), "instructor");
			return true;
		case R.id.menu_delete_author:
			ToastUtil.toast(ctx, "Under Construction ...");
			return true;
		case R.id.menu_update_admin:
			contextListener.redirectMenuItem(item);
			return true;
		case R.id.menu_delete_admin:
			contextListener.redirectMenuItem(item);
			return true;
		case R.id.menu_send_author_pswd:
			PasswordRequestUtil.sendAuthorPasswordRequest(author.getAuthorID(), ctx, this, this);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}
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
	private ContextMenuInterface contextListener;
	
	private AuthorAdapter adapter;
	private AuthorDTO author;
	private ResponseDTO response;
	@SuppressWarnings("unused")
	private AdministratorDTO administrator;
	
	private TextView txtCount;
	private ListView listView;

	

	@Override
	public void onPasswordReturned(String password) {
		
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
			if (ctx == null) {
				ctx = getActivity();
				Log.e(LOG, "context was null, had to reset it");
			}
			p.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
			p.setAuthorID(author.getAuthorID());
			p.setNumberOfImages(1);
			p.setType(PhotoUploadDTO.AUTHOR);
			
			Log.w(LOG, "...requesting author photo upload");
			PictureUtil.uploadPhoto(p.getCompanyID(), p.getAuthorID(), 
					imageUri.toString(), p.getType(), ctx, new PhotoUploadedListener() {
				
				@Override
				public void onPhotoUploaded() {
					Log.i(LOG, "author photo has been uploaded for " 
							+ author.getFirstName() + " " 
							+ author.getLastName() + " id: " + author.getAuthorID());
					int i = 0;
					for (AuthorDTO t : authorList) {
						if (t.getAuthorID() == author.getAuthorID()) {
							break;
						}
						i++;
					}
					setList();
					listView.setSelection(i);
					
				}
				
				@Override
				public void onPhotoUploadFailed() {
					Log.e(LOG, "Problem uploading author photo");
					
				}
			});
			
		}
	}
}
