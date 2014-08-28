package com.boha.cmlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.cmlibrary.adapters.ExpandableListAdapter;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment implements PageInterface {

	Context ctx;
	View view;
	BusyListener busyListener;
	List<CategoryDTO> categoryList;
	DisplayMetrics metrics;
	int width;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			try {
				throw new Exception("Host activity must implement BusyListener");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		view = inflater.inflate(R.layout.fragment_category_list, container,
				false);
		setFields();
		Bundle b = getArguments();
		response = (ResponseDTO)b.getSerializable("response");
		company = SharedUtil.getCompany(ctx);
		administrator = SharedUtil.getAdministrator(ctx);
		setList();
		return view;
	}

	void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		setCoursesAlreadySelected();
		adapter = new ExpandableListAdapter(getActivity(), categoryList);
		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		width = metrics.widthPixels;
		// this code for adjusting the group indicator into right side of the
		// view
		listView.setIndicatorBounds(width - GetDipsFromPixel(150), width
				- GetDipsFromPixel(50));
		listView.setAdapter(adapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
	}

	private void setCoursesAlreadySelected() {
		for (CategoryDTO cat : categoryList) {
			for (CourseDTO crs : cat.getCourseList()) {
				for (TrainingClassCourseDTO tcc : trainingClass.getTrainingClassCourseList()) {					
					if (crs.getCourseID() == tcc.getCourse().getCourseID()) {
						crs.setSelected(true);
					}
				}
				
			}
		}
	}
	public int GetDipsFromPixel(float pixels) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	private void setFields() {

		listView = (ExpandableListView) view.findViewById(R.id.CL_expList);
		btnSave = (Button)view.findViewById(R.id.CL_btnSave);
		//btnSave.setEnabled(false);
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendData();
			}
		});

	}

	private void sendData() {
		List<CourseDTO> list = new ArrayList<CourseDTO>();
		for (CategoryDTO cat : categoryList) {
			for (CourseDTO c : cat.getCourseList()) {
				if (c.isSelected()) {
					CourseDTO dto = new CourseDTO();
					dto.setCourseID(c.getCourseID());
					list.add(dto);
				}
			}
		}
		if (list.size() == 0) {
			ToastUtil.toast(ctx, ctx.getResources().getString(R.string.no_courses_selected));
			return;
		}
		Log.w("CatListFrag", "Attempt to set courses to class: " + list.size());
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.ADD_COURSES_TO_CLASS);
		req.setCourseList(list);
		req.setTrainingClassID(trainingClass.getTrainingClassID());
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

		busyListener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.INSTRUCTOR_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
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
                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.courses_saved_to_class));
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
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx, new BaseVolley.BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				busyListener.setNotBusy();
//
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO response) {
//				busyListener.setNotBusy();
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, response.getMessage());
//					return;
//				}
//				ToastUtil.toast(ctx, ctx.getResources().getString(R.string.courses_saved_to_class));
//
//			}
//		});
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
			//MenuInflater inflater = getActivity().getMenuInflater();
			//inflater.inflate(R.menu.equipment_context_menu, menu);
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		

		default:
			return super.onContextItemSelected(item);
		}
	}

	DatePicker startPicker, endPicker;
	ExpandableListAdapter adapter;
	TrainingClassDTO trainingClass;
	ResponseDTO response;
	CompanyDTO company;
	AdministratorDTO administrator;
	Button btnSave;

	TextView txtCount, txtLabel;
	ExpandableListView listView;

	

	public List<CategoryDTO> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryDTO> categoryList) {
		this.categoryList = categoryList;
	}

	public TrainingClassDTO getTrainingClass() {
		return trainingClass;
	}

	public void setTrainingClass(TrainingClassDTO trainingClass) {
		this.trainingClass = trainingClass;
	}
	static final String LOG = "CategoryListFragment";
}
