package com.boha.cmadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmadmin.fragments.AdminListFragment;
import com.boha.cmadmin.fragments.AuthorListFragment;
import com.boha.cmadmin.fragments.ClassListFragment;
import com.boha.cmadmin.fragments.EquipmentListFragment;
import com.boha.cmadmin.fragments.InstructorListFragment;
import com.boha.cmadmin.fragments.TraineeListFragment;
import com.boha.cmadmin.listeners.CameraRequestListener;
import com.boha.cmadmin.listeners.ClassAddedUpdatedListener;
import com.boha.cmadmin.listeners.ContextMenuInterface;
import com.boha.cmadmin.listeners.EquipmentListener;
import com.boha.cmadmin.listeners.InstructorClassRequestListener;
import com.boha.cmadmin.listeners.PageInterface;
import com.boha.cmlibrary.ProfileActivity;
import com.boha.cmlibrary.fragments.ProfileFragment;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.EquipmentDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.PhotoUploadDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PhotoUploadedListener;
import com.boha.coursemaker.util.CacheUtil;
import com.boha.coursemaker.util.ImageUtil;
import com.boha.coursemaker.util.PictureUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import org.acra.ACRA;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main Activity class for the Admin app. Uses a ViewPager to host various fragments,
 * including ClassListFragment, TraineeListFragment, InstructorListFragment, AuthorListFragment,
 * AdminListFragment, EquipmentListFragment.
 *
 * @author aubreyM
 */
public class MainPagerActivity extends FragmentActivity implements BusyListener,
        ContextMenuInterface, EquipmentListener, ClassAddedUpdatedListener,
        InstructorClassRequestListener, CameraRequestListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getApplicationContext();
        setContentView(R.layout.view_pager);
        CompanyDTO company = SharedUtil.getCompany(ctx);
        ACRA.getErrorReporter().putCustomData("companyID", "" + company.getCompanyID());
        ACRA.getErrorReporter().putCustomData("companyName", company.getCompanyName());

        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        administrator = SharedUtil.getAdministrator(ctx);
        if (savedInstanceState != null) {
            response = (ResponseDTO) savedInstanceState
                    .getSerializable("response");
            if (response != null) {
                Log.i(LOG,
                        "--- onCreate, restoring instance data ......buildPages comin up!.");
                buildPages();
            }
        }
    }

    @Override
    public void onPause() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG, "onResume ...nothing to be done");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        Log.d(LOG, "--- onSaveInstanceState ...save response object");
        b.putSerializable("response", response);
        super.onSaveInstanceState(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        mMenu = menu;
        if (response == null)
            CacheUtil.getCachedData(ctx, CacheUtil.CACHE_COMPANY_DATA, new CacheUtil.CacheUtilListener() {
                @Override
                public void onFileDataDeserialized(ResponseDTO r) {
                    Log.d(LOG, "------ onFileDataDeserialized");
                    if (r != null) {
                        response = r;
                        buildPages();
                    }
                    getCompanyDataFromServer();

                }

                @Override
                public void onDataCached() {

                }
            });
        return true;
    }


    private void getCompanyDataFromServer() {

        final RequestDTO req = new RequestDTO();
        req.setRequestType(RequestDTO.GET_COMPANY_DATA);
        req.setCompanyID(SharedUtil.getCompany(getApplicationContext())
                .getCompanyID());

        if (!BaseVolley.checkNetworkOnDevice(ctx))
            return;
        setRefreshActionButtonState(true);

       getData(req);
//        BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req,
//                getApplicationContext(), new BaseVolley.BohaVolleyListener() {
//                    @Override
//                    public void onVolleyError(VolleyError error) {
//                        setRefreshActionButtonState(false);
//                        ToastUtil.errorToast(
//                                getApplicationContext(),
//                                getResources().getString(
//                                        R.string.error_server_comms));
//                    }
//
//                    @Override
//                    public void onResponseReceived(ResponseDTO r) {
//                        setRefreshActionButtonState(false);
//                        response = r;
//                        if (response.getStatusCode() > 0) {
//                            ToastUtil.errorToast(ctx, response.getMessage());
//                            return;
//                        }
//                        buildPages();
//                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_COMPANY_DATA, new CacheUtil.CacheUtilListener() {
//                            @Override
//                            public void onFileDataDeserialized(ResponseDTO response) {
//
//                            }
//
//                            @Override
//                            public void onDataCached() {
//                                Log.d(LOG, "----- onDataCached");
//                            }
//                        });
//                    }
//                });
    }

    private void getData(RequestDTO req) {
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        response = r;
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        buildPages();
                        CacheUtil.cacheData(ctx, response, CacheUtil.CACHE_COMPANY_DATA, new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {
                                Log.d(LOG, "----- onDataCached");
                            }
                        });
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
    }
    ResponseDTO response;
    int currentPageIndex = 0;

    private void buildPages() {
        Bundle data = new Bundle();
        data.putSerializable("response", response);

        classListFragment = new ClassListFragment();
        classListFragment.setArguments(data);
        traineeListFragment = new TraineeListFragment();
        instructorListFragment = new InstructorListFragment();
        authorListFragment = new AuthorListFragment();
        adminListFragment = new AdminListFragment();
        equipmentListFragment = new EquipmentListFragment();

        classListFragment.setArguments(data);
        classListFragment.setFragmentManager(getSupportFragmentManager());
        traineeListFragment.setArguments(data);
        instructorListFragment.setArguments(data);
        authorListFragment.setArguments(data);
        adminListFragment.setArguments(data);
        equipmentListFragment.setArguments(data);

        pageList = new ArrayList<PageInterface>();
        pageList.add(classListFragment);
        pageList.add(traineeListFragment);
        pageList.add(instructorListFragment);
        pageList.add(authorListFragment);
        pageList.add(equipmentListFragment);
        if (administrator != null) {
            if (administrator.getSuperUserFlag() != 0) {
                pageList.add(adminListFragment);
            }
        }
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                currentPageIndex = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) pageList.get(i);
        }

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";
            switch (position) {
                case 0:
                    title = ctx.getResources().getString(R.string.classes);
                    break;
                case 1:
                    title = ctx.getResources().getString(R.string.trainees);
                    break;
                case 2:
                    title = ctx.getResources().getString(R.string.instructors);
                    break;
                case 3:
                    title = ctx.getResources().getString(R.string.content_authors);
                    break;
                case 4:
                    title = ctx.getResources().getString(R.string.equipment);
                    break;
                case 5:
                    title = ctx.getResources().getString(R.string.administrators);
                    break;

                default:
                    break;
            }
            return title;
        }
    }

    Menu mMenu;

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.menu_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(LOG, "onOptionsItemSelected - " + item.getTitle()
                + " pageIndex: " + currentPageIndex);
        switch (item.getItemId()) {

            case R.id.menu_add:

                switch (currentPageIndex) {
                    case 0:
                        classListFragment.addNewClass();
                        break;
                    case 1:
                        traineeListFragment.addTrainee();
                        break;
                    case 2:
                        instructorListFragment.addInstructor();
                        break;
                    case 3:
                        authorListFragment.addAuthor();
                        break;
                    case 4:
                        equipmentListFragment.addEquipment();
                        break;
                    case 5:
                        adminListFragment.addAdministrator();
                        break;
                }

                return true;

            case R.id.menu_equipment:
                mPager.setCurrentItem(4, true);
                return true;
            case R.id.menu_author:
                mPager.setCurrentItem(3, true);
                return true;
            case R.id.menu_trainees:
                mPager.setCurrentItem(1, true);
                return true;
            case R.id.menu_class:
                mPager.setCurrentItem(0, true);
                return true;
            case R.id.menu_instructors:
                mPager.setCurrentItem(2, true);
                return true;
            case R.id.menu_admin:
                if (pageList.size() == 6)
                    mPager.setCurrentItem(5, true);
                return true;
            case R.id.menu_profile:
                Intent ix = new Intent(ctx, ProfileActivity.class);
                ix.putExtra("type", ProfileFragment.ADMIN);
                startActivity(ix);
                return true;
            case R.id.menu_refresh:
                getCompanyDataFromServer();
                return true;
            case R.id.menu_rating:
                Intent i = new Intent(ctx, LookupActivity.class);
                i.putExtra("pType", LookupActivity.RATING);
                startActivity(i);
                return true;
            case R.id.menu_helptype:
                Intent m = new Intent(ctx, LookupActivity.class);
                m.putExtra("pType", LookupActivity.HELPTYPE);
                startActivity(m);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    ViewPager mPager;
    List<PageInterface> pageList = new ArrayList<PageInterface>();
    Context ctx;
    PagerAdapter mAdapter;
    ClassListFragment classListFragment;
    TraineeListFragment traineeListFragment;
    InstructorListFragment instructorListFragment;
    AuthorListFragment authorListFragment;
    AdminListFragment adminListFragment;
    EquipmentListFragment equipmentListFragment;

    @Override
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }

    @Override
    public void redirectMenuItem(MenuItem menuItem) {
        Log.w(LOG, "Redirecting menu selection: " + menuItem.getTitle());
        switch (menuItem.getItemId()) {
            case R.id.menu_update_class:
                classListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_update_trainee:
                traineeListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_delete_trainee:
                traineeListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_equipment_trainee:
                traineeListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_update_instructor:
                instructorListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_delete_instructor:
                instructorListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_update_admin:
                adminListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_delete_admin:
                adminListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_update_author:
                authorListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_delete_author:
                authorListFragment.onContextItemSelected(menuItem);
                break;
            case R.id.menu_send_trainee_password:
                traineeListFragment.onContextItemSelected(menuItem);
                break;

            default:
                break;
        }

    }

    AdministratorDTO administrator;
    static final String LOG = "AdminActivity";

    @Override
    public void onEquipmentSelected(EquipmentDTO selectedEquipment) {
        Intent i = new Intent(ctx, InventoryPagerActivity.class);
        i.putExtra("equipment", selectedEquipment);
        ResponseDTO r = new ResponseDTO();
        r.setTrainingClassList(response.getTrainingClassList());
        i.putExtra("response", r);
        startActivityForResult(i, INVENTORY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(LOG, "onActivityResult -- requestCode: " + requestCode
                + " resultCode: " + resultCode);
        if (requestCode == INVENTORY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                final ResponseDTO resp = (ResponseDTO) data.getExtras()
                        .getSerializable("response");
                final int id = data.getExtras().getInt("equipmentID");
                equipmentListFragment.upgradeEquipment(Integer.valueOf(id),
                        resp.getInventoryList());
            }
        }
        if (requestCode == REQUEST_INSTRUCTOR_CLASS) {
            if (resultCode == Activity.RESULT_OK) {
                ResponseDTO r = (ResponseDTO) data
                        .getSerializableExtra("response");
                Log.w(LOG, "requesting refresh from instructorListFragment");
                instructorListFragment.refreshInstructor(r.getInstructor());
            }
        }
        if (requestCode == CAPTURE_IMAGE) {
            Log.i(LOG, "back from camera, should resize and store");
            if (resultCode == Activity.RESULT_OK) {
                new PhotoTask().execute();
            } else {
                Log.e(LOG, "back from camera, activity result code not OK");
                ToastUtil.toast(
                        ctx,
                        ctx.getResources().getString(
                                R.string.image_capture_cancelled));
            }
        }

    }

    private void startInstructorClass(InstructorDTO instructor) {
        Intent i = new Intent(ctx, InstructorClassActivity.class);
        ResponseDTO r = new ResponseDTO();
        r.setInstructor(instructor);
        r.setTrainingClassList(response.getTrainingClassList());
        i.putExtra("response", r);
        startActivityForResult(i, REQUEST_INSTRUCTOR_CLASS);
    }

    static final int INVENTORY_REQUEST = 303, REQUEST_INSTRUCTOR_CLASS = 404;
    ;

    @Override
    public void onClassAdded(TrainingClassDTO tc) {
        Log.d(LOG, "onClassAdded - " + tc.getTrainingClassName()
                + " - traineeListFragment add nudge");
        getCompanyDataFromServer();
    }

    @Override
    public void onClassUpdated(TrainingClassDTO tc) {

    }

    @Override
    public void onClassDeleted(TrainingClassDTO tc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInstructorClassRequested(InstructorDTO instructor) {
        Log.d(LOG, "onInstructorClassRequested, starting activity...");
        if (instructor == null) throw new UnsupportedOperationException("Instructor is null");
        startInstructorClass(instructor);

    }

    Uri fileUri;
    public static final int CAPTURE_IMAGE = 309;
    int requestorType, ID;

    @Override
    public void onCameraRequested(int width, int height, int requestorType, int ID) {
        this.requestorType = requestorType;
        this.ID = ID;
        Log.i(LOG, "onCameraRequested - requestor type = " + requestorType);
        dispatchTakePictureIntent();

    }

    File photoFile;

    class PhotoTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            ExifInterface exif = null;
            //fileUri = Uri.fromFile(photoFile);

            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
                String orient = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                Log.e("pic", "Orientation says: " + orient);
                float rotate = 0;
                if (orient.equalsIgnoreCase("6")) {
                    rotate = 90f;
                }
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                    getLog(bm, "Raw Camera");
                    //scale and rotate for the screen
                    Matrix matrix = new Matrix();
                    matrix.postScale(1.0f, 1.0f);
                    matrix.postRotate(rotate);
                    bitmapForScreen = Bitmap.createBitmap
                            (bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    getLog(bitmapForScreen, "Screen");
                    //get thumbnail for upload
                    Matrix matrixThumbnail = new Matrix();
                    matrixThumbnail.postScale(0.4f, 0.4f);
                    //matrixThumbnail.postRotate(rotate);
                    Bitmap thumb = Bitmap.createBitmap
                            (bitmapForScreen, 0, 0, bitmapForScreen.getWidth(),
                                    bitmapForScreen.getHeight(), matrixThumbnail, true);
                    getLog(thumb, "Thumb");
                    //get resized "full" size for upload
                    Matrix matrixF = new Matrix();
                    matrixF.postScale(0.6f, 0.6f);
                    //matrixF.postRotate(rotate);
                    Bitmap fullBm = Bitmap.createBitmap
                            (bitmapForScreen, 0, 0, bitmapForScreen.getWidth(),
                                    bitmapForScreen.getHeight(), matrixF, true);
                    getLog(fullBm, "Full");
                    currentFullFile = ImageUtil.getFileFromBitmap(fullBm, "m" + System.currentTimeMillis() + ".jpg");
                    currentThumbFile = ImageUtil.getFileFromBitmap(thumb, "t" + System.currentTimeMillis() + ".jpg");
                    thumbUri = Uri.fromFile(currentThumbFile);
                    fullUri = Uri.fromFile(currentFullFile);
                    getFileLengths();

                    PhotoUploadDTO p = new PhotoUploadDTO();
                    p.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());

                    p.setNumberOfImages(1);
                    p.setType(PhotoUploadDTO.ADMINISTRATOR);

                    Log.w(LOG, "...requesting  photo upload");
                    PictureUtil.uploadPhoto(SharedUtil.getCompany(ctx).getCompanyID(), ID,
                            thumbUri.toString(), requestorType, ctx, new PhotoUploadedListener() {

                                @Override
                                public void onPhotoUploaded() {
                                    Log.i(LOG, " photo has been uploaded");
                                }

                                @Override
                                public void onPhotoUploadFailed() {
                                    Log.e(LOG, "Problem uploading photo");

                                }
                            });


                } catch (Exception e) {
                    Log.e("pic", "Fuck it!", e);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }


            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                ToastUtil.errorToast(ctx, ctx.getResources().getString(R.string.camera_error));
                return;
            }
            Log.i(LOG, "-------------------------> onPostExecute, to refresh fragment - actor's picture");
            //TODO - force appropriate fragment to refresh image ....
            switch (requestorType) {
                case PhotoUploadDTO.ADMINISTRATOR:
                    break;
                case PhotoUploadDTO.AUTHOR:
                    break;
                case PhotoUploadDTO.INSTRUCTOR:
                    //instructorListFragment.setBitmaps(bitmaps);
                    break;
                case PhotoUploadDTO.TRAINEE:
                    //traineeListFragment.setBitmaps(bitmaps);
                    break;

                default:
                    break;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("pic", "Fuck!", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void getLog(Bitmap bm, String which) {
        Log.e(LOG, which + " - bitmap: width: "
                + bm.getWidth() + " height: "
                + bm.getHeight() + " rowBytes: "
                + bm.getRowBytes());
    }

    private void getFileLengths() {
        Log.i(LOG, "Thumbnail file length: " + currentThumbFile.length());
        Log.i(LOG, "Full file length: " + currentFullFile.length());

    }

    Bitmap bitmapForScreen;
    File currentThumbFile, currentFullFile;
    Uri thumbUri, fullUri;
}
