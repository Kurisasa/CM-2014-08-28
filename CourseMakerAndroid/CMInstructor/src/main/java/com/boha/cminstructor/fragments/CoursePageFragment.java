package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cminstructor.R;
import com.boha.cmlibrary.adapters.ActivityAdapter;
import com.boha.cmlibrary.listeners.RatingListener;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.RatingDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CoursePageFragment extends Fragment implements PageInterface {

	public CoursePageFragment() {
	}

	public interface CoursePageFragmentListener {
        public void onRatingRequested(CourseTraineeActivityDTO cta, int type);
	}
	CoursePageFragmentListener listener;
	RatingListener ratingListener;
	@Override
	public void onAttach(Activity a) {
		if (a instanceof CoursePageFragmentListener) {
			listener = (CoursePageFragmentListener)a;
		} else {
			throw new UnsupportedOperationException("Host Activity: "+ 
					a.getLocalClassName() +" should implement CoursePageFragmentListener, sorry!");
		}
		if (a instanceof RatingListener) {
			ratingListener = (RatingListener)a;
		} else {
			throw new UnsupportedOperationException("Host Activity: "+ 
					a.getLocalClassName() +" should implement RatingListener, sorry!");
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
		Log.w(LOG, "#### onCreateView inflating view ..");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater
				.inflate(R.layout.fragment_course_page, container, false);
		
		setFields();

		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "####### resuming in " + " - " + courseName);
		if (courseTraineeActivityList != null) {
			setList();
		} 
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);	
		super.onSaveInstanceState(state);
	}

	
	private void setFields() {
		txtCourseName = (TextView) view.findViewById(R.id.LP_txtCourseName);
		txtTrainee = (TextView) view.findViewById(R.id.LP_txtTraineeName);
		listView = (ListView) view.findViewById(R.id.LP_list);
		txtCount = (TextView) view.findViewById(R.id.LP_actCount);		
		image = (NetworkImageView)view.findViewById(R.id.LP_image);
		image.setDefaultImageResId(R.drawable.boy);
		txtPerc = (TextView)view.findViewById(R.id.LP_txtPerc);
		
		
	}

	CourseTraineeActivityDTO courseTraineeActivity;
	
	private void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		txtCourseName.setText(courseName);
		CourseTraineeActivityDTO dto = courseTraineeActivityList.get(0);
		txtTrainee.setText(dto.getTraineeName());
		StringBuilder sb = new StringBuilder();
		sb.append(Statics.IMAGE_URL).append("company").append(dto.getCompanyID()).append("/trainee/");
		sb.append(dto.getTraineeID()).append(".jpg");
		image.setImageUrl(sb.toString(), imageLoader);
		
		adapter = new ActivityAdapter(getActivity(), R.layout.activity_item,
				courseTraineeActivityList, false, false,new ActivityAdapter.ActivityAdapterListener() {
            @Override
            public void onRatingRequested(CourseTraineeActivityDTO cta) {
                listener.onRatingRequested(cta,1);
            }

            @Override
            public void onHelpRequested(CourseTraineeActivityDTO cta) {

            }
        });

		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText(getResources().getString(R.string.total) + ": " + courseTraineeActivityList.size());
		txtPerc.setText(df.format(getPercentage()) + "%");

		int index = getSelectionIndex();
		listView.setSelection(index);
	}

	private double getPercentage() {
		int total = courseTraineeActivityList.size(), complete = 0;
		if (total == 0) return 0;
		for (CourseTraineeActivityDTO c : courseTraineeActivityList) {
			if (c.getCompletedFlag() > 0) {
				complete++;
			}
		}
		
		Double dt = Double.valueOf(total);
		Double dc = Double.valueOf(complete);
		Double p = (dc /dt) * 100;
		return p.doubleValue();
	}
	
	public void setCourseTraineeActivityList(List<CourseTraineeActivityDTO> list) {
		Log.w(LOG, "setting courseTraineeActivityList ...");
		this.courseTraineeActivityList = list;
		
	}
	private int getSelectionIndex() {
		List<CourseTraineeActivityDTO> list = new ArrayList<CourseTraineeActivityDTO>();
		for (CourseTraineeActivityDTO c : courseTraineeActivityList) {
			CourseTraineeActivityDTO x = new CourseTraineeActivityDTO();
			x.setCompletedFlag(c.getCompletedFlag());
			x.setCompletionDate(c.getCompletionDate());
			x.setCourseTraineeActivityID(c.getCourseTraineeActivityID());
			x.setActivity(c.getActivity());
			list.add(x);
		}
		Collections.sort(list);
		CourseTraineeActivityDTO cta = list.get(0);
		Log.d(LOG, "Latest record, activity name, date: " + new Date(cta.getCompletionDate()) + " - "+ cta.getActivity().getActivityName());
		int index = 0;
		for (CourseTraineeActivityDTO xx : courseTraineeActivityList) {
			if (xx.getCourseTraineeActivityID() == cta.getCourseTraineeActivityID()) {
				break;
			}
			index++;
		}
		Log.d(LOG, "listview selection index = " + index);
		return index;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCourseName() {
		return courseName;
	}

	
	static final DecimalFormat df = new DecimalFormat("###,##0.00");
	TextView txtTrainee, txtPerc;
	NetworkImageView image;
	ImageLoader imageLoader;
	List<CourseTraineeActivityDTO> courseTraineeActivityList;
	Context ctx;
	View view;
	String courseName;
	ActivityAdapter adapter;
	ListView listView;
	TextView txtCourseName, txtCount;
	
	static final String LOG = "CoursePageFragment";
	List<RatingDTO> ratingList;

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public void setRatingList(List<RatingDTO> ratingList) {
		this.ratingList = ratingList;
	}
}
