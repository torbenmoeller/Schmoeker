package com.schmoeker;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.FeedState;

public class FeedItemsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;
    private final FeedState feedState;
    private final int feedId;

    public FeedItemsViewModelFactory(AppDatabase database, FeedState feedState, int feedId) {
        this.database = database;
        this.feedState = feedState;
        this.feedId = feedId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FeedItemsViewModel(database, feedState, feedId);
    }
}
