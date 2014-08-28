package com.boha.cmtrainee.fragments;

import android.app.Activity;
import android.content.Context;
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
import com.boha.cmtrainee.R;
import com.boha.cmtrainee.adapters.TraineeAdapter;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.email.AsyncMailSender;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.MailSenderListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.ToastUtil;

import java.util.Collections;
import java.util.List;

/**
 * Fragment that displays a list of Trainees.
 * Uses a busyListener to inform host activity to display or shut down progress bar
 *
 * @author aubreyM
 */
public class TraineeListFragment extends Fragment implements PageInterface,
		BusyListener {
	Context ctx;
	View view, space;
	BusyListener busyListener;

	public TraineeListFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		Log.d(LOG, "---- onAttach");
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
		Log.i(LOG, "-- onCreateView ............");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_trainee_list, container,
				false);
		setFields();
		
		Bundle b = getArguments();
		if (b != null) {
			response = (ResponseDTO) b.getSerializable("response");
			if (response != null) {
				traineeList = response.getTraineeList();

				//remove self
				TraineeDTO dto = SharedUtil.getTrainee(ctx);
				for (TraineeDTO t : traineeList) {
					if (t.getTraineeID() == dto.getTraineeID()) {
						traineeList.remove(t);
						break;
					}
				}
				setList();
			}
		}
		return view;
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


	List<TraineeDTO> traineeList;
	ImageLoader imageLoader;

	

	private void setList() {
		adapter = new TraineeAdapter(getActivity(), R.layout.trainee_item,
				traineeList, imageLoader);
		if (traineeList == null) {
			Log.w(LOG, "setList - list is NULL");
			return;
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
		
		txtCount = (TextView) view.findViewById(R.id.CLS_count);
		//space = view.findViewById(R.id.CLS_space);
		listView = (ListView) view.findViewById(R.id.CLS_list);
		TextView txt  = (TextView) view.findViewById(R.id.CLS_label);
		txt.setText(SharedUtil.getTrainingClass(ctx).getTrainingClassName());
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
		menu.setHeaderIcon(ctx.getResources().getDrawable(
				R.drawable.activity_48));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected");

		switch (item.getItemId()) {
		case R.id.menu_send_help_request:
			underConstruction();
			return true;
		case R.id.menu_send_mail:
			underConstruction();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	AdministratorDTO admin;

	@SuppressWarnings("unused")
	private void sendMail(String password) {
		admin = SharedUtil.getAdministrator(ctx);

		String subject = ctx.getResources().getString(
				R.string.password_email_subject);
		StringBuilder sb = new StringBuilder();
		sb.append(trainee.getFirstName()).append(" ")
				.append(trainee.getLastName());
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
				subject, body, trainee.getEmail(), new MailSenderListener() {

					@Override
					public void onMailSent() {
						ToastUtil.toast(
								ctx,
								ctx.getResources().getString(
										R.string.password_email_sent));
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

	private void underConstruction() {
		ToastUtil.toast(ctx, "Feature under construction. Watch the space!");
	}

	

	
	private TraineeAdapter adapter;
	private TraineeDTO trainee;
	private ResponseDTO response;
	
	private TextView txtCount;
	private ListView listView;
	
	private TrainingClassDTO trainingClass;

	@Override
	public void setBusy() {
		busyListener.setBusy();
	}

	@Override
	public void setNotBusy() {
		busyListener.setNotBusy();
	}


	public void setImageLoader(ImageLoader loader) {
		this.imageLoader = loader;
	}
	public void setResponse(ResponseDTO response) {
		this.response = response;
		trainingClass = response.getTrainingClass();
		traineeList = trainingClass.getTraineeList();
		setList();
		
	}
}
