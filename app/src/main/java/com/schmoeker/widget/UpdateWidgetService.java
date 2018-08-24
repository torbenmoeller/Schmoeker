package com.schmoeker.widget;

import android.app.IntentService;
import android.content.Intent;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.FeedItem;

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
