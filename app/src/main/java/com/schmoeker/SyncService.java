package com.schmoeker;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.rometools.rome.io.FeedException;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.Subscription;
import com.schmoeker.settings.SettingsActivity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SyncService extends IntentService {


    public SyncService() {
        super(SyncService.class.getSimpleName());
    }
    public SyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        JobScheduler jobScheduler = (JobScheduler)
//            getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        ComponentName componentName =
//                new ComponentName(getApplicationContext(), YourJobService.class);
//        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, componentName);
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
////        builder.setPeriodic(15* 60 * 1000); //15min
//        builder.setPeriodic(30 * 1000);//30sec
//        jobScheduler.schedule(builder.build());
        sync();
    }



    private void sync(){
//        notifySynchronization();
        AsyncTask task = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    synchronizeFeeds();
//                    createIntent();
                } catch (IOException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                } catch (FeedException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
//                populateUI(AppDatabase.getInstance(getApplicationContext()).getFeedItemDao().getAll());
                return null;
            }
        };
        task.execute();
    }


    private void synchronizeFeeds() throws IOException, FeedException {
        List<Feed> feeds = AppDatabase.getInstance(this).getFeedDao().getAll();
        for(Feed feed : feeds){
            Subscription subscription = new Subscription(new URL(feed.getLink()));
            List<FeedItem> feedItems = subscription.getFeedItems(feed.getId());
            long[] writtenIds = AppDatabase.getInstance(this).getFeedItemDao().insertAll(feedItems);
            for(int i = 0; i < writtenIds.length; i++){
                if(writtenIds[i] > 0){
                    FeedItem written = feedItems.get(i);
                    written.setId((int)writtenIds[i]);
                    createNewFeedNotification(feed, written);
                }
            }
        }
    }


    private void createNewFeedNotification(Feed feed, FeedItem feedItem){
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(KEYS.FEED_ITEM_ID, feedItem.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, KEYS.CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark) //ToDo
                .setContentTitle(feed.getTitle())
                .setContentText(feedItem.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(feedItem.getId(), mBuilder.build());
    }

    private void createIntent(){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, KEYS.CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify((new Random().nextInt()), mBuilder.build());
    }


    private void notifySynchronization(){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, KEYS.CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Sync started")
                .setContentText("Sync started")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((new Random().nextInt()), mBuilder.build());
    }



//    // Setup a recurring alarm every half hour
//    public void scheduleAlarm() {
//        // Construct an intent that will execute the AlarmReceiver
//        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
//        // Create a PendingIntent to be triggered when the alarm goes off
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Setup periodic alarm every every half hour from this point onwards
//        long firstMillis = System.currentTimeMillis(); // alarm is set right away
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
//        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
//                AlarmManager.INTERVAL_HALF_HOUR, pIntent);
//    }
//
//    public void cancelAlarm() {
//        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
//        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pIntent);
//    }
}
