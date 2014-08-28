package com.boha.cmlibrary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.util.actor.CourseFormatter;

public class CourseOverviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_overview);
		getData();
		setFields();

	}


	@Override
	public void onBackPressed() {
		Log.i("SA", "-- onBackPressed ---");
		if (webView.isFocused() && webView.canGoBack()) {
			webView.goBack();
		} else {
			finish();
		}
	}

	private void setFields() {
		webView = (WebView) findViewById(R.id.WEB_webView);

		webView.setWebViewClient(new MyClient());
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
		
		webView.loadData(CourseFormatter.format(course), "text/html", null);
	}
	
	
	private void getData() {
		course = (CourseDTO) getIntent().getExtras().getSerializable("course");

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.course_overview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.back) {
			onBackPressed();
		}
		
		return super.onOptionsItemSelected(item);
	}
	private class MyClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView web, String url) {
			Log.i("SA", "--onPageFinished --- title: " + web.getTitle()
					+ " - url: " + web.getUrl());
			currentTitle = web.getTitle();
			currentURL = web.getUrl();
			end = System.currentTimeMillis();
			Log.e("SA", "--onPageFinished -- elapsed loading time: " + (end-start)/1000 + " seconds");
			super.onPageFinished(web, url);
		}

		@Override
		public void onReceivedError(WebView web, int errorCode,
				String description, String failingURL) {
			Log.i("SA", "--onReceivedError code: " + errorCode + " "
					+ description + " - failing: " + failingURL);
		}
		@Override
		public void onPageStarted(WebView web, String url, Bitmap favIcon) {
			Log.e("SA", "--onPageStarted url: " + url);
			start = System.currentTimeMillis();
		}
	}

	PopupWindow pop;
	long start, end;
	AuthorDTO author;
	
	CourseDTO course;
	String currentTitle, currentURL;
	WebView webView;
	
}
