package com.schmoeker.sync;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.rometools.rome.io.FeedException;
import com.schmoeker.ArticleActivity;
import com.schmoeker.KEYS;
import com.schmoeker.R;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.Subscription;
import com.schmoeker.widget.UpdateWidgetService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SyncService extends IntentService {


    public SyncService() {
        super(SyncService.class.getSimpleName());
    }

    public SyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean autoupdate = intent.getBooleanExtra(KEYS.AUTOUPDATE, true);
        sync(autoupdate);
    }


    private void sync(final boolean autoUpdate) {
//        notifySynchronization();
        AsyncTask task = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    synchronizeFeeds(autoUpdate);
//                    createIntent();
                } catch (IOException | FeedException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
//                populateUI(AppDatabase.getInstance(getApplicationContext()).getFeedItemDao().getAll());
                return null;
            }
        };
        task.execute();
    }


    private void synchronizeFeeds(boolean autoUpdate) throws IOException, FeedException {
        List<Feed> feeds = AppDatabase.getInstance(this).getFeedDao().getAll();
        for (Feed feed : feeds) {
            Subscription subscription = new Subscription(new URL(feed.getLink()));
            List<FeedItem> feedItems = subscription.getFeedItems(feed.getId());
            long[] writtenIds = AppDatabase.getInstance(this).getFeedItemDao().insertAll(feedItems);
            for (int i = 0; i < writtenIds.length; i++) {
                if (writtenIds[i] > 0) {
                    FeedItem written = feedItems.get(i);
                    written.setId((int) writtenIds[i]);
                    if(autoUpdate) {
                        createNewFeedNotification(feed, written);
                    }
                }
            }
        }
        updateWidgetIntent();
    }

    private void updateWidgetIntent(){
        Intent startServiceIntent = new Intent(getApplicationContext(), UpdateWidgetService.class);
        getApplicationContext().startService(startServiceIntent);
    }


    private void createNewFeedNotification(Feed feed, FeedItem feedItem){
        String prefName = getApplicationContext().getPackageName() + "_preferences";
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(prefName, android.content.Context.MODE_PRIVATE);
        if(preferences.getBoolean("notifications_on", true)) {
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(KEYS.FEED_ITEM_ID, feedItem.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //Source: https://github.com/wix/react-native-notifications/issues/66#issuecomment-303972588
            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueInt, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, KEYS.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_rss_feed_black_24dp)
                    .setContentTitle(feed.getTitle())
                    .setContentText(feedItem.getTitle())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(feedItem.getId(), mBuilder.build());
        }
    }
}
