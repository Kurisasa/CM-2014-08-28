package com.boha.cmauthor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boha.cmauthor.R;
import com.boha.cmauthor.adapter.ObjectiveAdapter;
import com.boha.cmauthor.interfaces.ObjectiveListener;
import com.boha.cmauthor.interfaces.UpDownArrrowListener;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.ObjectiveDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;

import java.util.ArrayList;

public class ObjectiveListFragment extends AbstractBuilder implements
		UpDownArrrowListener {
	public ObjectiveListFragment() {
	}

	View view;
	private ObjectiveListener objectiveListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof ObjectiveListener) {
			objectiveListener = (ObjectiveListener) a;
			ctx = getActivity();
		} else {
			throw new UnsupportedOperationException();
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
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_objective_list, container,
				false);
		company = SharedUtil.getCompany(ctx);
		setFields();
		return view;
	}

	@Override
	public void onResume() {
		Log.e("oLF",
				"############### resuming in objectiveListFrag ...get objects from lesson");
		if (course != null) {
			setList();
		}
		super.onResume();

	}

	CourseDTO course;

	public void setCurrentCourse(CourseDTO course) {
		this.course = course;
		response = new ResponseDTO();
		response.setObjectiveList(course.getObjectiveList());

	}

	@Override
	public void onLocalDataTaskDone() {
	}

	@Override
	public void setFields() {
		TextView crsName = (TextView) view.findViewById(R.id.CRS_course);
		if (course != null) {
			crsName.setText(course.getCourseName());
		}
		TextView catName = (TextView) view.findViewById(R.id.CRS_category);
		if (category != null) {
			catName.setText(category.getCategoryName());
		}
		TextView lessonName = (TextView) view.findViewById(R.id.CRS_lesson);
		if (course != null) {
			lessonName.setText(course.getCourseName());
		}
		txtCount = (TextView) view.findViewById(R.id.LH_count);
		label = (TextView) view.findViewById(R.id.CRS_label2);
		Statics.setRobotoFontRegular(ctx, crsName);
		Statics.setRobotoFontBold(ctx, lessonName);
		Statics.setRobotoFontRegular(ctx, txtCount);
		Statics.setRobotoFontRegular(ctx, label);
		txtCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(
						ctx,
						response.getObjectiveList().size()
								+ " : "
								+ ctx.getResources().getString(
										R.string.items_in_list),
						Toast.LENGTH_SHORT).show();

			}
		});
		editName = (EditText) view.findViewById(R.id.CRS_name);
		editDescription = (EditText) view.findViewById(R.id.CRS_desc);
		btnSave = (Button) view.findViewById(R.id.CRS_btnSave);
		listView = (ListView) view.findViewById(R.id.CRS_listView);
		addLayout = view.findViewById(R.id.CRS_layout1);
		addLayout.setVisibility(View.GONE);

		btnCancel = (Button) view.findViewById(R.id.CRS_btnCancel);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (editName.getText().toString().isEmpty()) {
					ToastUtil.errorToast(ctx, "Please enter name");
					return;
				}

				if (isUpdate) {
					lessonObjective.setObjectiveName(editName.getText()
							.toString());
					lessonObjective.setDescription(editDescription.getText()
							.toString());
					shuffledObjectiveList = new ArrayList<ObjectiveDTO>();
					shuffledObjectiveList.add(lessonObjective);
					type = RequestDTO.UPDATE_OBJECTIVES;
					isUpdate = false;
				} else {
					lessonObjective = new ObjectiveDTO();
					lessonObjective.setCourseID(course.getCourseID());
					lessonObjective.setObjectiveName(editName.getText()
							.toString());
					lessonObjective.setDescription(editDescription.getText()
							.toString());

					type = RequestDTO.ADD_OBJECTIVES;
				}
				hideKeyboard();
				objectiveListener.onShowProgressBar();
				sendRequest(type, Statics.SERVLET_AUTHOR);

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				closeAddLayout();
				hideKeyboard();

			}
		});

	}

	int priority;
	TextView txtCount;
	ObjectiveAdapter adapter;
	boolean isAdding, isPriorityAnimation;

	@Override
	public void setList() {
		objectiveListener.onRemoveProgressBar();
		closeAddLayout();
		if (getActivity() == null) {
			Log.e(LOG, "Context is NULL. Somethin weird going down ...");
			return;
		}
		if (response == null) {
			Log.d("OLF", "---------- response is NULL");
			return;
		}
		if (response.getObjectiveList() == null
				|| response.getObjectiveList().size() == 0) {
			openAddLayout();
			return;
		}
		lessonObjectiveList = new ArrayList<ObjectiveDTO>();
		lessonObjectiveList.addAll(response.getObjectiveList());
		if (ctx == null) {
			Log.e("OLF", "------ Context is NULL in setList()");
			return;
		}

		adapter = new ObjectiveAdapter(getActivity(), R.layout.objective_item,
				response.getObjectiveList(), this);

		isPriorityAnimation = !isPriorityAnimation;
		listView.setAdapter(adapter);
		listView.setSelection(positionAtIndex);
		registerForContextMenu(listView);
		txtCount.setText("" + response.getObjectiveList().size());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				objectiveListener.onObjectivePicked(response.getObjectiveList()
						.get(arg2));
				lessonObjective = response.getObjectiveList().get(arg2);
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				lessonObjective = response.getObjectiveList().get(arg2);
				return false;
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.action_context, menu);
		menu.setHeaderTitle(lessonObjective.getObjectiveName());
		menu.setHeaderIcon(android.R.drawable.ic_menu_save);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		lessonObjective = response.getObjectiveList().get(info.position);

		switch (item.getItemId()) {

		case R.id.menu_update:
			isUpdate = true;
			editName.setText(lessonObjective.getObjectiveName());
			editDescription.setText(lessonObjective.getDescription());
			openAddLayout();
			return true;
		case R.id.menu_delete:
			lessonObjectiveList = new ArrayList<ObjectiveDTO>();
			ObjectiveDTO o = new ObjectiveDTO();
			o.setObjectiveID(lessonObjective.getObjectiveID());
			lessonObjectiveList.add(o);
			type = RequestDTO.DELETE_OBJECTIVES;
			sendRequest(type, Statics.SERVLET_AUTHOR);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	private void moveItemUp(int currentPosition) {

		if (currentPosition == 0)
			return;
		isShuffling = true;
		ObjectiveDTO currObj = response.getObjectiveList().get(currentPosition);
		response.getObjectiveList().remove(currentPosition);
		response.getObjectiveList().add(currentPosition - 1, currObj);
		positionAtIndex = currentPosition - 1;
		// adapter.notifyDataSetChanged();

		// update
		type = RequestDTO.UPDATE_OBJECTIVES;
		sendRequest(type, Statics.SERVLET_AUTHOR);
	}

	private void moveItemDown(int currentPosition) {

		if (currentPosition == response.getObjectiveList().size() - 1)
			return;
		isShuffling = true;
		ObjectiveDTO currentActivity = response.getObjectiveList().get(
				currentPosition);
		response.getObjectiveList().remove(currentPosition);
		response.getObjectiveList().add(currentPosition + 1, currentActivity);
		positionAtIndex = currentPosition + 1;
		// adapter.notifyDataSetChanged();

		// update
		type = RequestDTO.UPDATE_OBJECTIVES;
		sendRequest(type, Statics.SERVLET_AUTHOR);
	}

	boolean isShuffling;


	@Override
	public void onUpArrowTapped(int index) {
		moveItemUp(index);

	}

	@Override
	public void onDownArrowTapped(int index) {
		moveItemDown(index);

	}

	int positionAtIndex;

	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
	}

	@Override
	public void networkCallDone(boolean isOK) {
		objectiveListener.onRemoveProgressBar();
	}
}
