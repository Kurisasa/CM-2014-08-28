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
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.HelpRequestDTO;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HelpRequestFragment extends Fragment implements ScreenInterface {

	public HelpRequestFragment() {
	}
	static final String LOG = "HelpRequestFragment";

	@Override
	public void onAttach(Activity a) {
		
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	public static HelpRequestFragment init(HelpRequestDTO helpRequest, ImageLoader imageLoader) {
		Log.i(LOG, "fragment init - creating new fragment");
		HelpRequestFragment f = new HelpRequestFragment();
		f.setHelpRequest(helpRequest);
		f.setImageLoader(imageLoader);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_help_request, container, false);
		Bundle b = getArguments();
		helpRequest = (HelpRequestDTO)b.getSerializable("helpRequest");
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

	public void animate() {
		Animation an = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		an.setDuration(1000);
		view.startAnimation(an);
	}
	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
		super.onSaveInstanceState(state);
	}


	private void setFields() {
		txtTrainee = (TextView) view.findViewById(R.id.HR_txtTrainee);
		txtCourse  = (TextView) view.findViewById(R.id.HR_course);
		txtActivity  = (TextView) view.findViewById(R.id.HR_activity);
		txtHelpType  = (TextView) view.findViewById(R.id.HR_helpType);
		txtComment = (TextView) view.findViewById(R.id.HR_comment);
		txtDateRequested = (TextView) view.findViewById(R.id.HR_dateRequested);
		chkScheduleMeeting  = (CheckBox) view.findViewById(R.id.HR_chkMeeting);
		chkProblemSorted= (CheckBox) view.findViewById(R.id.HR_chkSorted);
		editMessage = (EditText)view.findViewById(R.id.HR_message);
		btnRespond = (Button)view.findViewById(R.id.HR_btnRespond);
		imageView = (NetworkImageView)view.findViewById(R.id.HR_image);
		//
		
		btnRespond.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		if (helpRequest != null) {
			txtTrainee.setText(helpRequest.getCourseTraineeActivity().getTraineeName());
			txtCourse.setText(helpRequest.getCourseTraineeActivity().getCourseName());
			txtActivity.setText(helpRequest.getCourseTraineeActivity().getActivity().getActivityName());
			txtHelpType.setText(helpRequest.getHelpType().getHelpTypeName());
			if (helpRequest.getComment() == null || helpRequest.getComment().isEmpty()) {
				txtComment.setVisibility(View.GONE);
			} else {
				txtComment.setText(helpRequest.getComment());
			}
			txtDateRequested.setText(sdf.format(new Date(helpRequest.getDateRequested())));
			//
			StringBuilder sb = new StringBuilder();
			sb.append(Statics.IMAGE_URL);
			CompanyDTO c = SharedUtil.getCompany(ctx);
			sb.append("company").append(c.getCompanyID()).append("/");
			sb.append("trainee/");
			sb.append(helpRequest.getCourseTraineeActivity().getTraineeID()).append(".jpg");
			Log.d(LOG, "..about to get image at " + sb.toString());
			imageView.setImageUrl(sb.toString(), imageLoader);
			
		}
	}

	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm",  loc);
	
	Context ctx;
	View view;
	HelpRequestDTO helpRequest;
	TextView txtCourse, txtTrainee, txtActivity,
		txtHelpType, txtDateRequested, txtComment, txtCompletionDate;
	EditText editMessage;
	CheckBox chkCompleted, chkScheduleMeeting, chkProblemSorted;
	Button btnRespond;
	NetworkImageView imageView;
	ImageLoader imageLoader;

	public HelpRequestDTO getHelpRequest() {
		return helpRequest;
	}

	public void setHelpRequest(HelpRequestDTO helpRequest) {
		this.helpRequest = helpRequest;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}
	
}
