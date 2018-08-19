package com.schmoeker;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.schmoeker.db.AppDatabase;

public class FeedManagementViewModelFactoryCopy extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;
    private final int feedId;

    public FeedManagementViewModelFactoryCopy(AppDatabase database, int feedId) {
        this.database = database;
        this.feedId = feedId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FeedManagementViewModelCopy(database, feedId);
    }
}
