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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmlibrary.dialogs.TraineeSkillDialog;
import com.boha.cmlibrary.R;
import com.boha.cmlibrary.adapters.CompanySkillAdapter;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.SkillDTO;
import com.boha.coursemaker.dto.SkillLevelDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeSkillDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that manages the profiles of Administratiors, Authors and Instructors. It manages the User Interface to edit
 * the data and manages the communications to the cloud server
 *
 * @author aubreyM
 * @see com.boha.coursemaker.dto.AdministratorDTO
 * @see com.boha.coursemaker.dto.AuthorDTO
 * @see com.boha.coursemaker.dto.InstructorDTO
 */
public class CompanySkillsFragment extends Fragment {

    public CompanySkillsFragment() {
    }

    static final String LOG = CompanySkillsFragment.class.getName();

    public interface CompanySkillsFragmentListener {
        public void onSkillsAdded(List<TraineeSkillDTO> skills);
    }

    CompanySkillsFragmentListener listener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof CompanySkillsFragmentListener) {
            listener = (CompanySkillsFragmentListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host " + a.getLocalClassName() + " must implement CompanySkillsFragmentListener");
        }
        Log.e(LOG, "Fragment hosted by " + a.getLocalClassName());
        super.onAttach(a);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.w(LOG, "onCreateView ...");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_company_skills, container, false);
        setFields();

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
        listView = (ListView) view.findViewById(R.id.FC_listView);
        txtCount = (TextView) view.findViewById(R.id.FC_txtCount);

    }

    private void setList() {
        Log.w(LOG,"########### setList");
        adapter = new CompanySkillAdapter(ctx, R.layout.company_skill_item,
                skillList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TraineeSkillDialog dialog = new TraineeSkillDialog();
                dialog.setContext(ctx);
                dialog.setTrainee(trainee);
                dialog.setSkill(skillList.get(i));
                dialog.setListener(new TraineeSkillDialog.SkillDialogListener() {
                    @Override
                    public void onSkillComplete(ResponseDTO response) {
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx,response.getMessage());
                            return;
                        }
                        listener.onSkillsAdded(response.getTraineeSkillList());
                    }
                });
                dialog.show(getFragmentManager(),"GRD_DIAG");
            }
        });
        txtCount.setText("" + skillList.size());
    }



    public void setTrainee(final TraineeDTO trainee) {
        this.trainee = trainee;
        CacheUtil.getCachedData(ctx, CacheUtil.CACHE_TRAINEE_ACTIVITY, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO response) {
                if (response != null) {
                    skillList = response.getSkillList();
                    skillLevelList = response.getSkillLevelList();
                    Log.i(LOG, "cached Skills: " + skillList.size() + " levels: " + skillLevelList.size());
                    //todo - remove current skills fro list
                    List<SkillDTO> list = new ArrayList<SkillDTO>();
                    HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
                    for (TraineeSkillDTO ts: trainee.getTraineeSkillList()) {
                        map.put(ts.getSkillID(), ts.getSkillID());
                    }
                    for (SkillDTO s : skillList) {
                        if (!map.containsKey(s.getSkillID())) {
                            list.add(s);
                        }
                    }
                    skillList = list;
                    Log.e(LOG,"########## skillList: " + skillList.size());
                    setList();
                } else {
                    throw new UnsupportedOperationException("Missing data ...");
                }
            }

            @Override
            public void onDataCached() {

            }
        });
    }

    CompanySkillAdapter adapter;
    TraineeDTO trainee;
    List<TraineeSkillDTO> traineeSkillList = new ArrayList<>();
    List<SkillDTO> skillList;
    List<SkillLevelDTO> skillLevelList;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat(
            "EEEE, dd MMMM yyyy HH:mm", loc);

    Context ctx;
    View view;
    ListView listView;
    TextView txtCount;
    Button btnSave;

}
