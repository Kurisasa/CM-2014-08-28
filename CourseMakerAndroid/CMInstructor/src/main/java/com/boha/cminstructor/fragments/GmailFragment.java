package com.boha.cminstructor.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.boha.cminstructor.R;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;

public class GmailFragment extends Fragment implements PageInterface {

	public GmailFragment() {
	}
	static final String LOG = "GmailFragment";

	BusyListener listener;
	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			listener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException("Host Activity must implement BusyListener");
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
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_gmail, container, false);
		setFields();

		return view;
	}

	private void setFields() {
		webView = (WebView) view.findViewById(R.id.GMAIL_webview);

		webView.setWebViewClient(new MyWebViewClient());
		webView.setInitialScale(1);
		webView.getSettings().setJavaScriptEnabled(true);
		// webView.addJavascriptInterface(new WebAppInterface(this), "Android");

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.getSettings().setDomStorageEnabled(true);

		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setScrollbarFadingEnabled(false);
		
		webView.loadUrl(GOOGLE_URL);
	}
	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView web, String url) {
			Log.i(LOG, "--onPageFinished --- title: " + web.getTitle()
					+ " - url: " + web.getUrl());
			currentTitle = web.getTitle();
			currentURL = web.getUrl();
			end = System.currentTimeMillis();
			listener.setNotBusy();
			Toast.makeText(ctx, "Page: " + currentTitle +
					" - loaded in " + (end-start)/1000 + " seconds", Toast.LENGTH_SHORT).show();
			Log.e(LOG, "--onPageFinished -- elapsed loading time: " + (end-start)/1000 + " seconds");
			super.onPageFinished(web, url);
		}

		@Override
		public void onReceivedError(WebView web, int errorCode,
				String description, String failingURL) {
			listener.setNotBusy();
			Log.i(LOG, "--onReceivedError code: " + errorCode + " "
					+ description + " - failing: " + failingURL);
		}
		@Override
		public void onPageStarted(WebView web, String url, Bitmap favIcon) {
			Log.e(LOG, "--onPageStarted url: " + url);
			start = System.currentTimeMillis();
			listener.setBusy();
		}
	}
	
	@Override
	public void onResume() {
		Log.e(LOG, "############### resuming in " + LOG);
		
		super.onResume();

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i(LOG, "##### onSaveInstanceState  fired ...." + LOG);
		super.onSaveInstanceState(state);
	}
	static final String GOOGLE_URL = "https://www.google.com/calendar";

	View view;
	Context ctx;
	WebView webView;
	String url;
	String currentTitle, currentURL;
	long start, end;
	public BusyListener getListener() {
		return listener;
	}

	public void setListener(BusyListener listener) {
		this.listener = listener;
	}
	

}
