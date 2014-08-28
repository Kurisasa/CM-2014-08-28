package com.boha.cmlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.boha.cmlibrary.fragments.TraineeSkillsFragment;
import com.boha.coursemaker.dto.TraineeDTO;


public class TraineeSkillsActivity extends FragmentActivity implements TraineeSkillsFragment.TraineeSkillsFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_skill_list);
        traineeSkillsFragment = (TraineeSkillsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
        trainee = (TraineeDTO)getIntent().getSerializableExtra("trainee");
        traineeSkillsFragment.setTrainee(trainee);
        setTitle(trainee.getFullName());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trainee_skills, menu);
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

    TraineeSkillsFragment traineeSkillsFragment;
    TraineeDTO trainee;

    @Override
    public void onAddSkillsRequest(TraineeDTO trainee) {
        Intent i = new Intent(this,CompanySkillsActivity.class);
        i.putExtra("trainee",trainee);
        startActivityForResult(i, COMPANY_SKILLS_REQ);
    }
    static final int COMPANY_SKILLS_REQ = 1321;
    @Override
    public void onActivityResult(int req, int res, Intent data) {
        Log.w(LOG,"onActivityResult req " + req + " result: " + res);
        switch (req) {
            case COMPANY_SKILLS_REQ:
                trainee = (TraineeDTO)data.getSerializableExtra("trainee");
                traineeSkillsFragment.setTrainee(trainee);
                break;
        }
    }
    static final String LOG = TraineeSkillsActivity.class.getName();
}
