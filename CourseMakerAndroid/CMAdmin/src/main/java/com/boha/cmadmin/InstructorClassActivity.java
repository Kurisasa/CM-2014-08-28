package com.boha.cmadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmadmin.adapter.InstructorClassAdapter;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.InstructorClassDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.volley.toolbox.BohaVolley;

import java.util.ArrayList;
import java.util.List;

public class InstructorClassActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructor_class);
		ctx = getApplicationContext();

        response = (ResponseDTO) getIntent().getSerializableExtra("response");
        trainingClassList = response.getTrainingClassList();
        instructor = response.getInstructor();
        setFields();
        if (instructor.getInstructorClassList() != null) {
            instructorClassList = instructor.getInstructorClassList();
            Log.w(LOG,"instructor class list: " + instructorClassList.size());
            setList();
        } else {
            throw new UnsupportedOperationException("F#$%K! instructor class list is null & void");
        }
		setTitle(ctx.getResources().getString(R.string.instructor_classes));
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		ResponseDTO r = new ResponseDTO();
		instructor.setInstructorClassList(instructorClassList);
		r.setInstructor(instructor);		
		i.putExtra("response", r);
		setResult(Activity.RESULT_OK, i);
		finish();
		Log.e(LOG, "---+------------ finished!");
	}

	private void submit() {
		if (trainingClass == null) {
			ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.select_class));
			return;
		}
		if (checkIfExists()) {
			ToastUtil.toast(ctx, ctx.getResources().getString(R.string.class_already_assigned));
			return;
		}
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.ASSIGN_INSTRUCTOR_CLASS);
		r.setInstructorID(instructor.getInstructorID());
		r.setTrainingClassID(trainingClass.getTrainingClassID());
		
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        response = r;
                        if (response.getStatusCode() > 0) {
                            errMessage();
                            return;
                        }
                        //ToastUtil.toast(ctx, ctx.getResources().getString(R.string.data_saved));
                        instructorClassList = response.getInstructorClassList();
                        setList();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, new BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				volleyMessage(error);
//				setRefreshActionButtonState(false);
//
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO r) {
//				setRefreshActionButtonState(false);
//				response = r;
//				if (response.getStatusCode() > 0) {
//					errMessage();
//					return;
//				}
//				//ToastUtil.toast(ctx, ctx.getResources().getString(R.string.data_saved));
//				instructorClassList = response.getInstructorClassList();
//				setList();
//
//			}
//		});
		
	}
	private boolean checkIfExists() {
		boolean found = false;
		for (InstructorClassDTO ic : instructorClassList) {
			if (ic.getTrainingClassID() == trainingClass.getTrainingClassID()) {
				return true;
			}
		}
		return found;
	}

	private void setList() {
		adapter = new InstructorClassAdapter(ctx,
				R.layout.instructor_class_item, instructorClassList);

		txtCount.setText("" + instructorClassList.size());
		txtCount2.setText("" + instructorClassList.size());
		mListView.setAdapter(adapter);
		registerForContextMenu(mListView);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				instructorClass = instructorClassList.get(arg2);
			}
		});
		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						instructorClass = instructorClassList.get(arg2);
						return false;
					}
				});
	}

	private void setFields() {

		mListView = (ListView) findViewById(R.id.IC_list);
		spinner = (Spinner) findViewById(R.id.IC_classSpinner);
		btnAdd = (Button) findViewById(R.id.IC_btnAddClass);
		txtCity = (TextView) findViewById(R.id.TR_ITEM_txtCity);
		txtName = (TextView) findViewById(R.id.TR_ITEM_txtName);
		txtEmail = (TextView) findViewById(R.id.TR_ITEM_txtEmail);
		txtCount = (TextView) findViewById(R.id.IC_txtCount);
		txtCount2 = (TextView) findViewById(R.id.TR_ITEM_classCount);
		image = (NetworkImageView) findViewById(R.id.TR_ITEM_image);
		//
		txtName.setText(instructor.getFirstName() + " "
				+ instructor.getLastName());
		txtEmail.setText(instructor.getEmail());
		txtCity.setText(instructor.getCityName());
		//
		imageLoader = BohaVolley.getImageLoader(ctx);
		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company")
				.append(instructor.getCompanyID()).append("/instructor/");
		sb.append(instructor.getInstructorID()).append(".jpg");
		image.setDefaultImageResId(R.drawable.boy);
		image.setImageUrl(sb.toString(), imageLoader);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		setClassSpinner();
		//setList();
	}

	private void setClassSpinner() {
		Log.d(LOG, "setting classSpinner ..." + trainingClassList.size());
		final ArrayList<String> tarList = new ArrayList<String>();
		if (trainingClassList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_class));
			for (TrainingClassDTO p : trainingClassList) {
				tarList.add(p.getTrainingClassName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					R.layout.xxsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 0) {
						trainingClass = null;
						return;
					}
					trainingClass = trainingClassList.get(arg2 - 1);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.instructor_class, menu);
		mMenu = menu;
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_back:
			onBackPressed();
			return true;

		case android.R.id.home:
			Intent intent = new Intent(this, MainPagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.action_bar_progess);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}
	private void errMessage() {
		ToastUtil.errorToast(ctx, response.getMessage());
	}

	private void volleyMessage(VolleyError error) {
		ToastUtil.errorToast(ctx,
				ctx.getResources().getString(R.string.error_server_comms));
		Log.e(LOG, "Volley Error, msg: " + error.getMessage());
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.w(LOG, "onCreateContextMenu ...");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.instructor_class_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		instructorClass = instructorClassList.get(info.position);
		menu.setHeaderTitle(instructorClass.getTrainingClassName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(R.drawable.ic_action_edit));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected");
		switch (item.getItemId()) {

		case R.id.menu_remove_class:
			deleteInstructorClass();
		return true;
		
		
		default:
			return super.onContextItemSelected(item);
		}
	}
	private void deleteInstructorClass() {
		RequestDTO r = new RequestDTO();
		r.setRequestType(RequestDTO.DELETE_INSTRUCTOR_CLASS);
		r.setInstructorClassID(instructorClass.getInstructorClassID());

		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,r,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        instructorClassList = response.getInstructorClassList();
                        setList();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, r, ctx, new BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				setRefreshActionButtonState(false);
//				volleyMessage(error);
//
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO response) {
//				setRefreshActionButtonState(false);
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, response.getMessage());
//					return;
//				}
//				instructorClassList = response.getInstructorClassList();
//				setList();
//			}
//		});
	}
	
	
	List<InstructorClassDTO> removedList = new ArrayList<InstructorClassDTO>();
	List<InstructorClassDTO> addedList = new ArrayList<InstructorClassDTO>();
	Context ctx;
	static final String LOG = InstructorClassActivity.class.getName();
	TrainingClassDTO trainingClass;
	List<TrainingClassDTO> trainingClassList;
	List<InstructorClassDTO> instructorClassList;
	InstructorClassDTO instructorClass;
	ImageLoader imageLoader;
	Menu mMenu;
	InstructorDTO instructor;
	ListView mListView;
	Spinner spinner;
	Button btnAdd;
	NetworkImageView image;
	TextView txtName, txtEmail, txtCity, txtCount, txtCount2;
	ResponseDTO response;
	InstructorClassAdapter adapter;
}
