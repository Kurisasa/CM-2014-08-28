package com.boha.cmauthor.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmauthor.R;
import com.boha.cmauthor.SearchActivity;
import com.boha.cmauthor.adapter.ResourceAdapter;
import com.boha.cmauthor.interfaces.ActivityListener;
import com.boha.cmauthor.interfaces.ResourceListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

public class ResourceListFragment extends AbstractBuilder {
	public ResourceListFragment() {
	}

	View view;

	private ResourceListener resourceListener;
	private ActivityListener activityListener;

	@Override
	public void onAttach(Activity a) {
		if (a instanceof ResourceListener) {
			resourceListener = (ResourceListener) a;
			ctx = getActivity();
		} else {
			throw new UnsupportedOperationException();
		}
		if (a instanceof ActivityListener) {
			activityListener = (ActivityListener) a;
		} else {
			throw new UnsupportedOperationException("Host " + a.getLocalClassName() + " must implement ActivityListener");
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
		view = inflater.inflate(R.layout.fragment_resource_list, container,
				false);
		company = SharedUtil.getCompany(ctx);
		setFields();
        Bundle b = getArguments();
        if (b != null) {
            course = (CourseDTO) b.getSerializable("course");
            lessonResourceList = course.getLessonResourceList();
            if (lessonResourceList == null || lessonResourceList.isEmpty() ) {
                addLink();
            }
            setList();
        }

		return view;
	}

	@Override
	public void onResume() {
		Log.e("RLF",
				"############### onResume in resourceListFrag ...setting list");

		setList();
		super.onResume();

	}

	public void addLink() {
		addLayout.setVisibility(View.VISIBLE);
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);
		addLayout.startAnimation(a);
	}
	ActivityDTO  activity;

	@Override
	public void onLocalDataTaskDone() {

	}

	TextView lessonName;

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
		lessonName = (TextView) view.findViewById(R.id.CRS_lesson);

		txtCount = (TextView) view.findViewById(R.id.LH_count);
		TextView label = (TextView) view.findViewById(R.id.LH_label);
		//Statics.setRobotoFontRegular(ctx, crsName);
    //		Statics.setRobotoFontBold(ctx, lessonName);
		//Statics.setRobotoFontRegular(ctx, txtCount);
		//Statics.setRobotoFontRegular(ctx, label);

		editName = (EditText) view.findViewById(R.id.CRS_name);
		editDescription = (EditText) view.findViewById(R.id.CRS_desc);
		btnSave = (Button) view.findViewById(R.id.CRS_btnSave);
		listView = (ListView) view.findViewById(R.id.CRS_listView);
		addLayout =  view.findViewById(R.id.CRS_layout1);
		addLayout.setVisibility(View.GONE);

		btnCancel = (Button) view.findViewById(R.id.CRS_btnCancel);
		Button btnSearch = (Button) view.findViewById(R.id.CRS_btnSearch);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (editName.getText().toString().isEmpty()) {
					ToastUtil.errorToast(ctx, "Please enter name");
					return;
				}
				hideKeyboard();
                sendLink();

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				closeAddLayout();

			}
		});
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startLinkSearch(null);

			}
		});
	}

    private void sendLink() {
        Log.e(LOG,"...........sendLink courseID: " + course.getCourseID());
        lessonResource = new LessonResourceDTO();
        lessonResource.setCourseID(course.getCourseID());
        lessonResource.setUrl(editName.getText().toString());

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.ADD_RESOURCES);
        w.setLessonResource(lessonResource);
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        WebSocketUtil.sendRequest(ctx,Statics.AUTHOR_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.getStatusCode() == 0) {
                            Log.i(LOG,"####### link added");
                        } else {
                            ToastUtil.errorToast(ctx, response.getMessage());
                        }
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
//        BaseVolley.getRemoteData(Statics.SERVLET_AUTHOR, w,ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//
//            }
//        });

    }
	
	int priority;
	TextView txtCount;
	ResourceAdapter adapter;

	@Override
	public void setList() {
		//closeAddLayout();

		if (lessonResourceList == null
				|| lessonResourceList.size() == 0) {
			return;
		}

		adapter = new ResourceAdapter(getActivity(), R.layout.resource_item,
				lessonResourceList);
		listView.setAdapter(adapter);
		listView.setDividerHeight(5);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				resourceListener.onResourcePicked(lessonResourceList.get(arg2));
				lessonResource = lessonResourceList.get(arg2);
				startLinkSearch(lessonResource.getUrl());
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				lessonResource = lessonResourceList.get(arg2);
				return false;
			}
		});
	}

	public void startLinkSearch(String url) {
		Intent i = new Intent(ctx, SearchActivity.class);
		i.putExtra("course", course);
		if (url != null) {
			i.putExtra("url", url);
		}
		startActivityForResult(i, START_SEARCH);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == START_SEARCH) {
			if (resultCode == Activity.RESULT_OK) {
				Log.w(LOG, "---onActivityResult  resultCode OK - tell listener, link added");
				ResponseDTO returnedResponse = (ResponseDTO) data.getExtras()
						.getSerializable("response");
				lessonResourceList = returnedResponse.getLessonResourceList();
				setList();
				activityListener.onLinksAddedUpdated();
			}
		}

	}

	static final int START_SEARCH = 33;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.action_link_context, menu);
		if (lessonResource.getResourceName() == null
				|| lessonResource.getResourceName().isEmpty()) {
			menu.setHeaderTitle("Link with no Title");
		} else {
			menu.setHeaderTitle(lessonResource.getResourceName());
		}
		menu.setHeaderIcon(android.R.drawable.ic_menu_delete);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		lessonResource = response.getLessonResourceList().get(info.position);

		switch (item.getItemId()) {
		case R.id.menu_delete:
			resourceListener.onShowProgressBar();
			type = RequestDTO.DELETE_LESSON_RESOURCES;
			hideKeyboard();
			sendRequest(type, Statics.SERVLET_AUTHOR);
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}


	@Override
	public void networkCallDone(boolean isOK) {
		resourceListener.onRemoveProgressBar();
	}
	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
	}
}
