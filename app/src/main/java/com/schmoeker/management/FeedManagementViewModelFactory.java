package com.schmoeker.management;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.schmoeker.db.AppDatabase;

public class FeedManagementViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;

    public FeedManagementViewModelFactory(AppDatabase database) {
        this.database = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FeedManagementViewModel(database);
    }
}
