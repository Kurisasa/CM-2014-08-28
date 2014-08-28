package com.boha.coursemaker.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.boha.coursemaker.dto.ResponseDTO;
import com.google.gson.Gson;

import java.io.*;

/**
 * Created by aubreyM on 2014/06/30.
 */
public class CacheUtil {

    public interface CacheUtilListener {
        public void onFileDataDeserialized(ResponseDTO response);

        public void onDataCached();
    }

    static CacheUtilListener listener;
    public static final int CACHE_CATEGORIES = 1, CACHE_RATINGS = 2,
            CACHE_HELPTYPES = 3, CACHE_DATA = 4, CACHE_COMPANY_DATA = 7,
            CACHE_TRAINEE_CLASS_LIST = 5, CACHE_TRAINEE_ACTIVITY = 6, CACHE_PROVINCES = 8;
    static int dataType, classID;
    static ResponseDTO response;
    static Context ctx;
    static final String JSON_CATEGORIES = "categories.json",
                JSON_RATINGS = "ratings.json", JSON_HELPTYPES = "helptypes.json",JSON_COMPANY_DATA = "companydata.json",
            JSON_DATA = "data.json", JSON_TRAINEE_LIST = "traineeList.json",
            JSON_TRAINEE_ACTIVITY = "traineeActivity", JSON_PROVINCES = "provinces.json";

    public static void cacheData(Context context, ResponseDTO r, int type, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        listener = cacheUtilListener;
        ctx = context;
        new CacheTask().execute();
    }
    public static void cacheData(Context context, ResponseDTO r, int type, int id, CacheUtilListener cacheUtilListener) {
        dataType = type;
        response = r;
        listener = cacheUtilListener;
        classID = id;
        ctx = context;
        new CacheTask().execute();
    }

    public static void getCachedData(Context context, int type, CacheUtilListener cacheUtilListener) {
        Log.d(LOG, "################ getting cached data ..................");
        dataType = type;
        listener = cacheUtilListener;
        ctx = context;
        new CacheRetrieveTask().execute();
    }
    public static void getCachedData(Context context, int type, int id, CacheUtilListener cacheUtilListener) {
        Log.d(LOG, "################ getting cached data ..................");
        dataType = type;
        listener = cacheUtilListener;
        ctx = context;
        classID = id;
        new CacheRetrieveTask().execute();
    }


    static class CacheTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String json = null;
            File file = null;
            FileOutputStream outputStream;
            try {
                switch (dataType) {

                    case CACHE_PROVINCES:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_PROVINCES, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_PROVINCES);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_COMPANY_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_COMPANY_DATA, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_COMPANY_DATA);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_TRAINEE_ACTIVITY:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_TRAINEE_ACTIVITY, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_TRAINEE_ACTIVITY);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;


                    case CACHE_TRAINEE_CLASS_LIST:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_TRAINEE_LIST + classID, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_TRAINEE_LIST + classID);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_DATA:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_DATA, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_DATA);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_RATINGS:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_RATINGS, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_RATINGS);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length() );
                        }
                        break;
                    case CACHE_HELPTYPES:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_HELPTYPES, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_HELPTYPES);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    case CACHE_CATEGORIES:
                        json = gson.toJson(response);
                        outputStream = ctx.openFileOutput(JSON_CATEGORIES, Context.MODE_PRIVATE);
                        write(outputStream, json);
                        file = ctx.getFileStreamPath(JSON_CATEGORIES);
                        if (file != null) {
                            Log.e(LOG, "......Data cache json written to disk,  - path: " + file.getAbsolutePath() +
                                    " - length: " + file.length());
                        }
                        break;
                    default:
                        Log.e(LOG, "######### NOTHING done ...");
                        break;

                }

            } catch (IOException e) {
                Log.e(LOG, "Failed to cache data", e);
            }
            return null;
        }

        private void write(FileOutputStream outputStream, String json) throws IOException {
            outputStream.write(json.getBytes());
            outputStream.close();
        }

        @Override
        protected void onPostExecute(Void v) {
            listener.onDataCached();
        }
    }

    static class CacheRetrieveTask extends AsyncTask<Void, Void, ResponseDTO> {

        private ResponseDTO getData(FileInputStream stream) throws IOException {
            String json = getStringFromInputStream(stream);
            ResponseDTO response = gson.fromJson(json, ResponseDTO.class);
            if (response == null) {
                response = new ResponseDTO();
            }
            return response;
        }

        @Override
        protected ResponseDTO doInBackground(Void... voids) {
            ResponseDTO response = null;
            FileInputStream stream;
            Log.d(LOG, "########### doInBackground: getting cached data ....");
            try {
                switch (dataType) {
                    case CACHE_COMPANY_DATA:
                        stream = ctx.openFileInput(JSON_COMPANY_DATA);
                        response = getData(stream);
                        break;
                    case CACHE_PROVINCES:
                        stream = ctx.openFileInput(JSON_PROVINCES);
                        response = getData(stream);
                        break;
                    case CACHE_TRAINEE_ACTIVITY:
                        stream = ctx.openFileInput(JSON_TRAINEE_ACTIVITY);
                        response = getData(stream);
                        break;
                    case CACHE_TRAINEE_CLASS_LIST:
                        stream = ctx.openFileInput(JSON_TRAINEE_LIST + classID);
                        response = getData(stream);
                        break;
                    case CACHE_CATEGORIES:
                        stream = ctx.openFileInput(JSON_CATEGORIES);
                        response = getData(stream);
                        break;
                    case CACHE_RATINGS:
                        stream = ctx.openFileInput(JSON_RATINGS);
                        response = getData(stream);
                        break;
                    case CACHE_HELPTYPES:
                        stream = ctx.openFileInput(JSON_HELPTYPES);
                        response = getData(stream);
                        break;
                    case CACHE_DATA:
                        stream = ctx.openFileInput(JSON_DATA);
                        response = getData(stream);
                        break;

                }

            } catch (IOException e) {
                Log.e(LOG, "Failed to retrieve cache", e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(ResponseDTO v) {
            if (v != null) {
                Log.e(LOG, "$$$$$$$$$$$$ cached data retrieved");
            }
            listener.onFileDataDeserialized(v);
        }
    }


    private static String getStringFromInputStream(InputStream is) throws IOException {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
        String json = sb.toString();
        return json;

    }

    static final String LOG = "CacheUtil";
    static final Gson gson = new Gson();
}
