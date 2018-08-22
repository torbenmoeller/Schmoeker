package com.schmoeker.settings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.schmoeker.R;
import com.schmoeker.SchedulerUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.interval_spinner)
    Spinner intervalSpinner;
    @BindString(R.string.every_15_minutes)
    String every_15_minutes;
    @BindString(R.string.every_1_hour)
    String every_1_hour;
    @BindString(R.string.every_2_hour)
    String every_2_hour;
    @BindString(R.string.every_4_hour)
    String every_4_hour;
    @BindString(R.string.daily)
    String daily;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.interval_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner.setAdapter(adapter);
    }

    @OnItemSelected(R.id.interval_spinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        String chosen = spinner.getSelectedItem().toString();
        int interval = 0;
        if(chosen.equals(every_15_minutes)){
            interval = (int)TimeUnit.MINUTES.toSeconds(15);
        } else if(chosen.equals(every_1_hour)){
            interval = (int)TimeUnit.HOURS.toSeconds(1);
        } else if(chosen.equals(every_2_hour)){
            interval = (int)TimeUnit.HOURS.toSeconds(2);
        } else if(chosen.equals(every_4_hour)){
            interval = (int)TimeUnit.HOURS.toSeconds(4);
        } else {//daily
            interval = (int)TimeUnit.HOURS.toSeconds(24);
        }
        SchedulerUtil.reschedule(getApplicationContext(), interval);
    }

}
