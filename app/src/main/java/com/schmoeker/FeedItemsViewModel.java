package com.schmoeker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;

import java.util.List;

public class FeedItemsViewModel extends ViewModel {

    private LiveData<List<FeedItem>> feedItemLiveData;

    public FeedItemsViewModel(AppDatabase database, int feedId) {
        feedItemLiveData = database.getFeedItemDao().loadLiveData();
        int i = 0;
        i++;
    }

    public LiveData<List<FeedItem>> getTask() {
        return feedItemLiveData;
    }
}
