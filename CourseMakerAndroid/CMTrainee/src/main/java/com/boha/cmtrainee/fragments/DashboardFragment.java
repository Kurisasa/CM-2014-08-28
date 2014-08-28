package com.boha.cmtrainee.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.boha.cmtrainee.R;
import com.boha.coursemaker.dto.*;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.Summary;
import com.boha.coursemaker.util.ToastUtil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment implements PageInterface {
    Context ctx;
    View view;
    BusyListener busyListener;

    @Override
    public void onAttach(Activity a) {
        Log.d(LOG, "---- onAttach");
        if (a instanceof BusyListener) {
            busyListener = (BusyListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host activity must implement BusyListener");
        }

        super.onAttach(a);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.i(LOG, "-- onCreateView ............");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_dashboard, container,
                false);
        trainee = SharedUtil.getTrainee(ctx);
        trainingClass = SharedUtil.getTrainingClass(ctx);
        setFields();
        Bundle b = getArguments();
        if (b != null) {
            response = (ResponseDTO) b.getSerializable("response");
            if (response != null) {
                getActivities();
                Log.w(LOG, "Trainee activities found: " + courseTraineeActivityList.size());
                setData();
            }
        }
        return view;
    }

    private void getActivities() {
        courseTraineeActivityList = new ArrayList<CourseTraineeActivityDTO>();
        for (TrainingClassCourseDTO tcc : response.getTrainingClassCourseList()) {
            courseTraineeActivityList.addAll(tcc.getCourseTraineeActivityList());
        }
    }

    private void setData() {
        Log.i(LOG, "*** setData from response object ");
        setSummary();
        txtCourses.setText(dfNoDecimal
                .format(summary.getNumberOfCourses()));
        txtActiv.setText(dfNoDecimal
                .format(summary.getNumberOfActivites()));

        txtNumberComplete.setText(dfNoDecimal.format(summary
                .getNumberOfCompletedActivities()));

        txtPerc.setText(df.format(summary.getPercCompleted()) + "%");
        bar.setProgress(((int) summary.getPercCompleted()));

        Statics.setRobotoFontBold(ctx, txtPerc);
        Statics.setRobotoFontBold(ctx, txtActiv);
        Statics.setRobotoFontBold(ctx, txtCourses);
        Statics.setRobotoFontBold(ctx, txtNumberComplete);

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(1000);
        a.setAnimationListener(new AnimationListener() {

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
                Animation b = AnimationUtils.loadAnimation(ctx, R.anim.xgrow_fade);
                b.setDuration(500);
                txtPerc.startAnimation(b);

            }
        });
        view.startAnimation(a);

    }

    private void setFields() {

        TextView txtTrainee = (TextView) view.findViewById(R.id.MAIN_trainee);
        String name = trainee.getFirstName() + " " + trainee.getLastName();
        txtTrainee.setText(name);
        Statics.setRobotoFontRegular(ctx, txtTrainee);
        TextView txtClass = (TextView) view.findViewById(R.id.MAIN_class);
        txtClass.setText(trainingClass.getTrainingClassName());
        Statics.setRobotoFontRegular(ctx, txtClass);

        txtCourses = (TextView) view.findViewById(R.id.MAIN_courses);
        txtActiv = (TextView) view.findViewById(R.id.MAIN_activities);
        txtPerc = (TextView) view.findViewById(R.id.MAIN_percComplete);
        txtNumberComplete = (TextView) view
                .findViewById(R.id.MAIN_numberComplete);
        bar = (ProgressBar) view.findViewById(R.id.MAIN_percProgress);
        txtLastDate = (TextView) view.findViewById(R.id.MAIN_lastDate);
        Date dt = SharedUtil.getLastCompletionDate(ctx);
        if (dt != null) {
            txtLastDate.setText(sdf.format(dt));
        } else {
            txtLastDate.setText(ctx.getResources().getString(R.string.no_completion_date));
        }
        Statics.setRobotoFontRegular(ctx, txtPerc);
        Statics.setRobotoFontBold(ctx, txtTrainee);
        Statics.setRobotoFontBold(ctx, txtPerc);
        txtPerc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ToastUtil.toast(ctx,"Details of completions. Under Construction");
            }
        });

    }

    static final Locale locale = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", locale);

    private void setSummary() {
        summary = new Summary();
        if (courseTraineeActivityList == null) {
            return;
        }
        summary.setNumberOfActivites(courseTraineeActivityList.size());
        summary.setNumberOfCourses(response.getTrainingClassCourseList().size());
        int cnt = 0, cnt2 = 0;
        for (CourseTraineeActivityDTO cta : courseTraineeActivityList) {
            if (cta.getRating() != null) {
                cnt++;
            }

                if (cta.getCompletedFlag() > 0) {
                    cnt2++;
                }

        }
        summary.setNumberOfRatings(cnt);
        summary.setNumberOfCompletedActivities(cnt2);

        if (summary.getNumberOfActivites() > 0) {
            Double dActivities = Double.valueOf(""
                    + summary.getNumberOfActivites());
            Double dCompleted = Double.valueOf(""
                    + summary.getNumberOfCompletedActivities());

            Double pc = (dCompleted / dActivities) * 100;
            summary.setPercCompleted(pc.doubleValue());
        }


    }


    private Summary summary;
    private TextView txtPerc, txtCourses, txtActiv, txtNumberComplete, txtLastDate;
    static final String LOG = "DashboardFragment";
    private ResponseDTO response;
    private ProgressBar bar;
    private TraineeDTO trainee;
    Gson gson = new Gson();
    private List<CourseTraineeActivityDTO> courseTraineeActivityList;
    private TrainingClassDTO trainingClass;
    static final int START_COURSE_LIST = 3;
    public static final DecimalFormat df = new DecimalFormat("###,###,###.0");
    public static final DecimalFormat dfNoDecimal = new DecimalFormat(
            "###,###,###,###,###");
}
