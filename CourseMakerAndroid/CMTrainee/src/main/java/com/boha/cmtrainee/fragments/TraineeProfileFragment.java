package com.boha.cmtrainee.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.boha.cmtrainee.R;
import com.boha.cmtrainee.interfaces.ImageCaptureListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CityDTO;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.ProvinceDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.DiskImageListener;
import com.boha.coursemaker.listeners.FileListener;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.listeners.PhotoUploadedListener;
import com.boha.coursemaker.util.AsyncImageUtil;
import com.boha.coursemaker.util.FileTask;
import com.boha.coursemaker.util.PictureUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.volley.toolbox.BohaVolley;

import java.util.ArrayList;
import java.util.List;

public class TraineeProfileFragment extends Fragment implements PageInterface {

    public TraineeProfileFragment() {
    }

    ImageCaptureListener imageCaptureListener;

    @Override
    public void onAttach(Activity a) {

        if (a instanceof BusyListener) {
            busyListener = (BusyListener) a;
        } else {
            throw new UnsupportedOperationException("Activity "
                    + a.getLocalClassName() + " must implement BusyListener");
        }
        if (a instanceof ImageCaptureListener) {
            imageCaptureListener = (ImageCaptureListener) a;
        } else {
            throw new UnsupportedOperationException("Activity "
                    + a.getLocalClassName()
                    + " must implement ImageCaptureListener");
        }
        super.onAttach(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saved) {

        Log.e(LOG, "## onCreateView ");
        ctx = getActivity();
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_trainee_profile, container,
                false);
        BohaVolley.initialize(ctx);
        imageLoader = BohaVolley.getImageLoader(ctx);
        trainee = SharedUtil.getTrainee(ctx);
        setFields();
        Bundle b = getArguments();
        if (b != null) {
            ResponseDTO r = (ResponseDTO) b.getSerializable("response");
            provinceList = r.getProvinceList();
            setProvinceSpinner();
            isFirstTime = true;
            loadForm();
        }

        Animation an = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in);
        an.setDuration(500);
        view.startAnimation(an);
        return view;
    }

    @Override
    public void onResume() {
        Log.e(LOG, "## onResume in " + LOG);
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
        super.onSaveInstanceState(state);
    }

    private void setProfilePicture() {
        Log.d(LOG, "*** attempting to set profile picture");
        image = (ImageView) view.findViewById(R.id.PROF_image);
        String uri = SharedUtil.getImageUri(ctx);
        if (uri != null) {
            AsyncImageUtil.getBitmapFromUri(Uri.parse(uri), ctx,
                    new DiskImageListener() {

                        @Override
                        public void onError() {
                            ToastUtil.errorToast(ctx, ctx.getResources()
                                    .getString(R.string.error_image_save));
                            return;
                        }

                        @Override
                        public void onBitmapReturned(Bitmap bitmap) {
                            image.setImageBitmap(bitmap);
                        }
                    }
            );
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Statics.IMAGE_URL).append("company")
                .append(trainee.getCompanyID()).append("/trainee/");
        sb.append(trainee.getTraineeID()).append(".jpg");
        imageLoader.get(sb.toString(), new ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(LOG, "No photo was found for download");
            }

            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                image.setImageBitmap(response.getBitmap());
                FileTask.saveFileFromBitmap(response.getBitmap(), ctx,
                        new FileListener() {

                            @Override
                            public void onFileSaved() {
                                ToastUtil.toast(ctx, ctx.getResources()
                                        .getString(R.string.data_saved));
                            }

                            @Override
                            public void onError() {
                                ToastUtil.errorToast(ctx, ctx.getResources()
                                        .getString(R.string.error_image_save));
                            }
                        }
                );
            }
        });

    }

    boolean pictureChanged;

    private void submit() {

        String uri = SharedUtil.getThumbUri(ctx);
        if (uri != null) {
            PictureUtil.uploadPhoto(trainee.getCompanyID(),
                    trainee.getTraineeID(), uri, PhotoUploadDTO.TRAINEE, ctx,
                    new PhotoUploadedListener() {

                        @Override
                        public void onPhotoUploaded() {
                            pictureChanged = false;
                            ToastUtil.toast(
                                    ctx,
                                    ctx.getResources().getString(
                                            R.string.profile_picture_uploaded)
                            );
                        }

                        @Override
                        public void onPhotoUploadFailed() {
                            pictureChanged = true;

                        }
                    }
            );
        }

        if (editFirstName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(R.string.enter_firstname));
            return;
        }
        if (editLastName.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(R.string.enter_lastname));
            return;
        }
        if (editCell.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(R.string.label_cellphone));
            return;
        }
        if (editIDNumber.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(R.string.enter_idnumber));
            return;
        }
        if (editPassword.getText().toString().isEmpty()) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(R.string.enter_password));
            return;
        }

        if (city == null) {
            ToastUtil.errorToast(ctx,
                    ctx.getResources().getString(R.string.select_city));
            return;
        }

        RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.UPDATE_TRAINEE);
        TraineeDTO t = new TraineeDTO();
        t.setTraineeID(trainee.getTraineeID());
        t.setFirstName(editFirstName.getText().toString());
        t.setLastName(editLastName.getText().toString());
        t.setiDNumber(editIDNumber.getText().toString());
        trainee.setiDNumber(t.getiDNumber());
        trainee.setFirstName(t.getFirstName());
        trainee.setLastName(t.getLastName());
        t.setCellphone(editCell.getText().toString());
        trainee.setCellphone(t.getCellphone());
        t.setPassword(editPassword.getText().toString());

        if (!editAddress.getText().toString().isEmpty()) {
            t.setAddress(editAddress.getText().toString());
            trainee.setAddress(t.getAddress());
        }
        if (radioFemale.isChecked()) {
            t.setGender(Integer.valueOf(2));
            trainee.setGender(Integer.valueOf(2));
        } else {
            t.setGender(Integer.valueOf(1));
            trainee.setGender(Integer.valueOf(1));
        }
        t.setProvinceID(province.getProvinceID());
        trainee.setProvinceID(t.getProvinceID());
        t.setCityID(city.getCityID());
        trainee.setCityID(t.getCityID());
        SharedUtil.saveTrainee(ctx, trainee);

        req.setTrainee(t);
        if (!BaseVolley.checkNetworkOnDevice(ctx))
            return;

        busyListener.setBusy();
        btnSubmit.setEnabled(false);
        WebSocketUtil.sendRequest(ctx, Statics.TEAM_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        busyListener.setNotBusy();
                        btnSubmit.setEnabled(true);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }

                        ToastUtil.toast(
                                ctx,
                                ctx.getResources().getString(
                                        R.string.data_saved));
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
//        BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, req, ctx,
//                new BohaVolleyListener() {
//
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        busyListener.setNotBusy();
//                        btnSubmit.setEnabled(true);
//                        ToastUtil.errorToast(
//                                ctx,
//                                ctx.getResources().getString(
//                                        R.string.error_server_comms)
//                        );
//
//                    }
//
//                    @Override
//                    public void onResponseReceived(ResponseDTO response) {
//                        busyListener.setNotBusy();
//                        btnSubmit.setEnabled(true);
//                        if (response.getStatusCode() > 0) {
//                            ToastUtil.errorToast(ctx, response.getMessage());
//                            return;
//                        }
//
//                        ToastUtil.toast(
//                                ctx,
//                                ctx.getResources().getString(
//                                        R.string.data_saved)
//                        );
//
//                    }
//                }
//        );

    }

    private void selectImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getResources().getString(R.string.image_source));
        builder.setItems(
                new CharSequence[]{
                        ctx.getResources().getString(R.string.gallery),
                        ctx.getResources().getString(R.string.camera)},
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startGalleryIntent();
                                break;
                            case 1:
                                startCameraIntent();
                                break;

                            default:
                                break;
                        }

                    }
                }
        );

        builder.show();
    }

    private void setFields() {
        btnSubmit = (Button) view.findViewById(R.id.PROF_submit);
        btnTakePic = (Button) view.findViewById(R.id.PROF_takePic);
        editAddress = (EditText) view.findViewById(R.id.PROF_address);
        editIDNumber = (EditText) view.findViewById(R.id.PROF_idNumber);
        editFirstName = (EditText) view.findViewById(R.id.PROF_firstName);
        editLastName = (EditText) view.findViewById(R.id.PROF_lastName);
        editCell = (EditText) view.findViewById(R.id.PROF_cell);
        editPassword = (EditText) view.findViewById(R.id.PROF_password);
        provinceSpinner = (Spinner) view
                .findViewById(R.id.PROF_provinceSpinner);
        citySpinner = (Spinner) view.findViewById(R.id.PROF_citySpinner);

        editIDNumber.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateTrainee();

            }
        });
        editAddress.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateTrainee();

            }
        });
        image = (ImageView) view.findViewById(R.id.PROF_image);
        setProfilePicture();
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImageSource();
            }
        });

        radioFemale = (RadioButton) view.findViewById(R.id.PROF_radioFemale);
        radioMale = (RadioButton) view.findViewById(R.id.PROF_radioMale);
        radioFemale.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    trainee.setGender(2);
                    updateTrainee();
                }
            }
        });
        radioMale.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    trainee.setGender(1);
                    updateTrainee();
                }
            }
        });

        if (trainee.getGender() == 1)
            radioMale.setChecked(true);

        if (trainee.getGender() == 2) {
            radioFemale.setChecked(true);
        }

        if (trainee.getiDNumber() != null) {
            editIDNumber.setText(trainee.getiDNumber());
        }

        if (trainee.getAddress() != null) {
            editAddress.setText(trainee.getAddress());
        }

        Animation an = AnimationUtils.loadAnimation(ctx, R.anim.fade_in);
        an.setDuration(1000);
        image.startAnimation(an);

    }

    private void loadForm() {
        editFirstName.setText(trainee.getFirstName());
        editLastName.setText(trainee.getLastName());
        if (trainee.getCellphone() != null) {
            editCell.setText(trainee.getCellphone());
        }
        if (trainee.getAddress() != null)
            editAddress.setText(trainee.getAddress());
        if (trainee.getGender() == 1) {
            radioMale.setChecked(true);
        }
        if (trainee.getGender() == 2) {
            radioFemale.setChecked(true);
        }


        if (trainee.getPassword() != null) {
            editPassword.setText(trainee.getPassword());
        }
        if (trainee.getiDNumber() != null)
            editIDNumber.setText(trainee.getiDNumber());


        int i = getProvinceIndex(trainee.getProvinceID());
        if (i > -1) {
            Log.d(LOG, "loadForm - detected province, setting selection");
            provinceSpinner.setSelection(i + 1, true);
        }

    }

    private int getCityIndex(int id) {
        int cnt = 0;
        Log.d(LOG, "prov cities: " + cityList.size());
        if (cityList == null)
            return 0;
        for (CityDTO p : cityList) {
            if (p.getCityID() == id) {
                Log.d(LOG, "Trainee city found: " + p.getCityName()
                        + " index: " + cnt);
                return cnt;
            }
            cnt++;
        }
        return -1;
    }

    private int getProvinceIndex(Integer id) {
        int cnt = 0;
        if (provinceList == null)
            return 0;
        for (ProvinceDTO p : provinceList) {
            if (p.getProvinceID() == id) {
                return cnt;
            }
            cnt++;
        }

        return -1;
    }

    private void updateTrainee() {

        if (!editIDNumber.getText().toString().isEmpty()) {
            trainee.setiDNumber(editIDNumber.getText().toString());
        }
        if (!editAddress.getText().toString().isEmpty()) {
            trainee.setAddress(editAddress.getText().toString());
        }

        SharedUtil.saveTrainee(ctx, trainee);
    }

    private void startCameraIntent() {
        imageCaptureListener.onCameraRequest(image.getWidth(),
                image.getHeight());
    }

    private void startGalleryIntent() {
        imageCaptureListener.onGalleryRequest();

    }

    public void setImage(Bitmap bm) {
        image.setImageBitmap(bm);
        pictureChanged = true;
    }

    public void setmImageLoader(ImageLoader mImageLoader) {
        this.imageLoader = mImageLoader;
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editIDNumber.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editIDNumber.getWindowToken(), 0);
    }

    private boolean isFirstTime;

    private void setProvinceSpinner() {
        Log.d(LOG, "setting provinceSpinner ...");
        if (provinceSpinner == null) {
            return;
        }

        final ArrayList<String> tarList = new ArrayList<String>();
        if (provinceList != null) {
            tarList.add(ctx.getResources().getString(R.string.select_prov));
            for (ProvinceDTO p : provinceList) {
                tarList.add(p.getProvinceName());
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
                    R.layout.xxsimple_spinner_item, tarList);
            dataAdapter
                    .setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
            provinceSpinner.setAdapter(dataAdapter);
            provinceSpinner
                    .setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {
                            if (arg2 == 0) {
                                province = null;
                                return;
                            }
                            province = provinceList.get(arg2 - 1);
                            cityList = province.getCityList();
                            setCitySpinner();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
        }

    }

    private void setCitySpinner() {
        final ArrayList<String> tarList = new ArrayList<String>();
        if (cityList != null) {
            tarList.add(ctx.getResources().getString(R.string.select_city));
            for (CityDTO p : cityList) {
                tarList.add(p.getCityName());
            }
        }
        Log.d(LOG, "setting citySpinner ... tarList: " + tarList.size());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
                R.layout.xxsimple_spinner_item, tarList);
        dataAdapter
                .setDropDownViewResource(R.layout.xxsimple_spinner_dropdown_item);
        citySpinner.setAdapter(dataAdapter);
        if (isFirstTime) {
            isFirstTime = !isFirstTime;
            int index = getCityIndex(trainee.getCityID());
            if (index > -1)
                citySpinner.setSelection(index + 1);
        }

        citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (arg2 == 0) {
                    city = null;
                    return;
                }
                city = cityList.get(arg2 - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    static final String LOG = "TraineeProfileFragment";
    private ImageLoader imageLoader;
    private Context ctx;
    private TraineeDTO trainee;
    private BusyListener busyListener;

    private List<ProvinceDTO> provinceList;
    private List<CityDTO> cityList;
    private Spinner provinceSpinner, citySpinner;
    private ProvinceDTO province;
    private CityDTO city;
    ResponseDTO response;
    View view;
    TextView txtSkills, txtQuals, txtName;
    EditText editIDNumber, editAddress, editFirstName, editLastName, editCell,
            editPassword;
    RadioButton radioMale, radioFemale;
    ImageView image;
    Button btnTakePic, btnSubmit, btnAddSkill, btnAddQual, btnRemoveSkill,
            btnRemoveQual;
    public static final int CAPTURE_IMAGE = 3033, PICK_IMAGE = 4044;

}
