package com.schmoeker.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;

@Database(entities = {Feed.class, FeedItem.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "db_feeds";

    private static AppDatabase instance;

    public abstract FeedDao getFeedDao();
    public abstract FeedItemDao getFeedItemDao();

    public static AppDatabase getInstance(Context context){
        if(instance ==null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating new database instance");
                instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return instance;
    }
}

