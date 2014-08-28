package com.boha.cmauthor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boha.cmauthor.R;
import com.boha.cmauthor.adapter.AuthorActivityAdapter;
import com.boha.cmauthor.interfaces.ActivityListener;
import com.boha.cmauthor.interfaces.UpDownArrrowListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class ActivityListFragment extends AbstractBuilder implements
        UpDownArrrowListener {
    public ActivityListFragment() {
    }

    static final String LOG = "ActivityListFragment";
    View view;

    private ActivityListener activityListener;

    @Override
    public void onAttach(Activity a) {
        Log.i(LOG, "#################### onAttach ACTIVITYListFragment");
        if (a instanceof ActivityListener) {
            activityListener = (ActivityListener) a;
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
        view = inflater.inflate(R.layout.fragment_activity_list, container,
                false);
        company = SharedUtil.getCompany(ctx);
        setFields();
        Bundle b = getArguments();
        if (b != null) {
            course = (CourseDTO) b.getSerializable("course");
            activityList = course.getActivityList();
            if (activityList == null || activityList.isEmpty()) {
                openAddLayout();
            }
        }
        setList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(LOG, "## onResume");

    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "##### onSaveInstanceState -- save lesson");
        super.onSaveInstanceState(b);
    }


    @Override
    public void onLocalDataTaskDone() {

    }

    boolean isAdding;
    TextView crsName, catName, txtLabel;

    @Override
    public void setFields() {
        crsName = (TextView) view.findViewById(R.id.CRS_course);
        if (course != null) {
            crsName.setText(course.getCourseName());
        }
        catName = (TextView) view.findViewById(R.id.CRS_category);
        if (category != null) {
            catName.setText(category.getCategoryName());
        }
        txtCount = (TextView) view.findViewById(R.id.LH_count);
        txtLabel = (TextView) view.findViewById(R.id.LH_label);

        txtCount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        ctx,
                        activityList.size()
                                + " : "
                                + ctx.getResources().getString(
                                R.string.items_in_list),
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

        editName = (EditText) view.findViewById(R.id.CRS_name);
        editDescription = (EditText) view.findViewById(R.id.CRS_desc);
        btnSave = (Button) view.findViewById(R.id.CRS_btnSave);
        listView = (ListView) view.findViewById(R.id.CRS_listView);
        addLayout = view.findViewById(R.id.CRS_layout1);
        addLayout.setVisibility(View.GONE);
        label = (TextView) addLayout.findViewById(R.id.CRS_label2);

        btnCancel = (Button) view.findViewById(R.id.CRS_btnCancel);

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (editName.getText().toString().isEmpty()) {
                    ToastUtil.errorToast(ctx, "Please enter name");
                    return;
                }
                if (isUpdate) {
                    activity.setActivityName(editName.getText().toString());
                    activity.setDescription(editDescription.getText()
                            .toString());
                    shuffledActivityList = new ArrayList<ActivityDTO>();
                    shuffledActivityList.add(activity);
                    setActivityListForPriorityUpdate();
                    isUpdate = false;
                    sendShuffledActivityList();
                } else {
                    activity = new ActivityDTO();
                    activity.setCourseID(course.getCourseID());
                    activity.setLocalID(System.currentTimeMillis());
                    activity.setActivityName(editName.getText().toString());
                    activity.setDescription(editDescription.getText()
                            .toString());

                    type = RequestDTO.ADD_ACTIVITIES;
                }

                addActivity();

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                closeAddLayout();

            }
        });
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.xblink);
        a.setDuration(300);
        txtLabel.startAnimation(a);
    }

    private void addActivity() {
        Log.e(LOG, "----------- addActivity for course: " + course.getCourseID());
        type = RequestDTO.ADD_ACTIVITIES;
        RequestDTO req = new RequestDTO();
        req.setRequestType(type);
        req.setCourseID(course.getCourseID());
        req.setActivity(activity);

        activityListener.onShowProgressBar();
        WebSocketUtil.sendRequest(ctx, Statics.AUTHOR_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityListener.onRemoveProgressBar();
                        response = r;
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        hideKeyboard();
                        activityList = response.getActivityList();
                        setList();
                        activityListener.onActivitiesAddedUpdated();
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
//		BaseVolley.getRemoteData(Statics.SERVLET_AUTHOR, req, ctx, new BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				activityListener.onRemoveProgressBar();
//				ToastUtil.toast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO r) {
//				activityListener.onRemoveProgressBar();
//				response = r;
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, response.getMessage());
//					return;
//				}
//				hideKeyboard();
//                activityList = response.getActivityList();
//				setList();
//				activityListener.onActivitiesAddedUpdated();
//			}
//		});
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

    private boolean hideDescription;

    public void hideDescriptions() {
        hideDescription = true;
        setList();
    }

    public void showDescriptions() {
        hideDescription = false;
        setList();
    }

    @Override
    public void setList() {
        //closeAddLayout();
        if (activityList == null || activityList.size() == 0) {
            openAddLayout();
            return;
        }

        adapter = new AuthorActivityAdapter(ctx, R.layout.activity_item,
                activityList, hideDescription);

        isPriorityChangeAnimation = !isPriorityChangeAnimation;
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setSelection(positionAtIndex);
        positionAtIndex = 0;

        txtCount.setText("" + activityList.size());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                activityListener.onActivityPicked(activityList
                        .get(arg2));
                activity = activityList.get(arg2);
            }
        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                activityListener.onActivityPicked(activityList
                        .get(arg2));
                activity = activityList.get(arg2);
                return false;
            }
        });
    }

    Menu mMenu;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.action_context, menu);
        menu.setHeaderTitle(activity.getActivityName());
        menu.setHeaderIcon(android.R.drawable.ic_menu_save);
        mMenu = menu;

        if (menu != null) {
            menu.getItem(0).setActionView(R.layout.update_custom_view);
            menu.getItem(1).setActionView(R.layout.delete_custom_view);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        activity = activityList.get(info.position);

        switch (item.getItemId()) {
            case R.id.menu_up:
                moveItemUp(info.position);
                return true;
            case R.id.menu_down:
                moveItemDown(info.position);
                return true;

            case R.id.menu_update:
                isUpdate = true;

                label.setText(ctx.getResources().getString(R.string.update_save));
                openAddLayout(activity.getActivityName(),activity.getDescription());
                return true;
            case R.id.menu_delete:
                type = RequestDTO.DELETE_ACTIVITIES;
                activityList = new ArrayList<ActivityDTO>();
                ActivityDTO a = new ActivityDTO();
                a.setActivityID(activity.getActivityID());
                activityList.add(a);
                sendRequest(type, Statics.SERVLET_AUTHOR);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void moveItemUp(int currentPosition) {

        if (currentPosition == 0)
            return;
        ActivityDTO currentActivity = activityList.get(
                currentPosition);
        List<ActivityDTO> list = activityList;
        list.remove(currentPosition);
        list.add(currentPosition - 1, currentActivity);
        positionAtIndex = currentPosition - 1;

        int i = 0;
        for (ActivityDTO a : activityList) {
            a.setPriorityFlag(Integer.valueOf(i + 1));
            i++;
        }
        Log.w(LOG, "Trying to reset manually UP...");
        setList();
        setActivityListForPriorityUpdate();
        sendShuffledActivityList();
    }

    boolean isShuffling;
    int positionAtIndex;

    private void moveItemDown(int currentPosition) {

        if (currentPosition == activityList.size() - 1)
            return;
        isShuffling = true;
        ActivityDTO currentActivity = activityList.get(
                currentPosition);

        activityList.remove(currentPosition);
        activityList.add(currentPosition + 1, currentActivity);
        positionAtIndex = currentPosition + 1;

        if (currentPosition < activityList.size() - 1) {
            listView.setSelection(currentPosition + 1);
        }
        setActivityListForPriorityUpdate();
        int i = 0;
        for (ActivityDTO a : activityList) {
            a.setPriorityFlag(Integer.valueOf(i + 1));
            i++;
        }
        Log.w(LOG, "Trying to reset manually DOWN...");
        setList();
        sendShuffledActivityList();
    }
    private void sendShuffledActivityList() {
        isShuffling = true;
        IDs = new ArrayList<Integer>();
        for (ActivityDTO a : activityList) {
            IDs.add(a.getActivityID());;
        }
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.SHUFFLE_ACTIVITIES);
        w.setIDs(IDs);

        WebSocketUtil.sendRequest(ctx,Statics.AUTHOR_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r.getStatusCode() == 0) {
                            response = r;
                            setList();
                            CacheUtil.cacheData(ctx, r, CacheUtil.CACHE_CATEGORIES, new CacheUtil.CacheUtilListener() {
                                @Override
                                public void onFileDataDeserialized(ResponseDTO response) {

                                }

                                @Override
                                public void onDataCached() {

                                }
                            });
                        } else {
                            ToastUtil.errorToast(ctx, r.getMessage());
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
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });

    }
    List<Integer> IDs = new ArrayList<>(), flags = new ArrayList<>();


    private void setActivityListForPriorityUpdate() {
        isShuffling = true;
        shuffledActivityList = new ArrayList<ActivityDTO>();
        int index = 0;
        for (ActivityDTO a : activityList) {
            ActivityDTO x = new ActivityDTO();
            x.setActivityID(a.getActivityID());
            x.setActivityName(null);
            x.setDescription(null);
            x.setPriorityFlag(Integer.valueOf(index + 1));
            shuffledActivityList.add(x);
            index++;
        }
        isPriorityChangeAnimation = true;

    }


    int priority;
    TextView txtCount;
    AuthorActivityAdapter adapter;
    boolean isPriorityChangeAnimation;

    @Override
    public void onUpArrowTapped(int index) {
        moveItemUp(index);

    }

    @Override
    public void onDownArrowTapped(int index) {
        moveItemDown(index);
    }

    @Override
    public void networkCallDone(boolean isOK) {
        activityListener.onRemoveProgressBar();
    }
}
