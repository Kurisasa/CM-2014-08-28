package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cminstructor.R;
import com.boha.cminstructor.adapters.TraineeCourseAdapter;
import com.boha.cminstructor.listeners.CourseListener;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;

public class CourseListFragment extends Fragment {

	public CourseListFragment() {
	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof CourseListener) {
			courseListener = (CourseListener) a;
		} else {
			throw new UnsupportedOperationException();
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
				.inflate(R.layout.fragment_course_list, container, false);
		if (saved != null) {
			Log.i(LOG, "saved is not null");
			data = (ResponseDTO) saved.getSerializable("data");
		} 
		setFields();

		return view;
	}

	@Override
	public void onResume() {
		Log.d(LOG, "#### onResume() ...");
		if (data != null) {
			setList();
		} 
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {

		Log.i("CourseListFrag", "##### onSaveInstanceState  fired ....");
		if (data != null) {
			state.putSerializable("data", data);
		} 
		super.onSaveInstanceState(state);
	}

	private void setFields() {
		txtTrainee = (TextView) view.findViewById(R.id.TH_txtTraineeName);
		txtClassName  = (TextView) view.findViewById(R.id.TH_txtCourseName);
		txtPerc  = (TextView) view.findViewById(R.id.TH_txtPerc);
		image = (NetworkImageView) view.findViewById(R.id.TH_image);
		
		listView = (ListView) view.findViewById(R.id.CRS_listView);
		txtCount = (TextView) view.findViewById(R.id.CRS_count);
		setHeader();
		
	}
	private void setHeader() {
		if (txtTrainee != null && trainee != null) {
			txtTrainee.setText(trainee.getFullName());
			txtClassName.setText(trainee.getTrainingClassName());
			txtPerc.setText(df.format(trainee.getPercComplete()) + "%");
			StringBuilder sb = new StringBuilder();
			sb.append(Statics.IMAGE_URL).append("company")
					.append(trainee.getCompanyID()).append("/trainee/");
			sb.append(trainee.getTraineeID()).append(".jpg");
			image.setDefaultImageResId(R.drawable.boy);
			image.setImageUrl(sb.toString(), imageLoader);
		}
	}
	static final String LOG = "CourseListFragment";
	static final DecimalFormat df = new DecimalFormat("###,###,##0.00");
	private void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		if (data == null || data.getTrainingClassCourseList().size() == 0) {
			Log.d(LOG, "setList() data is NULL");
			return;
		}
		adapter = new TraineeCourseAdapter(getActivity(), R.layout.course_item2,
				data.getTrainingClassCourseList());

		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText("" + data.getTrainingClassCourseList().size());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				trainingClassCourse = data.getTrainingClassCourseList().get(
						arg2);
				courseListener.onCoursePicked(trainingClassCourse);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				return false;
			}
		});
	}

	
	public void setData(ResponseDTO data) {
		this.data = data;
		if (listView != null) {
			setList();
		}
	}
	TraineeDTO trainee;
	TrainingClassCourseDTO trainingClassCourse;
	private CourseListener courseListener;
	Context ctx;
	View view;
	ResponseDTO data;

	TraineeCourseAdapter adapter;
	ListView listView;
	TextView txtClassName, txtTrainee, txtCount, txtPerc;
	NetworkImageView image;
	ImageLoader imageLoader;
	public void setActivityCompleted(int count) {
		Log.i(LOG, "setActivityCompleted " + count);
		trainee.setTotalCompleted(trainee.getTotalCompleted() + count);
		setHeader();
	}
	public void setTrainee(TraineeDTO trainee) {
		this.trainee = trainee;
		setHeader();
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

}
