package com.schmoeker.management;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.schmoeker.db.AppDatabase;

public class FeedManagementViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;

    public FeedManagementViewModelFactory(AppDatabase database) {
        this.database = database;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FeedManagementViewModel(database);
    }
}
