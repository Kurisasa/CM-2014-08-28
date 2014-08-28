package com.boha.cmtrainee;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.dto.ResponseDTO;

@SuppressLint("SetJavaScriptEnabled")
public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		getData();
		setFields();
		//
		

	}
	public void setRefreshActionButtonState(final boolean refreshing) {
	    if (mMenu != null) {
	        final MenuItem refreshItem = mMenu
	            .findItem(R.id.menu_back);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.action_bar_progess);
	            } else {
	                refreshItem.setActionView(null);
	            }
	        }
	    }
	}
	
	static final String GOOGLE_URL = "https://www.google.com/search?";

	@Override
	public void onBackPressed() {
		Log.i("SA", "-- onBackPressed ---");
		if (webView.isFocused() && webView.canGoBack()) {
			webView.goBack();
		} else {
			//set result
			Intent data = new Intent();
			if (aResponse == null) {
				setResult(Activity.RESULT_CANCELED);
			} else {
				data.putExtra("response", aResponse);
				setResult(Activity.RESULT_OK, data);
			}
			finish();
		}
	}
	@Override
	public void onPause() {	
		Log.i("SA", "-- onPause ---");	
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}
	private void setFields() {
		webView = (WebView) findViewById(R.id.WEB_webView);

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
		
		webView.loadUrl(url);
	}
	
	private void getData() {
		course = (CourseDTO) getIntent().getExtras().getSerializable("course");
		resource = (LessonResourceDTO) getIntent().getExtras().getSerializable("resource");
		url = resource.getUrl();
		if (url == null) {
			url = GOOGLE_URL;
		}

	}



	Menu mMenu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.search, menu);
		mMenu = menu;
		

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			  Intent intent = new Intent(this, MainPagerActivity.class);
			  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  startActivity(intent);
			  return true;
		case R.id.menu_search:
			webView.loadUrl(GOOGLE_URL);
			return true;
		case R.id.menu_back:
			Animation an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left);
			an.setDuration(1000);
			an.setAnimationListener(new Animation.AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					finish();
					
				}
			});
			
			webView.startAnimation(an);
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView web, String url) {
			Log.i("SA", "--onPageFinished --- title: " + web.getTitle()
					+ " - url: " + web.getUrl());
			currentTitle = web.getTitle();
			currentURL = web.getUrl();
			end = System.currentTimeMillis();
			setRefreshActionButtonState(false);
			Toast.makeText(getApplicationContext(), "Page: " + currentTitle +
					" - loaded in " + (end-start)/1000 + " seconds", Toast.LENGTH_SHORT).show();
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
			setRefreshActionButtonState(true);
		}
	}
	
	
	PopupWindow pop;
	String url;
	long start, end;
	AuthorDTO author;
	LessonResourceDTO resource;
	ResponseDTO aResponse;
	CourseDTO course;
	String currentTitle, currentURL;
	WebView webView;
	TextView txtCourse, txtLesson;
	Button btnAddRes;
}
