package com.schmoeker.sync;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.schmoeker.KEYS;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        Intent service = new Intent(getApplicationContext(), SyncService.class);
        service.putExtra(KEYS.AUTOUPDATE, true);
        getApplicationContext().startService(service);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}