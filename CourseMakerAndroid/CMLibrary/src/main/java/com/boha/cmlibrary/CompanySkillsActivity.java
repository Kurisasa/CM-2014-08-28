package com.boha.cmlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmlibrary.fragments.CompanySkillsFragment;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TraineeSkillDTO;

import java.util.List;


public class CompanySkillsActivity extends FragmentActivity
        implements CompanySkillsFragment.CompanySkillsFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_skills);
        companySkillsFragment = (CompanySkillsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        trainee = (TraineeDTO) getIntent().getSerializableExtra("trainee");
        companySkillsFragment.setTrainee(trainee);
        setTitle(trainee.getFullName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.company_skills, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    CompanySkillsFragment companySkillsFragment;
    TraineeDTO trainee;

    @Override
    public void onSkillsAdded(List<TraineeSkillDTO> skills) {
        Log.e(LOG,"##### onSkillsAdded skills: " + skills.size());
        trainee.setTraineeSkillList(skills);
        onBackPressed();

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("trainee",trainee);
        setResult(RESULT_OK, i);
        finish();
    }
    static final String LOG = CompanySkillsActivity.class.getName();
}
