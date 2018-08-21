package com.schmoeker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;

import java.util.List;

@Dao
public interface FeedItemDao {
    @Query("SELECT * FROM feeditem")
    List<FeedItem> getAll();

    @Query("SELECT * FROM feeditem WHERE id = :feedItemId LIMIT 1")
    FeedItem loadById(int feedItemId);

    @Query("SELECT * FROM feeditem")
    LiveData<List<FeedItem>> loadLiveData();

    @Query("SELECT * FROM feeditem WHERE read = :read")
    LiveData<List<FeedItem>> loadLiveData(boolean read);

    @Query("SELECT * FROM feeditem WHERE feed_id = :feedId")
    LiveData<List<FeedItem>> loadLiveData(int feedId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FeedItem> feedItems);

    @Query("UPDATE feeditem SET read = 'true' WHERE id =:feedItemId")
    void markAsRead(int feedItemId);

    @Delete
    void delete(FeedItem feedItem);
}


