package com.schmoeker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;

public class FeedManagementViewModelCopy extends ViewModel {

    private LiveData<Feed> feedLiveData;

    public FeedManagementViewModelCopy(AppDatabase database, int feedId) {
        feedLiveData = database.getFeedDao().loadLiveDataById(feedId);
    }

    public LiveData<Feed> getTask() {
        return feedLiveData;
    }
}
