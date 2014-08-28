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
import com.boha.cmlibrary.dialogs.SkillDialog;
import com.boha.cmlibrary.adapters.SkillAdapter;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.SkillDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.ToastUtil;

import java.util.List;

public class SkillListFragment extends Fragment implements PageInterface {

	public SkillListFragment() {
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
		view = inflater.inflate(R.layout.fragment_skill_list, container, false);
		setFields();
		Bundle b = getArguments();
        if (b != null) {
            response = (ResponseDTO) b.getSerializable("response");
            skillList = response.getSkillList();
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
	
		listView = (ListView) view.findViewById(R.id.SKIL_listView);
		txtCount = (TextView) view.findViewById(R.id.SKIL_count);
        imageNew = (ImageView)view.findViewById(R.id.SKIL_imgNew);
        imageNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkillDialog dialog = new SkillDialog();
                dialog.setContext(ctx);
                dialog.setSkill(new SkillDTO());
                dialog.setListener(new SkillDialog.SkillDialogListener() {
                    @Override
                    public void onSkillComplete(ResponseDTO response) {
                        if (response.getStatusCode()>0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        skillList = response.getSkillList();
                        setList();
                    }
                });
                dialog.show(getFragmentManager(), "DIAG_SKILL");
            }
        });


	}

    boolean isUpdate;

	private void setList() {

		if (skillList == null) {
			Log.w(LOG, "skillList is null");
			return;
		}
		ctx = getActivity();
		adapter = new SkillAdapter(getActivity(), R.layout.skill_item,
				skillList);

		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		txtCount.setText("" + skillList.size());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				skill = skillList.get(arg2);

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
		skill = skillList.get(info.position);
		menu.setHeaderTitle(skill.getSkillName());
		menu.setHeaderIcon(ctx.getResources().getDrawable(
                R.drawable.ic_action_edit));
		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG, "onContextItemSelected: " + item.getTitle());


		if (item.getItemId() == R.id.menu_update) {
            SkillDialog dialog = new SkillDialog();
            dialog.setContext(ctx);
            dialog.setSkill(skill);
            dialog.setListener(new SkillDialog.SkillDialogListener() {
                @Override
                public void onSkillComplete(ResponseDTO response) {
                    if (response.getStatusCode()>0) {
                        ToastUtil.errorToast(ctx, response.getMessage());
                        return;
                    }
                    skillList = response.getSkillList();
                    setList();
                }
            });
            dialog.show(getFragmentManager(), "DIAG_SKILL");
            return true;
        }

        if (item.getItemId() == R.id.menu_update) {
            return true;
        }

		return true;
	}

	List<SkillDTO> skillList;
	SkillDTO skill;
	Context ctx;
	View view;

	SkillAdapter adapter;
	ListView listView;
	TextView txtCount;
    EditText editName;
    Button btnCancel, btnSave;
    View editLayout;
    ImageView imageNew;

}
