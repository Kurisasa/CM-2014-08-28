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
import com.boha.cmtrainee.adapters.CourseAdapter;
import com.boha.cmtrainee.interfaces.CourseListener;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class TraineeCourseListFragment extends Fragment implements PageInterface {

    public TraineeCourseListFragment() {
    }

    @Override
    public void onAttach(Activity a) {
        if (a instanceof CourseListener) {
            courseListener = (CourseListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
                    " must implement CourseListener");
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
        trainee = SharedUtil.getTrainee(ctx);
        trainingClass = SharedUtil.getTrainingClass(ctx);
        setFields();
        if (saved != null) {
            Log.i(LOG, "saved is not null");
            response = (ResponseDTO) saved.getSerializable("response");
            trainingClassCourseList = response.getTrainingClassCourseList();
            setList();
        }

        Bundle b = getArguments();
        if (b != null) {
            response = (ResponseDTO) b.getSerializable("response");
            if (response != null) {
                trainingClassCourseList = response.getTrainingClassCourseList();
                setList();
            }
        }

        return view;
    }

    ResponseDTO response;

    @Override
    public void onResume() {
        Log.e(LOG, "## onResume");

        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        Log.i(LOG, "##### onSaveInstanceState  fired ....");
        if (response != null) {
            state.putSerializable("response", response);
        }
        super.onSaveInstanceState(state);
    }

    private void setFields() {
        txtClassName = (TextView) view.findViewById(R.id.CRS_className);
        txtClassName.setText(trainingClass.getTrainingClassName());
        txtTrainee = (TextView) view.findViewById(R.id.CRS_trainee);
        listView = (ListView) view.findViewById(R.id.CRS_listView);
        txtCount = (TextView) view.findViewById(R.id.LH_count);
        TextView txt = (TextView) view.findViewById(R.id.LH_label);
        Statics.setRobotoFontRegular(ctx,txt);
        Statics.setRobotoFontRegular(ctx,txtClassName);


    }

    List<TrainingClassCourseDTO> trainingClassCourseList;
    private void setList() {
        if (getActivity() == null) {
            Log.e(LOG, "Context is NULL. Somethin weird going down ...");
            return;
        }

        adapter = new CourseAdapter(getActivity(), R.layout.course_item,
                trainingClassCourseList);

        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        txtCount.setText("" + trainingClassCourseList.size());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                trainingClassCourse = trainingClassCourseList.get(
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

    TraineeDTO trainee;
    TrainingClassDTO trainingClass;
    TrainingClassCourseDTO trainingClassCourse;
    private CourseListener courseListener;
    Context ctx;
    View view;
    //Container data;

    CourseAdapter adapter;
    ListView listView;
    TextView txtClassName, txtTrainee, txtCount;
    static final String LOG = "TraineeCourseListFragment";

}
