package com.boha.cmlibrary;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.boha.coursemaker.util.Statics;
import com.boha.volley.toolbox.BitmapLruCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by aubreyM on 2014/07/02.
 */
@ReportsCrashes(
        formKey = "",
        formUri = Statics.CRASH_REPORTS_URL,
        customReportContent = {ReportField.APP_VERSION_NAME, ReportField.APP_VERSION_CODE,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.STACK_TRACE,
                ReportField.PACKAGE_NAME,
                ReportField.CUSTOM_DATA,
                ReportField.LOGCAT},
        socketTimeout = 3000
)
public class CMApp extends Application implements Thread.UncaughtExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG, "############################ onCreate CMApp has started ---------------->");

        ACRA.init(this);
        Log.e(LOG, "###### ACRA Crash Reporting has been initiated");
        initializeVolley(getApplicationContext());



    }
    public void initializeVolley(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        bitmapLruCache = new BitmapLruCache(cacheSize);
        imageLoader = new ImageLoader(requestQueue, bitmapLruCache);

        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public BitmapLruCache getBitmapLruCache() {
        return bitmapLruCache;
    }

    ImageLoader imageLoader;
    RequestQueue requestQueue;
    BitmapLruCache bitmapLruCache;
    static final String LOG = "CMApp";
    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-53661372-1";

    public static int GENERAL_TRACKER = 0;

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(LOG,"UncaughtExceptionHandler throwable: " + throwable.getMessage());
    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

}
