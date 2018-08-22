package com.schmoeker;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        Intent service = new Intent(getApplicationContext(), SyncService.class);
        getApplicationContext().startService(service);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
//        Intent service = new Intent(getApplicationContext(), SyncService.class);
//        getApplicationContext().startService(service);
        return false;
    }
}