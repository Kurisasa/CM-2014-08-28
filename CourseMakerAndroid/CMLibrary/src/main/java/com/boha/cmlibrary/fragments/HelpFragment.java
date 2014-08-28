package com.boha.cmlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmlibrary.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HelpFragment extends Fragment implements ScreenInterface {

	public HelpFragment() {
	}
	static final String LOG = "HelpFragment";

	@Override
	public void onAttach(Activity a) {
		
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	public static HelpFragment init(int helpType, ImageLoader imageLoader) {
		Log.i(LOG, "fragment init - creating new fragment");
		HelpFragment f = new HelpFragment();
		f.setImageLoader(imageLoader);
		f.setType(helpType);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_help, container, false);
		setFields();

		animate();
		return view;
	}

	@Override
	public void onResume() {
		Log.e(LOG, "############### resuming in " + LOG);		
		super.onResume();
		animate();
	}

	private void animate() {
		Animation an = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
		an.setDuration(1000);
		view.startAnimation(an);
	}
	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
		super.onSaveInstanceState(state);
	}


	private void setFields() {
		
	}

	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm",  loc);
	
	Context ctx;
	View view;
	
	TextView txtCourse, txtTrainee, txtActivity,
		txtHelpType, txtDateRequested, txtComment, txtCompletionDate;
	EditText editMessage;
	CheckBox chkCompleted, chkScheduleMeeting, chkProblemSorted;
	Button btnRespond;
	NetworkImageView imageView;
	ImageLoader imageLoader;

	int type;

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
