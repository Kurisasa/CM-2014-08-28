package com.boha.cmlibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.cmlibrary.dialogs.SkillLevelDialog;
import com.boha.cmlibrary.adapters.SkillLevelAdapter;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.SkillLevelDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.ToastUtil;

import java.util.List;

public class SkillLevelListFragment extends Fragment implements PageInterface {

    public SkillLevelListFragment() {
    }

    static final String LOG = "ClassListFragment";
    BusyListener busyListener;

    @Override
    public void onAttach(Activity a) {

        super.onAttach(a);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        Log.e(LOG, "############ onCreateView");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_skill_level_list, container, false);
        setFields();
        Bundle b = getArguments();
        if (b != null) {
            response = (ResponseDTO) b.getSerializable("response");
            skillLevelList = response.getSkillLevelList();
            setList();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    ResponseDTO response;

    private void setFields() {

        listView = (ListView) view.findViewById(R.id.SKV_listView);
        txtCount = (TextView) view.findViewById(R.id.SKV_count);

        imageNew = (ImageView) view.findViewById(R.id.SKV_imgNew);
        imageNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkillLevelDialog dialog = new SkillLevelDialog();
                dialog.setContext(ctx);
                dialog.setSkillLevel(new SkillLevelDTO());
                dialog.setListener(new SkillLevelDialog.SkillLevelDialogListener() {
                    @Override
                    public void onSkillLevelComplete(ResponseDTO response) {
                        if (response.getStatusCode()>0) {
                            ToastUtil.errorToast(ctx,response.getMessage());
                            return;
                        }
                        skillLevelList = response.getSkillLevelList();
                        setList();
                    }
                });
                dialog.show(getFragmentManager(), "DIAG_SKL");
            }
        });


    }



    private void setList() {

        if (skillLevelList == null) {
            Log.w(LOG, "skillLevelList is null");
            return;
        }
        ctx = getActivity();
        adapter = new SkillLevelAdapter(getActivity(), R.layout.skill_level_item,
                skillLevelList);

        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        txtCount.setText("" + skillLevelList.size());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                skillLevel = skillLevelList.get(arg2);

            }
        });

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        Log.w(LOG, "onCreateContextMenu ...");
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.skill_contextual, menu);
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        skillLevel = skillLevelList.get(info.position);
        menu.setHeaderTitle(skillLevel.getSkillLevelName());
        menu.setHeaderIcon(ctx.getResources().getDrawable(
                R.drawable.ic_action_edit));
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.w(LOG, "onContextItemSelected: " + item.getTitle());

        if (item.getItemId() == R.id.menu_update) {
            SkillLevelDialog dialog = new SkillLevelDialog();
            dialog.setContext(ctx);
            dialog.setSkillLevel(skillLevel);
            dialog.setListener(new SkillLevelDialog.SkillLevelDialogListener() {
                @Override
                public void onSkillLevelComplete(ResponseDTO response) {
                    if (response.getStatusCode()>0) {
                        ToastUtil.errorToast(ctx,response.getMessage());
                        return;
                    }
                    skillLevelList = response.getSkillLevelList();
                    setList();
                }
            });
            dialog.show(getFragmentManager(), "DIAG_SKL");
            return true;
        }
        if (item.getItemId() == R.id.menu_delete) {

            return true;
        }

        return true;
    }

    List<SkillLevelDTO> skillLevelList;
    SkillLevelDTO skillLevel;
    Context ctx;
    View view;

    SkillLevelAdapter adapter;
    ListView listView;
    TextView txtCount;
    EditText editName, editLevel;
    Button btnCancel, btnSave;
    View editLayout;
    ImageView imageNew;

}
