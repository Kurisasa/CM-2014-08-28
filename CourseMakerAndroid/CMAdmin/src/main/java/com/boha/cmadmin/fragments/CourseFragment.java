package com.boha.cmadmin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.boha.cmadmin.R;
import com.boha.cmadmin.listeners.PageInterface;

public class CourseFragment extends Fragment implements PageInterface {

	Context ctx;
	View view;
	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_course_list, container, false);
		
		return view;
	}
	@Override
	public void dummy() {
		// TODO Auto-generated method stub

	}

}
