package com.boha.reporter.coursemaker.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.reporter.coursemaker.R;

import java.text.DecimalFormat;

/**
 * Created by aubreyM on 2014/04/09.
 */
public class ServerLogFragment extends Fragment implements PageFragment {


    @Override
    public void onAttach(Activity a) {
        Log.i(LOG,
                "onAttach ---- Fragment called and hosted by "
                        + a.getLocalClassName()
        );
        super.onAttach(a);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e(LOG, "onCreateView.........");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.fragment_log, container, false);

        fragmentManager = getFragmentManager();
        setFields();
        if (response != null) {
            Log.e(LOG, "response not null in onCreateView");
            setList();
            return view;
        }
        if (saved != null) {
            Log.i(LOG, "onCreateView - getting saved response");
            response = (StatsResponseDTO) saved.getSerializable("response");
        } else {
            Bundle bundle = getArguments();
            if (bundle != null) {
                response = (StatsResponseDTO) bundle.getSerializable("response");
            }
        }
        Log.e(LOG, "about to setList in onCreateView");
        setList();

        return view;
    }



    @Override
    public void onSaveInstanceState(Bundle b) {
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    public void setFields() {
        Log.e(LOG, "setFields.........");
        txtLog = (TextView) view.findViewById(R.id.LOG_log);
        txtCount = (TextView) view.findViewById(R.id.LOG_count);


    }

    public void setList() {
        if (response.getLogString() == null) {
            Log.w(LOG,"--------- getLogString is NULL");
            return;
        }
        txtLog.setText(response.getLogString());
        txtCount.setText(df.format(response.getLogString().length()));

    }


    static final DecimalFormat df = new DecimalFormat("###,###,###,###,###,###");
    FragmentManager fragmentManager;
    TextView txtLog, txtCount;

    static final String LOG = "ServerLogFrag";
    Context ctx;
    View view;
    StatsResponseDTO response;


}
