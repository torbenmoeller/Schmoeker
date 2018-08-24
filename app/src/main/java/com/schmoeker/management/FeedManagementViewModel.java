package com.schmoeker.management;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;

import java.util.List;

public class FeedManagementViewModel extends ViewModel {

    private LiveData<List<Feed>> feedLiveData;

    public FeedManagementViewModel(AppDatabase database) {
        feedLiveData = database.getFeedDao().getLiveDataAll();
    }

    public LiveData<List<Feed>> getTask() {
        return feedLiveData;
    }
}
