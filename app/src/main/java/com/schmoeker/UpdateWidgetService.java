package com.schmoeker;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.rometools.rome.io.FeedException;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.Subscription;
import com.schmoeker.widget.AppWidgetService;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class UpdateWidgetService extends IntentService {


    public UpdateWidgetService() {
        super(UpdateWidgetService.class.getSimpleName());
    }
    public UpdateWidgetService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<FeedItem> feedItemList = AppDatabase.getInstance(getApplicationContext()).getFeedItemDao().getAll();
        AppWidgetService.updateWidget(this, feedItemList);
    }



}
