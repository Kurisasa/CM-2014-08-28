package com.boha.coursemaker.base;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmlibrary.R;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.ErrorUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebCheck;
import com.boha.coursemaker.util.WebCheckResult;
import com.boha.volley.toolbox.BohaRequest;
import com.boha.volley.toolbox.BohaVolley;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility class to encapsulate calls to the remote server via the Volley Networking library.
 * Uses BohaVolleyListener to inform caller on status of the communications request
 * @author Aubrey Malabie
 *
 */
public class BaseVolley  {

    /**
     * Informs whoever implements this interface when a communications request is concluded
     */
    public interface BohaVolleyListener {
        public void onResponseReceived(ResponseDTO response);
        public void onVolleyError(VolleyError error);
    }

    public static void setVolley(Context ctx) {
        requestQueue = BohaVolley.getRequestQueue(ctx);
    }

    static BohaVolleyListener bohaVolleyListener;

    public static boolean checkNetworkOnDevice(Context context) {
        ctx = context;
        WebCheckResult r = WebCheck.checkNetworkAvailability(ctx);
        if (r.isNetworkUnavailable()) {
            ToastUtil.errorToast(
                    ctx,
                    ctx.getResources().getString(
                            R.string.error_network_unavailable)
            );
            return false;
        }

        return true;
    }

    /**
     * This method gets a Volley based communications request started
     * @param suffix the suffix pointing to the destination servlet
     * @param request the request object in JSON format
     * @param context the Activity context
     * @param listener the listener implementor who wants to know abdout call status
     */
    public static void getRemoteData(String suffix, RequestDTO request,
                                     Context context, BohaVolleyListener listener) {
        ctx = context;
        bohaVolleyListener = listener;
        if (requestQueue == null) {
            requestQueue = BohaVolley.getRequestQueue(ctx);
        }
        String json = null, jj = null;

        Gson gson = new Gson();
        try {
            jj = gson.toJson(request);
            json = URLEncoder.encode(jj, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        retries = 0;
        Log.i(LOG, "...sending remote request: .......>\n" + Statics.URL + suffix + jj);
        bohaRequest = new BohaRequest(Method.POST, Statics.URL + suffix + json,
                onSuccessListener(), onErrorListener());
        bohaRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, 2));
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        // DefaultRetryPolicy.DEFAULT_MAX_RETRIES
        requestQueue.add(bohaRequest);
    }

    private static Response.Listener<ResponseDTO> onSuccessListener() {
        return new Response.Listener<ResponseDTO>() {
            @Override
            public void onResponse(ResponseDTO r) {
                response = r;
                Log.e(LOG, "Yup! ...response object received, status code: " + r.getStatusCode());
                if (r.getStatusCode() > 0) {
                    Log.w(LOG, response.getMessage());
                }
                bohaVolleyListener.onResponseReceived(response);

            }
        };
    }

    static int retries;

    private static Response.ErrorListener onErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorUtil.logVolleyError(ctx, error);
                if (error instanceof TimeoutError) {
                    Log.i(LOG, "Retrying after timeout error ...");
                    retries++;
                    if (retries < 3)
                        requestQueue.add(bohaRequest);
                    return;
                }
                if (error instanceof NetworkError) {
                    NetworkError ne = (NetworkError) error;
                    if (ne.networkResponse != null) {
                        Log.w(LOG, "volley http status code: "
                                + ne.networkResponse.statusCode);
                    }
                    Log.e(LOG, ctx.getResources().getString(
                            R.string.error_server_unavailable));

                } else {
                    Log.e(LOG, ctx.getResources().getString(
                            R.string.error_server_comms));
                }
                bohaVolleyListener.onVolleyError(error);
            }
        };
    }

    private static ResponseDTO response;
    private static Context ctx;
    protected static BohaRequest bohaRequest;
    protected static RequestQueue requestQueue;
    protected ImageLoader imageLoader;
    static final String LOG = "BaseVolley";
}
