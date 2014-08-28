package com.boha.cmtrainee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmtrainee.fragments.ResourceListFragment;
import com.boha.cmtrainee.interfaces.ResourceListener;
import com.boha.cmtrainee.misc.BaseCMActivity;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;

public class ResourceActivity extends BaseCMActivity implements ResourceListener{

	ResourceListFragment resourceListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resource);
		ctx = getApplicationContext();
		resourceListFragment = (ResourceListFragment)
				getSupportFragmentManager()
				.findFragmentById(R.id.resource_fragment);
		getData();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(LOG, "*** onPause() - ...");	
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.course, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_back:
			finish();
			return true;
		case R.id.menu_info:			
			return true;
		
		case android.R.id.home:
			Intent intent = new Intent(this, MainPagerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void getData() {
		course = (CourseDTO) getIntent().getExtras().getSerializable("course");
		resourceListFragment.setData(course);
	}

	@Override
	public void setFields() {
		
	}

	@Override
	public void setList() {
		
	}

	
	@Override
	public void onResourcePicked(LessonResourceDTO resource) {
		Log.i(LOG, "resource selected. " + resource.getResourceName());
		Intent i = new Intent(ctx, SearchActivity.class);
		i.putExtra("resource", resource);
		i.putExtra("course", course);
		startActivityForResult(i, START_SEARCH);
		
	}
	static final int START_SEARCH = 33;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}
	CourseDTO course;

	@Override
	public void onShowProgressBar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveProgressBar() {
		// TODO Auto-generated method stub
		
	}
	static final String LOG = "ResourceActivity";
}
