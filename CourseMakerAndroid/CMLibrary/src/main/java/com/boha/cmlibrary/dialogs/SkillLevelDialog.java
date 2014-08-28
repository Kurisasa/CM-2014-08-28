package com.boha.cmlibrary.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.SkillLevelDTO;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

/**
 * Created by aubreyM on 2014/07/05.
 */
public class SkillLevelDialog extends DialogFragment {
    public SkillLevelDialog() {
    }
    public interface SkillLevelDialogListener {
        public void onSkillLevelComplete(ResponseDTO response);
    }

    SkillLevelDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView of Dialog");
        dialog = this;
        dialog.setCancelable(true);
        dialog.getDialog().setTitle("Add/Update Skill Level");
        final View view = inflater.inflate(R.layout.skill_level_template, container);
        editName = (EditText)view.findViewById(R.id.SKV_editName);
        editLevel = (EditText)view.findViewById(R.id.SKV_editLevel);

        btnCancel = (Button)view.findViewById(R.id.SKV_btnCancel);
        btnSend = (Button)view.findViewById(R.id.SKV_btnSave);
        if (level.getSkillLevelID() > 0) {
            isUpdate = true;
        }
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
        return view;
    }

    private void submit() {
        if (editName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_name));
            return;
        }
        if (editLevel.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.enter_level));
            return;
        }
        RequestDTO w = new RequestDTO();
        if (isUpdate) {
            isUpdate = false;
            w.setRequestType(RequestDTO.UPDATE_COMPANY_SKILL_LEVEL);
            level.setSkillLevelName(editName.getText().toString());
            level.setLevel(Integer.parseInt(editLevel.getText().toString()));
            w.setSkillLevel(level);
        } else {
            w.setRequestType(RequestDTO.ADD_COMPANY_SKILL_LEVEL);
            SkillLevelDTO d = new SkillLevelDTO();
            d.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
            d.setSkillLevelName(editName.getText().toString());
            d.setLevel(Integer.parseInt(editLevel.getText().toString()));
            w.setSkillLevel(d);
        }

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        //TODO set busy
        WebSocketUtil.sendRequest(ctx, Statics.INSTRUCTOR_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        listener.onSkillLevelComplete(r);
                        dialog.dismiss();
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
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });

    }
    public void setSkillLevel(SkillLevelDTO level) {
        this.level = level;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setListener(SkillLevelDialogListener listener) {
        this.listener = listener;
    }

    SkillLevelDialogListener listener;
    boolean isUpdate;
    SkillLevelDTO level;
    static final String LOG = SkillLevelDialog.class.getName();
    EditText editName, editLevel;
    Button btnCancel, btnSend;
    Context ctx;
    ProgressBar bar;
}
