package com.boha.cmlibrary.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.SkillDTO;
import com.boha.coursemaker.dto.SkillLevelDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeSkillDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/07/05.
 */
public class TraineeSkillDialog extends DialogFragment {
    public TraineeSkillDialog() {
    }
    public interface SkillDialogListener {
        public void onSkillComplete(ResponseDTO response);
    }

    TraineeSkillDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView of Dialog");
        dialog = this;
        dialog.setCancelable(true);
        dialog.getDialog().setTitle("Add/Update Trainee Skill");
        final View view = inflater.inflate(R.layout.add_skill_dialog, container);
        txtSkillName = (TextView)view.findViewById(R.id.ASD_txtSkillName);
        spinner = (Spinner)view.findViewById(R.id.ASD_spinner);
        txtSkillName.setText(skill.getSkillName());

        btnCancel = (Button)view.findViewById(R.id.ASD_btnCancel);
        btnSend = (Button)view.findViewById(R.id.ASD_btnSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        getLookups();
        return view;
    }

    private void submit() {
        if (skillLevel == null) {
            ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.select_something));
            return;
        }
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.ADD_TRAINEE_SKILLS);
        TraineeSkillDTO ts = new TraineeSkillDTO();
        ts.setInstructorID(SharedUtil.getInstructor(ctx).getInstructorID());
        ts.setTraineeID(trainee.getTraineeID());
        ts.setSkillID(skill.getSkillID());
        ts.setSkillLevelID(skillLevel.getSkillLevelID());
        ts.setLevel(skillLevel.getLevel());
        w.setTraineeSkillList(new ArrayList<TraineeSkillDTO>());
        w.getTraineeSkillList().add(ts);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        //TODO set busy
        WebSocketUtil.sendRequest(ctx, Statics.INSTRUCTOR_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx,r.getMessage());
                            return;
                        }
                        listener.onSkillComplete(r);
                        dismiss();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });

    }
    private void getLookups() {
        CacheUtil.getCachedData(ctx,CacheUtil.CACHE_TRAINEE_ACTIVITY,new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    skillLevelList = response.getSkillLevelList();
                    setSpinner();
                }
            }

            @Override
            public void onDataCached() {

            }
        });
    }
    List<SkillLevelDTO> skillLevelList;
    SkillLevelDTO skillLevel;
    boolean isFirstTime;
    private void setSpinner() {
        List<String> stringList = new ArrayList<>();
        stringList.add(ctx.getResources().getString(R.string.select_something));
        for (SkillLevelDTO s: skillLevelList) {
            stringList.add(s.getSkillLevelName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_dropdown_item, stringList);
        adapter.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    skillLevel = null;
                } else {
                    skillLevel = (skillLevelList.get(i-1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (skill.getSkillLevel() != null) {
            int index = 0;
            for (SkillLevelDTO sl: skillLevelList) {
                if (sl.getSkillLevelID() == skill.getSkillLevel().getSkillLevelID()) {
                    spinner.setSelection(index + 1);
                    break;
                }
                index++;
            }
        }
    }
    public void setSkill(SkillDTO s) {
        this.skill = s;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setListener(SkillDialogListener listener) {
        this.listener = listener;
    }

    public void setTrainee(TraineeDTO trainee) {
        this.trainee = trainee;
    }

    SkillDialogListener listener;
    SkillDTO skill;
    TraineeDTO trainee;
    static final String LOG = TraineeSkillDialog.class.getName();
    Spinner spinner;
    Button btnCancel, btnSend;
    TextView txtSkillName;
    Context ctx;
}
