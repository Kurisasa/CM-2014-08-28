package com.boha.cmauthor.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.cmauthor.R;
import com.boha.cmauthor.adapter.CourseAdapter;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.dto.ObjectiveDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.ImportUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by aubreyM on 2014/06/13.
 */
public class ImportFragment extends Fragment {
    public interface ImportFragmentListener {
        public void onCoursesImported();

        public void setBusy();

        public void setNotBusy();
    }

    ImportFragmentListener listener;

    @Override
    public void onAttach(Activity a) {
        if (a instanceof ImportFragmentListener) {
            listener = (ImportFragmentListener) a;
        } else {
            throw new UnsupportedOperationException("Host " + a.getLocalClassName() +
                    " must implement ImportFragmentListener");
        }
        Log.e(LOG, "##### Fragment hosted by " + a.getLocalClassName());
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater
                .inflate(R.layout.import_players, container, false);
        setFields();
        return view;

    }

    private void setFields() {
        fileSpinner = (Spinner) view.findViewById(R.id.IMP_fileSpinner);
        btnImport = (Button) view.findViewById(R.id.IMP_btnImport);
        txtTitle = (TextView) view.findViewById(R.id.IMP_title);
        txtCount = (TextView) view.findViewById(R.id.IMP_count);
        image = (ImageView) view.findViewById(R.id.IMP_image);
        listView = (ListView) view.findViewById(R.id.IMP_list);

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                controlImport();
            }
        });

        files = ImportUtil.getImportFilesOnSD();
        files.addAll(ImportUtil.getImportFiles());

        setSpinner();
        Log.w(LOG, "------------- Import files found: " + files.size());
    }

    List<File> files;
    List<CategoryDTO> categoryList;

    private void saveCategory(CategoryDTO cat) {
        Log.d(LOG,"############# saveCategory cat: " + cat.getCategoryName());
        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.ADD_CATEGORY);
        w.setCategory(cat);
        listener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.AUTHOR_ENDPOINT, w, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        Log.d(LOG,"############# onResponseReceived categoryID: " + r.getCategory().getCategoryID());
                        category = r.getCategory();
                        categoryList = r.getCategoryList();
                        try {
                            parseFile();
                        } catch (IOException e) {
                            e.printStackTrace();
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
//        BaseVolley.getRemoteData(Statics.SERVLET_AUTHOR, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, response.getMessage());
//                    return;
//                }
//                Log.d(LOG,"############# onResponseReceived categoryID: " + response.getCategory().getCategoryID());
//                category = response.getCategory();
//                categoryList = response.getCategoryList();
//                try {
//                    parseFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//
//            }
//        });
    }

    File file;

    private void parseFile() throws IOException {
        courseList = new ArrayList<CourseDTO>();

        BufferedReader brReadMe = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"));
        String strLine = brReadMe.readLine();
        CourseDTO course = null;
        while (strLine != null) {

                Pattern patt = Pattern.compile(";");
                String[] result = patt.split(strLine);
               try {
                   String s = result[0];
               } catch (IndexOutOfBoundsException e) {
                   strLine = brReadMe.readLine();
                   continue;
               }
                try {
                    if (result[0] != null) {
                        if (result[0].equalsIgnoreCase("category")) {
                            if (category == null) {
                                Log.d(LOG,"############# parseFile category is null");
                                CategoryDTO c = new CategoryDTO();
                                c.setCategoryName(result[1]);
                                c.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
                                saveCategory(c);
                                return;
                            }
                        }
                    }
                } catch (Exception e) {e.printStackTrace();}
                if (result[0] != null) {
                    if (result[0].equalsIgnoreCase("unit")) {
                        CourseDTO crs = new CourseDTO();
                        crs.setCategory(category);
                        crs.setCourseName(result[1]);
                        try {
                            crs.setDescription(result[2]);
                        } catch (Exception e) {}
                        courseList.add(0, crs);
                        strLine = brReadMe.readLine();
                        continue;
                    }
                    if (result[0].equalsIgnoreCase("activity")) {
                        if (courseList.get(0).getActivityList() == null)
                            courseList.get(0).setActivityList(new ArrayList<ActivityDTO>());
                        ActivityDTO act = new ActivityDTO();
                        act.setActivityName(result[1]);
                        try {
                            act.setDescription(result[2]);
                        } catch (Exception e) {}
                        courseList.get(0).getActivityList().add(act);
                        strLine = brReadMe.readLine();
                        continue;
                    }
                    if (result[0].equalsIgnoreCase("objective")) {
                        if (courseList.get(0).getObjectiveList() == null)
                            courseList.get(0).setObjectiveList(new ArrayList<ObjectiveDTO>());
                        ObjectiveDTO objective = new ObjectiveDTO();
                        objective.setObjectiveName(result[1]);
                        courseList.get(0).getObjectiveList().add(objective);
                        strLine = brReadMe.readLine();
                        continue;
                    }

                    if (result[0].equalsIgnoreCase("link")) {
                        if (courseList.get(0).getLessonResourceList() == null)
                            courseList.get(0).setLessonResourceList(new ArrayList<LessonResourceDTO>());
                        LessonResourceDTO link = new LessonResourceDTO();
                        link.setUrl(result[1]);
                        try {
                            link.setResourceName(result[2]);
                        } catch (Exception e) {}
                        courseList.get(0).getLessonResourceList().add(link);
                        strLine = brReadMe.readLine();
                        continue;
                    }
                }
                strLine = brReadMe.readLine();
        }

        brReadMe.close();
        setList();
        if (courseList.isEmpty()) {
            btnImport.setEnabled(false);
        } else {
            btnImport.setEnabled(true);
        }
    }

    private void setList() {
        Collections.reverse(courseList);
        adapter = new CourseAdapter(ctx, R.layout.course_item, courseList);
        listView.setAdapter(adapter);
        txtCount.setText(""+courseList.size());
    }

    private List<CourseDTO> courseList = new ArrayList<>();
    CategoryDTO category;

    int index;

    private void controlImport() {

        if (index < courseList.size() - 1) {
            importUnit(courseList.get(index));
            return;
        }
        Log.w(LOG, "****** controlImport completed");
    }

    private void importUnit(CourseDTO course) {

        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.REGISTER_COURSE);
        w.setCourse(course);
        w.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
        w.setAuthorID(SharedUtil.getAuthor(ctx).getAuthorID());

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        listener.setBusy();
        WebSocketUtil.sendRequest(ctx, Statics.AUTHOR_ENDPOINT,w,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.setNotBusy();
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        Log.w(LOG, "#####  ########## course imported OK");
                        index++;
                        controlImport();
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
//        BaseVolley.getRemoteData(Statics.SERVLET_AUTHOR, w, ctx, new BaseVolley.BohaVolleyListener() {
//            @Override
//            public void onResponseReceived(ResponseDTO response) {
//                listener.setNotBusy();
//                if (response.getStatusCode() > 0) {
//                    ToastUtil.errorToast(ctx, response.getMessage());
//                    return;
//                }
//                Log.w(LOG, "#####  ########## course imported OK");
//                index++;
//                controlImport();
//
//            }
//
//            @Override
//            public void onVolleyError(VolleyError error) {
//                listener.setNotBusy();
//                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.error_server_comms));
//            }
//        });
    }


    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", loc);

    private void setSpinner() {

        List<String> list = new ArrayList<String>();
        list.add("Please select file");
        for (File p : files) {
            list.add(p.getName() + " - " + sdf.format(new Date(p.lastModified())));
        }
        ArrayAdapter a = new ArrayAdapter(ctx, R.layout.xxsimple_spinner_item, list);
        a.setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        fileSpinner.setAdapter(a);
        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (i == 0) {
                        file = null;
                        return;
                    }
                    file = files.get(i - 1);
                    btnImport.setEnabled(true);
                    parseFile();
                } catch (IOException e) {
                    Log.e(LOG, "import_menu failed", e);
                    ToastUtil.errorToast(ctx, "import_menu failed");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    View view;
    Context ctx;
    TextView txtTitle, txtCount;
    Spinner fileSpinner;
    Button btnImport;
    ListView list;
    CourseAdapter adapter;
    ListView listView;
    ImageView image;
    static final String LOG = "ImportFragment";
}
