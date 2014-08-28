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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boha.cmauthor.R;
import com.boha.cmauthor.adapter.CategoryAdapter;
import com.boha.cmauthor.interfaces.CategoryListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends AbstractBuilder {

    CategoryListener categoryListener;
    BusyListener busyListener;

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

    }

    @Override
    public void onAttach(Activity a) {
        if (a instanceof CategoryListener) {
            categoryListener = (CategoryListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host must implement CategoryListener");
        }
        if (a instanceof BusyListener) {
            busyListener = (BusyListener) a;
        } else {
            throw new UnsupportedOperationException(
                    "Host must implement BusyListener");
        }
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {

        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_category_list, container,
                false);
        setFields();
        company = SharedUtil.getCompany(ctx);
        if (saved != null) {
            response = (ResponseDTO) saved.getSerializable("response");
        }

        return view;
    }


    Gson g = new Gson();

    public void onResume() {
        super.onResume();
        setList();

    }

    @Override
    public void onSaveInstanceState(Bundle state) {

        Log.i(LOG, "##### onSaveInstanceState  fired ....");
        state.putSerializable("response", response);
        super.onSaveInstanceState(state);
    }

    class Container {
        List<CategoryDTO> list;

    }

    RequestDTO request;

    public void setResponse(ResponseDTO response) {
        this.response = response;
        if (listView != null) {
            setList();
        }
    }

    @Override
    public void onLocalDataTaskDone() {
        response.setCategoryList(categoryList);
        setList();
    }

    TextView txtCount;
    boolean isAddingCategory;
    static final String LOG = "CategoryListFragment";

    @Override
    public void setList() {
        if (getActivity() == null) {
            Log.e(LOG, "Context is NULL. Somethin weird going down ...");
            return;
        }
        if (response == null)
            return;
        if (SharedUtil.getCategoryPressHoldCount(getActivity()) < 5) {
            Toast.makeText(ctx, R.string.press_and_hold_category,
                    Toast.LENGTH_LONG).show();
            SharedUtil.incrementCategoryPressHoldCount(ctx);
        }

        if (response.getCategoryList() != null) {
            txtCount.setText("" + response.getCategoryList().size());
        }
        adapter = new CategoryAdapter(getActivity(), R.layout.category_item,
                response.getCategoryList());
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                category = response.getCategoryList().get(arg2);
                categoryListener.onCategoryPicked(category);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // categoryListener.onCategoryPicked(response.getCategoryList().get(arg2));
                category = response.getCategoryList().get(arg2);
                return false;
            }
        });
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.xblink);
        a.setDuration(500);
        txtHdrLabel.startAnimation(a);
    }

    @Override
    public void setFields() {
        TextView auth = (TextView) view.findViewById(R.id.CAT_author);
        txtHdrLabel = (TextView) view.findViewById(R.id.LH_label);
        txtCompany = (TextView) view.findViewById(R.id.CAT_company);
        txtCompany.setText(SharedUtil.getCompany(ctx).getCompanyName());
        author = SharedUtil.getAuthor(ctx);
        auth.setText(author.getFirstName() + " " + author.getLastName());
        Statics.setRobotoFontRegular(ctx, auth);

        editName = (EditText) view.findViewById(R.id.CAT_categoryName);
        listView = (ListView) view.findViewById(R.id.CAT_listView);
        addLayout = view.findViewById(R.id.CAT_layout1);
        addLayout.setVisibility(View.GONE);
        company = SharedUtil.getCompany(ctx);
        txtHdrLabel.setText(getResources().getString(R.string.category_list));
        txtCount = (TextView) view.findViewById(R.id.LH_count);
        Statics.setRobotoFontRegular(ctx, txtCount);
        txtCount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        ctx,
                        response.getCategoryList().size()
                                + " : "
                                + ctx.getResources().getString(
                                R.string.items_in_list),
                        Toast.LENGTH_SHORT
                ).show();

            }
        });
        btnCancel = (Button) view.findViewById(R.id.CAT_btnCancel);
        btnImport = (Button) view.findViewById(R.id.CRS_btnImport);
        btnSave = (Button) view.findViewById(R.id.CAT_btnSave);
        btnImport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryListener.onImportRequested();
            }
        });
        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (editName.getText().toString().isEmpty()) {
                    ToastUtil.errorToast(ctx, "Please enter category name");
                    return;
                }
                if (isUpdate) {
                    CompanyDTO co = SharedUtil.getCompany(ctx);
                    category.setCompanyID(co.getCompanyID());
                    category.setCategoryName(editName.getText().toString());
                    type = RequestDTO.UPDATE_CATEGORY;
                } else {
                    category = new CategoryDTO();
                    category.setLocalID(System.currentTimeMillis());
                    category.setCategoryName(editName.getText().toString());
                    isAddingCategory = true;
                    type = RequestDTO.ADD_CATEGORY;
                }

                categoryListener.setBusy();
                hideKeyboard();
                sendRequest(type, Statics.SERVLET_AUTHOR);

            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                closeAddLayout();

            }
        });

    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        menu.setHeaderIcon(android.R.drawable.ic_menu_preferences);
        if (category != null) {
            menu.setHeaderTitle(category.getCategoryName());
        } else {
            menu.setHeaderTitle("Category Actions");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        category = response.getCategoryList().get(info.position);
        switch (item.getItemId()) {

            case R.id.menu_down:
                moveItemDown(info.position);
                return true;
            case R.id.menu_up:
                moveItemUp(info.position);
                return true;
            case R.id.menu_overview:

                return true;
            case R.id.menu_delete:
                categoryListener.setBusy();
                type = RequestDTO.DELETE_CATEGORY;
                sendRequest(type, Statics.SERVLET_AUTHOR);
                return true;
            case R.id.menu_update:
                isUpdate = true;
                editName.setText(category.getCategoryName());
                openAddLayout();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void moveItemUp(int currentPosition) {

        if (currentPosition == 0)
            return;
        CategoryDTO currCat = response.getCategoryList().get(
                currentPosition);
        List<CategoryDTO> list = response.getCategoryList();
        list.remove(currentPosition);
        list.add(currentPosition - 1, currCat);
        positionAtIndex = currentPosition - 1;

        int i = 0;
        for (CategoryDTO a : response.getCategoryList()) {
            a.setPriorityFlag(Integer.valueOf(i + 1));
            i++;
        }
        Log.w(LOG, "Trying to reset manually UP...");
        setList();
        sendShuffledCategoryList();

    }
    private void moveItemDown(int currentPosition) {

        if (currentPosition == response.getCategoryList().size() - 1)
            return;
        CategoryDTO currCat = response.getCategoryList().get(
                currentPosition);
        List<CategoryDTO> list = response.getCategoryList();
        list.remove(currentPosition);
        list.add(currentPosition + 1, currCat);
        positionAtIndex = currentPosition + 1;

        int i = 0;
        for (CategoryDTO a : response.getCategoryList()) {
            a.setPriorityFlag(Integer.valueOf(i + 1));
            i++;
        }
        Log.w(LOG, "Trying to reset manually DOWN...");
        setList();
        sendShuffledCategoryList();

    }
    private void sendShuffledCategoryList() {
        isShuffling = true;
        IDs = new ArrayList<Integer>();
        flags = new ArrayList<Integer>();
        int index = 0;
        for (CategoryDTO a : response.getCategoryList()) {
            flags.add(index + 1);
            IDs.add(a.getCategoryID());;
            index++;
        }
    if (!BaseVolley.checkNetworkOnDevice(ctx)) {
        return;
    }
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.SHUFFLE_CATEGORIES);
        w.setIDs(IDs);
        w.setPriorityFlags(flags);

        WebSocketUtil.sendRequest(ctx,Statics.AUTHOR_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r.getStatusCode() == 0) {
                            response = r;
                            setList();
                            CacheUtil.cacheData(ctx,r,CacheUtil.CACHE_CATEGORIES, new CacheUtil.CacheUtilListener() {
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
    boolean isShuffling;
    int positionAtIndex;
    CategoryAdapter adapter;
    int type;
    TextView txtHdrLabel, txtCompany;
    Button btnImport;


    @Override
    public void networkCallDone(boolean isOK) {

        categoryListener.setNotBusy();
    }

}
