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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.HelpRequestDTO;
import com.boha.coursemaker.dto.HelpTypeDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aubreyM on 2014/07/05.
 */
public class HelpRequestDialog extends DialogFragment {
    public HelpRequestDialog() {
    }

    HelpRequestDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView of Dialog");
        dialog = this;
        dialog.setCancelable(true);
        dialog.getDialog().setTitle(courseTraineeActivity.getCourseName());
        final View view = inflater.inflate(R.layout.help_dialog, container);
        txtActivity = (TextView)view.findViewById(R.id.HD_txtActName);
        txtCourse = (TextView)view.findViewById(R.id.HD_txtCourse);
        txtDesc = (TextView)view.findViewById(R.id.HD_txtActDesc);
        btnCancel = (Button)view.findViewById(R.id.HD_btnCancel);
        btnSend = (Button)view.findViewById(R.id.HD_btnSend);
        spinner = (Spinner) view.findViewById(R.id.HD_spinner);

        bar = (ProgressBar)view.findViewById(R.id.progressBar);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHelpRequest();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        getHelpTypes();
        txtActivity.setText(courseTraineeActivity.getActivity().getActivityName());
        txtCourse.setText(courseTraineeActivity.getCourseName());
        txtDesc.setText(courseTraineeActivity.getActivity().getDescription());
        return view;
    }

    private void sendHelpRequest() {

        if (helpType == null) {
            ToastUtil.toast(ctx,ctx.getResources().getString(R.string.select_helptype));
            return;
        }

        HelpRequestDTO d = new HelpRequestDTO();
        CourseTraineeActivityDTO x = new CourseTraineeActivityDTO();
        x.setCourseTraineeActivityID(courseTraineeActivity.getCourseTraineeActivityID());
        d.setCourseTraineeActivity(x);
        d.setHelpType(helpType);
        //d.setComment(eComment.getText().toString());

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GCM_SEND_TRAINEE_TO_INSTRUCTOR_MSG);
        w.setHelpRequest(d);
        w.setTrainingClassID(courseTraineeActivity.getTrainingClassID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

        bar.setVisibility(View.VISIBLE);
        WebSocketUtil.sendRequest(ctx,Statics.TRAINEE_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bar.setVisibility(View.GONE);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.toast(ctx, response.getMessage());
                            return;
                        }
                        dialog.dismiss();
                        ToastUtil.toast(ctx,ctx.getResources().getString(R.string.help_request_sent));
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
                        bar.setVisibility(View.GONE);
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//        BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE,w,ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                bar.setVisibility(View.GONE);
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.toast(ctx, response.getMessage());
//                    return;
//                }
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                bar.setVisibility(View.GONE);
//                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//            }
//        });
    }

    public CourseTraineeActivityDTO getCourseTraineeActivity() {
        return courseTraineeActivity;
    }

    public void setCourseTraineeActivity(CourseTraineeActivityDTO courseTraineeActivity) {
        this.courseTraineeActivity = courseTraineeActivity;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private void getHelpTypes() {
        CacheUtil.getCachedData(ctx,CacheUtil.CACHE_TRAINEE_ACTIVITY, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    helpTypeList = response.getHelpTypeList();
                    setHelpSpinner();
                }
            }

            @Override
            public void onDataCached() {

            }
        });
    }
    private void setHelpSpinner() {
        List<String> strings = new ArrayList<String>();
        strings.add(ctx.getResources().getString(R.string.select_helptype));
        for (HelpTypeDTO h: helpTypeList) {
            strings.add(h.getHelpTypeName());
        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(ctx, R.layout.xxsimple_spinner_item, strings);
        a.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);

        spinner.setAdapter(a);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    helpType = null;
                    return;
                }
                helpType = helpTypeList.get(i-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    static final String LOG = "HelpRequestDialog";
    TextView txtCourse, txtActivity, txtDesc;
    Button btnCancel, btnSend;
    EditText eComment;
    CourseTraineeActivityDTO courseTraineeActivity;
    Spinner spinner;
    List<HelpTypeDTO> helpTypeList;
    HelpTypeDTO helpType;
    Context ctx;
    ProgressBar bar;
}
