package com.boha.cminstructor.fragments;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import com.boha.cminstructor.R;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TotalsDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DashboardFragment extends Fragment implements PageInterface {

	public DashboardFragment() {
	}

	static final String LOG = "DashboardFragment";

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		Log.e(LOG, "--- onCreateView");
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_dashboard, container, false);
		company = SharedUtil.getCompany(ctx);

		Bundle b = getArguments();
		response = (ResponseDTO) b.getSerializable("response");
		instructor = response.getInstructor();

        setFields();
		setTotalFields();
		return view;
	}

    public void refresh(ResponseDTO r) {
        response = r;
        isRefreshing = true;
        setTotalFields();
        animatePercentage();
    }
    private void summarise() {

    }

	@Override
	public void onResume() {

		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...not saving...");
		super.onSaveInstanceState(state);
	}

	private void setFields() {
		TextView ins = (TextView) view.findViewById(R.id.MAIN_instructor);
		ins.setText(instructor.getFirstName() + " " + instructor.getLastName());
		txtClasses = (TextView) view.findViewById(R.id.MAIN_classes);
		txtCourses = (TextView) view.findViewById(R.id.MAIN_courses);
		txtActiv = (TextView) view.findViewById(R.id.MAIN_activities);
		txtPerc = (TextView) view.findViewById(R.id.MAIN_percComplete);
		txtNumberComplete = (TextView) view
				.findViewById(R.id.MAIN_numberComplete);
		bar = (ProgressBar) view.findViewById(R.id.MAIN_percProgress);
		txtLastDate = (TextView) view.findViewById(R.id.MAIN_lastDate);

		Button btn = (Button) view.findViewById(R.id.MAIN_btnStart);
		btn.setVisibility(View.GONE);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		txtPerc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO - activity to view trainee list of course completions
				if (totComplete > 0) {

				}
			}
		});

	}

	Context ctx;
	View view;
	InstructorDTO instructor;
	int totClass, totCourse, totActivity, totComplete;
	ProgressBar bar;
	TextView txtPerc, txtCourses, txtClasses, txtActiv, txtNumberComplete,
			txtLastDate;

	private void setLocalTotals(int totCourse) {
		if (instructor != null) {
			this.totClass = instructor.getInstructorClassList().size();
			this.totCourse = totCourse;
			txtCourses.setText("" + totCourse);
			txtClasses.setText("" + totClass);
		}

	}

    boolean isRefreshing;
	private void setTotalFields() {
        setLocalTotals(response.getTotalCourses());
		int totalTasks = 0, totalComplete = 0;

		for (TotalsDTO t : response.getTotals()) {
			totalTasks += t.getTotalTasks();
			totalComplete += t.getTotalComplete();
		}
		if (txtActiv == null || txtNumberComplete == null)
			return;
		txtActiv.setText(df2.format(totalTasks));
		txtNumberComplete.setText(df2.format(totalComplete));

		if (totalTasks > 0) {
			BigDecimal bTasks = new BigDecimal(totalTasks);
			BigDecimal bComp = new BigDecimal(totalComplete);
			BigDecimal perc = bComp.divide(bTasks, 4, RoundingMode.HALF_EVEN)
					.multiply(new BigDecimal(100));
			txtPerc.setText(df.format(perc.doubleValue()) + " %");
			bar.setProgress(perc.intValue());
		} else {
			txtPerc.setText("0.00%");
		}
        if (isRefreshing) {
            isRefreshing = false;
        } else {
            Animation a = AnimationUtils.loadAnimation(ctx,
                    R.anim.grow_fade_in_center);
            a.setDuration(1000);
            a.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animatePercentage();

                }
            });
            view.startAnimation(a);
        }
	}

	private void animatePercentage() {
		Animation a = AnimationUtils.loadAnimation(ctx,
				R.anim.xgrow_fade);
		a.setDuration(500);
		a.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Animation b = AnimationUtils.loadAnimation(ctx,
						R.anim.xgrow_fade);
				b.setDuration(500);
				//txtPerc.startAnimation(b);

			}
		});
		txtPerc.startAnimation(a);
		Animation x = AnimationUtils.makeInAnimation(ctx, true);
		x.setDuration(1000);
		//bar.startAnimation(x);
	}

	ResponseDTO response;
	CompanyDTO company;
	static final DecimalFormat df = new DecimalFormat("###,###,##0.00");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###");
}
