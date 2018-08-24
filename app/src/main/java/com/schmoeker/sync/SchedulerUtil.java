package com.schmoeker.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class SchedulerUtil {
    private static final String REMINDER_JOB_TAG = "sync_subscribed_feeds";

    private static boolean sInitialized;

    synchronized public static void scheduleChargingReminder(@NonNull final Context context) {
        if (sInitialized) {
            return;
        }
        String prefName = context.getPackageName() + "_preferences";
        SharedPreferences preferences = context.getSharedPreferences(prefName, android.content.Context.MODE_PRIVATE);
        String pref = preferences.getString("sync_interval", "15");
        reschedule(context, Integer.valueOf(pref));
    }

    synchronized public static void reschedule(@NonNull final Context context, int time){
        int interval = (int)TimeUnit.MINUTES.toSeconds(time);
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        interval,
                        2 * interval))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(constraintReminderJob);
        sInitialized = true;

    }

}
