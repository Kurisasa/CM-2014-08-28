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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.boha.cmlibrary.R;
import com.boha.cmlibrary.adapters.TraineeSkillAdapter;
import com.boha.cmlibrary.dialogs.TraineeSkillDialog;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.SkillDTO;
import com.boha.coursemaker.dto.SkillLevelDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeSkillDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TraineeSkillsFragment extends Fragment implements PageInterface {

    public TraineeSkillsFragment() {
    }

    public interface TraineeSkillsFragmentListener {
        public void onAddSkillsRequest(TraineeDTO trainee);
    }

    static final String LOG = TraineeSkillsFragment.class.getName();
    TraineeSkillsFragmentListener listener;
    @Override
    public void onAttach(Activity a) {

        if (a instanceof TraineeSkillsFragmentListener) {
            listener = (TraineeSkillsFragmentListener)a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() + " must implement TraineeSkillsFragmentListener");
        }
        Log.e(LOG,"Hosted by " + a.getLocalClassName());
        super.onAttach(a);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.i(LOG,".......................onCreateView");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_trainee_skill_list, container, false);
        setFields();

        Bundle b = getArguments();
        if (b != null) {
            trainee = (TraineeDTO)b.getSerializable("trainee");
            imageNew.setVisibility(View.GONE);
            disableDialog = true;
            setTrainee(trainee);
        }


        return view;
    }

    @Override
    public void onResume() {
        Log.e(LOG, "############### resuming in " + LOG);
        super.onResume();
    }



    @Override
    public void onSaveInstanceState(Bundle state) {
        Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
        super.onSaveInstanceState(state);
    }


    private void setFields() {
        txtCount = (TextView) view.findViewById(R.id.TRSK_count);
        imageNew = (ImageView) view.findViewById(R.id.TRSK_imgNew);
        listView = (ListView) view.findViewById(R.id.TRSK_listView);

        imageNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onAddSkillsRequest(trainee);
            }
        });
    }

    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm", loc);


    public void setTrainee(TraineeDTO trainee) {
        if (trainee.getTraineeSkillList() == null) {
            trainee.setTraineeSkillList(new ArrayList<TraineeSkillDTO>());
        }
        Log.e(LOG,"########### setTrainee, skills: " + trainee.getTraineeSkillList().size());
        this.trainee = trainee;
        setList();


    }
    private void setList() {
        Log.i(LOG,"############# setList, list: " + trainee.getTraineeSkillList().size());
        adapter = new TraineeSkillAdapter(ctx,R.layout.trainee_skill_item, trainee.getTraineeSkillList());
        listView.setAdapter(adapter);
        txtCount.setText("" + trainee.getTraineeSkillList().size());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (disableDialog) return;
                TraineeSkillDialog d = new TraineeSkillDialog();
                d.setContext(ctx);
                d.setTrainee(trainee);
                SkillDTO skill = new SkillDTO();
                skill.setSkillID(trainee.getTraineeSkillList().get(i).getSkillID());
                skill.setSkillName(trainee.getTraineeSkillList().get(i).getSkillName());
                SkillLevelDTO sl = new SkillLevelDTO();
                sl.setLevel(trainee.getTraineeSkillList().get(i).getLevel());
                sl.setSkillLevelID(trainee.getTraineeSkillList().get(i).getSkillLevelID());
                skill.setSkillLevel(sl);
                d.setSkill(skill);
                d.setListener(new TraineeSkillDialog.SkillDialogListener() {
                    @Override
                    public void onSkillComplete(ResponseDTO response) {
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx,response.getMessage());
                            return;
                        }
                        trainee.setTraineeSkillList(response.getTraineeSkillList());
                        setTrainee(trainee);
                    }
                });
                d.show(getFragmentManager(), "HBVC");
            }
        });
    }
    boolean disableDialog;
    Context ctx;
    View view;
    TextView txtTrainee, txtCount;
    ImageView imageNew;
    NetworkImageView imageView;
    ImageLoader imageLoader;
    TraineeDTO trainee;
    ListView listView;
    TraineeSkillAdapter adapter;
}
