package com.boha.cmlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmlibrary.ActivityListActivity;
import com.boha.cmlibrary.ActivityListListener;
import com.boha.cmlibrary.CMApp;
import com.boha.cmlibrary.dialogs.HelpRequestDialog;
import com.boha.cmlibrary.R;
import com.boha.cmlibrary.adapters.ActivityAdapter;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivityPageFragment extends Fragment implements PageInterface {

    public ActivityPageFragment() {
    }

    ActivityListListener activityListListener;
    Context ctx;
    View view;

    @Override
    public void onAttach(Activity a) {

        if (a instanceof ActivityListListener) {
            activityListListener = (ActivityListListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() + " must implement ActivityCompletedListener");
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
        Log.w(LOG, "############################# onCreateView");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_lesson_page, container, false);
        CMApp app = (CMApp) getActivity().getApplication();
        imageLoader = app.getImageLoader();
        Bundle b = getArguments();
        if (b != null) {
            trainingClassCourse = (TrainingClassCourseDTO) b.getSerializable("course");
            trainee = (TraineeDTO) b.getSerializable("trainee");
            type = b.getInt("type", ActivityListActivity.INSTRUCTOR);
            setFields();
            setList();
        } else {
            setFields();
        }


        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "#onSaveInstanceState,  ");
        b.putSerializable("activity", activity);
        super.onSaveInstanceState(b);
    }

    private void setList() {
        boolean hideHelp = false;
        if (type == ActivityListActivity.INSTRUCTOR) {
            hideHelp = true;
        }
        adapter = new ActivityAdapter(getActivity(), R.layout.activity_item,
                trainingClassCourse.getCourseTraineeActivityList(), false,
                hideHelp, new ActivityAdapter.ActivityAdapterListener() {
            @Override
            public void onRatingRequested(CourseTraineeActivityDTO cta) {
                activityListListener.onRatingRequested(cta, type);
            }

            @Override
            public void onHelpRequested(CourseTraineeActivityDTO cta) {
                if (type == ActivityListActivity.TRAINEE) {
                    HelpRequestDialog d = new HelpRequestDialog();
                    d.setCtx(ctx);
                    d.setCourseTraineeActivity(cta);
                    d.show(getFragmentManager(), "HELP_DIAG");
                }
                if (type == ActivityListActivity.INSTRUCTOR) {
                }
            }
        });
        listView.setAdapter(adapter);
        actCount.setText("" + trainingClassCourse.getCourseTraineeActivityList().size());
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //activityListListener.onRatingRequested(trainingClassCourse.getCourseTraineeActivityList().get(arg2), type);
            }
        });
        upDateTotals();
    }

    private void upDateTotals() {
        Numbers n = calculate();
        txtCompleted.setText(df.format(n.getPercentage()) + "%");
        txtNumbers.setText("" + n.completed + " of " + n.total);

    }

    private Numbers calculate() {
        Numbers s = new Numbers();
        for (CourseTraineeActivityDTO c : trainingClassCourse.getCourseTraineeActivityList()) {
            if (c.getCompletedFlag() > 0) {
                s.completed++;
            }
        }
        s.total = trainingClassCourse.getCourseTraineeActivityList().size();
        return s;
    }

    TrainingClassCourseDTO trainingClassCourse;

    public void setTrainingClassCourse(TrainingClassCourseDTO trainingClassCourse, TraineeDTO trainee, int type) {
        Log.w(LOG, "############## setTrainingClassCourse");
        if (trainingClassCourse == null) {
            throw new UnsupportedOperationException("trainingClassCourse is NULL");
        }
        if (trainingClassCourse.getCourseTraineeActivityList() == null) {
            throw new UnsupportedOperationException("trainingClassCourseList is NULL");
        }
        this.trainingClassCourse = trainingClassCourse;
        this.trainee = trainee;
        this.type = type;

        setHeader();
        setList();
        upDateTotals();

    }


    static final DecimalFormat df = new DecimalFormat("###,###,##0.00");

    class Numbers {
        int completed, total;
        double perc;

        public double getPercentage() {
            double p = 0;
            if (total == 0)
                return p;

            Double dActivities = Double.valueOf("" + total);
            Double dCompleted = Double.valueOf("" + completed);
            Double pc = (dCompleted / dActivities) * 100;
            return pc.doubleValue();
        }
    }

    public void refresh(CourseTraineeActivityDTO cta) {
        Log.e(LOG, "-------------- refreshing cta ........");
        int i = 0;
        List<CourseTraineeActivityDTO> list = new ArrayList<>();

        for (CourseTraineeActivityDTO dto : trainingClassCourse.getCourseTraineeActivityList()) {
            if (dto.getCourseTraineeActivityID() == cta.getCourseTraineeActivityID()) {
                list.add(cta);
            } else {
                list.add(dto);
            }
        }
        for (CourseTraineeActivityDTO dto: list) {
            if (dto.getCourseTraineeActivityID() == cta.getCourseTraineeActivityID()) {
                break;
            }
            i++;
        }
        trainingClassCourse.setCourseTraineeActivityList(list);
        setList();
        upDateTotals();
        listView.setSelection(i);


        if (cta.getCompletedFlag() > 0) {
            trainee.setTotalCompleted(trainee.getTotalCompleted() + 1);
            setHeader();
            Log.i(LOG, "telling activityCompletedListener of completion, index: " + i);
            activityListListener.onActivityCompleted(cta);
        }


    }

    private void setFields() {

        txtName = (TextView) view.findViewById(R.id.TH_txtTraineeName);
        txtClassName = (TextView) view.findViewById(R.id.TH_txtCourseName);
        txtCompletedHeader = (TextView) view.findViewById(R.id.TH_txtPerc);
        txtCompleted = (TextView) view.findViewById(R.id.LP_completed);
        txtNumbers = (TextView) view.findViewById(R.id.LP_numbers);
        actCount = (TextView) view.findViewById(R.id.LP_actCount);

        listView = (ListView) view.findViewById(R.id.LP_list);
        image = (NetworkImageView) view.findViewById(R.id.TH_image);

        setHeader();
    }

    private void setHeader() {
        if (txtName != null && trainee != null) {
            txtName.setText(trainee.getFullName());
            txtClassName.setText(trainee.getTrainingClassName());
            txtCompletedHeader.setText(df.format(trainee.getPercComplete()) + "%");
            StringBuilder sb = new StringBuilder();
            sb.append(Statics.IMAGE_URL).append("company")
                    .append(trainee.getCompanyID()).append("/trainee/");
            sb.append(trainee.getTraineeID()).append(".jpg");
            image.setDefaultImageResId(R.drawable.boy);
            image.setImageUrl(sb.toString(), imageLoader);
        }
    }

    ListView listView;
    TextView actCount, txtClassName, txtName, txtCompleted, txtCompletedHeader, txtNumbers;
    ActivityDTO activity;
    Button btnObj, btnRes;
    NetworkImageView image;
    TraineeDTO trainee;
    ImageLoader imageLoader;
    int type;
    ActivityAdapter adapter;
    static final String LOG = "ActivityPageFragment";

}
