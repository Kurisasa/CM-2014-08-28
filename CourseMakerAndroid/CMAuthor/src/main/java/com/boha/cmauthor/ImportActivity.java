package com.boha.cmauthor;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmauthor.fragments.ImportFragment;

public class ImportActivity extends FragmentActivity implements ImportFragment.ImportFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        importFragment = (ImportFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    ImportFragment importFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCoursesImported() {
        coursesImported = true;
        onBackPressed();
    }
    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu
                    .findItem(R.id.action_refresh);
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
    public void setBusy() {
        setRefreshActionButtonState(true);
    }

    @Override
    public void setNotBusy() {
        setRefreshActionButtonState(false);
    }
    boolean coursesImported = false;
    Menu mMenu;
    @Override
    public void onBackPressed() {
        if (coursesImported) {
           setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
        super.onBackPressed();

    }
}
