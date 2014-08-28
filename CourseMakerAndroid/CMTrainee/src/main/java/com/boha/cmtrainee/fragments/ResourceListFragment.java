package com.boha.cmtrainee.fragments;

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
import com.boha.cmtrainee.R;
import com.boha.cmtrainee.adapters.ResourceAdapter;
import com.boha.cmtrainee.interfaces.ResourceListener;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;

import java.util.List;

public class ResourceListFragment extends Fragment {

	public ResourceListFragment() {
	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof ResourceListener) {
			listener = (ResourceListener) a;
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
				.inflate(R.layout.fragment_resource_list, container, false);
		setFields();
		if (saved != null) {
			Log.i(LOG, "saved is not null");
			course = (CourseDTO) saved.getSerializable("course");
		} 
		

		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "############### onResume in " + LOG);
		if (course != null) {
			setList();
		} else {
		
		}
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {

		Log.i(LOG, "##### onSaveInstanceState  fired ....");
		if (course != null) {
			state.putSerializable("course", course);
		}
		super.onSaveInstanceState(state);
	}

	private void setFields() {
		txtCourseName = (TextView) view.findViewById(R.id.RES_course);
		txtLessonName = (TextView) view.findViewById(R.id.RES_lessonName);
		listView = (ListView) view.findViewById(R.id.RES_listView);
		txtCount = (TextView) view.findViewById(R.id.LH_count);
		
		if (course != null) {
			txtLessonName.setText(course.getCourseName());
		}
		
		
	}

	private void setList() {
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		adapter = new ResourceAdapter(getActivity(), R.layout.resource_item,
				resourceList);

		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText("" + resourceList.size());
		if (txtLessonName != null) {
			txtLessonName.setText(course.getCourseName());
		}
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				resource = resourceList.get(
						arg2);
				listener.onResourcePicked(resource);

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

	public void setData(CourseDTO course) {
		this.course = course;
		resourceList = course.getLessonResourceList();
		if (txtLessonName != null) {
			txtLessonName.setText(this.course.getCourseName());
		}
	}
	
	static final String LOG = "ResourceListFragment";
	private ResourceListener listener;
	
	
	Context ctx;
	View view;
    CourseDTO course;
	List<LessonResourceDTO> resourceList;
	LessonResourceDTO resource;
	ResourceAdapter adapter;
	ListView listView;
	TextView txtCourseName, txtLessonName, txtCount;

}
