package com.schmoeker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.FeedState;

import java.util.List;

public class FeedItemsViewModel extends ViewModel {

    private LiveData<List<FeedItem>> feedItemLiveData;

    public FeedItemsViewModel(AppDatabase database, FeedState feedState, int feedId) {
        switch (feedState){
            case ALL:
                feedItemLiveData = database.getFeedItemDao().loadLiveData();
                break;
            case UNREAD:
                feedItemLiveData = database.getFeedItemDao().loadLiveData(false);
                break;
            case FEED:
                feedItemLiveData = database.getFeedItemDao().loadLiveData(feedId);
                break;
        }
    }

    public LiveData<List<FeedItem>> getData() {
        return feedItemLiveData;
    }
}
