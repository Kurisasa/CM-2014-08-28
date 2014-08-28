package com.boha.cmserverprobe.fragments;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.CompanyStatsDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;

public class CompanyStatsFragment extends Fragment implements PageInterface{

	Context ctx;
	View view, space;
	BusyListener busyListener;

	public CompanyStatsFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
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
		Log.d(LOG, "onCreateView");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.company_stats, container, false);
		setFields();
		Bundle b = getArguments();
		if (b != null) {
			companyStats = (CompanyStatsDTO)b.getSerializable("companyStats");
			Log.i(LOG, "company stats from args " + companyStats.getCompanyName());
		}
		setStatsForm();
		return view;
	}


	public void setData(CompanyStatsDTO stats) {
		Log.d(LOG, "setData");
		this.companyStats = stats;
		setStatsForm();

	}

	private void setStatsForm() {
		if (companyStats == null) return;
		if (txtAdmins == null) return;
		
		txtCompany.setText(companyStats.getCompanyName());
		txtDate.setText(sdf.format(new Date(companyStats.getDateRegistered())));
		txtAdmins.setText(df.format(companyStats.getNumberOfAdmins()));
		txtAuthors.setText(df.format(companyStats.getNumberOfAuthors()));
		txtInst.setText(df.format(companyStats.getNumberOfInstructors()));
		txtTrainees.setText(df.format(companyStats.getNumberOfTrainees()));
		
		txtClasses.setText(df.format(companyStats.getNumberOfClasses()));
		txtCategories.setText(df.format(companyStats.getNumberOfCategories()));
		txtCourses.setText(df.format(companyStats.getNumberOfCourses()));
		txtLessons.setText(df.format(companyStats.getNumberOfLessons()));
		txtActivities.setText(df.format(companyStats.getNumberOfActivities()));
		txtLinks.setText(df.format(companyStats.getNumberOfResourceLinks()));
		
		txtInstRatings.setText(df.format(companyStats.getNumberOfInstructorRatings()));
		txtTraineeRatings.setText(df.format(companyStats.getNumberOfTraineeRatings()));
		txtTraineeActs.setText(df.format(companyStats.getNumberOfCourseTraineeActivities()));
		
	}


	public CompanyStatsDTO getCompanyStats() {
		return companyStats;
	}

	public void setCompanyStats(CompanyStatsDTO companyStats) {
		this.companyStats = companyStats;
	}


	static final String LOG = "CompanyListFragment";

	private void setFields() {
		
		
		txtAdmins = (TextView) view.findViewById(R.id.STAT_admins);
		txtAuthors  = (TextView) view.findViewById(R.id.STAT_authors);
		txtInst  = (TextView) view.findViewById(R.id.STAT_instructors);
		txtTrainees = (TextView) view.findViewById(R.id.STAT_trainees);
		txtClasses  = (TextView) view.findViewById(R.id.STAT_classes);
		txtCategories  = (TextView) view.findViewById(R.id.STAT_categories);
		txtCourses  = (TextView) view.findViewById(R.id.STAT_courses);
		txtLessons  = (TextView) view.findViewById(R.id.STAT_lessons);
		txtActivities  = (TextView) view.findViewById(R.id.STAT_activities);
		txtInstRatings = (TextView) view.findViewById(R.id.STAT_insRatings);
		txtTraineeRatings  = (TextView) view.findViewById(R.id.STAT_traineeRatings);
		txtTraineeActs  = (TextView) view.findViewById(R.id.STAT_traineeActivities);
		txtCompany   = (TextView) view.findViewById(R.id.STAT_companyName);
		txtDate   = (TextView) view.findViewById(R.id.STAT_date);
		txtLinks = (TextView) view.findViewById(R.id.STAT_links);
	}

	
	private CompanyStatsDTO companyStats;
	
	
	private TextView txtAdmins, txtAuthors, txtInst, txtTrainees, txtClasses, txtCategories, txtCourses,
	txtLessons, txtActivities, txtTraineeRatings, txtInstRatings, txtLinks, txtTraineeActs,
	txtCompany, txtDate;
	static final DecimalFormat df = new DecimalFormat("###,###,###,###,###");
	static final Locale loc = Locale.getDefault();
	static final SimpleDateFormat sdf = new SimpleDateFormat("EE dd-MM-yyyy HH:mm:ss", loc);

	
}
