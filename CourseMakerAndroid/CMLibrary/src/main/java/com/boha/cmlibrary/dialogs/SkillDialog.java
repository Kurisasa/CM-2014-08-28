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
import com.boha.coursemaker.dto.SkillDTO;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

/**
 * Created by aubreyM on 2014/07/05.
 */
public class SkillDialog extends DialogFragment {
    public SkillDialog() {
    }
    public interface SkillDialogListener {
        public void onSkillComplete(ResponseDTO response);
    }

    SkillDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "onCreateView of Dialog");
        dialog = this;
        dialog.setCancelable(true);
        dialog.getDialog().setTitle("Add/Update Skill Type");
        final View view = inflater.inflate(R.layout.skill_template, container);
        editName = (EditText)view.findViewById(R.id.SKV_editName);

        btnCancel = (Button)view.findViewById(R.id.SKV_btnCancel);
        btnSend = (Button)view.findViewById(R.id.SKV_btnSave);
        if (skill.getSkillID() > 0) {
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
            ToastUtil.errorToast(ctx,ctx.getResources().getString(R.string.enter_name));
            return;
        }
        RequestDTO w = new RequestDTO();
        if (isUpdate) {
            w.setRequestType(RequestDTO.UPDATE_COMPANY_SKILL);
            w.setSkill(skill);
        } else {
            w.setRequestType(RequestDTO.ADD_COMPANY_SKILL);
            SkillDTO d = new SkillDTO();
            d.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
            d.setSkillName(editName.getText().toString());
            w.setSkill(d);
        }

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
    public void setSkill(SkillDTO level) {
        this.skill = level;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setListener(SkillDialogListener listener) {
        this.listener = listener;
    }

    SkillDialogListener listener;
    boolean isUpdate;
    SkillDTO skill;
    static final String LOG = SkillDialog.class.getName();
    EditText editName;
    Button btnCancel, btnSend;
    Context ctx;
    ProgressBar bar;
}
