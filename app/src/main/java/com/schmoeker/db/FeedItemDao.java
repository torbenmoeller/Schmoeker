package com.schmoeker.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.schmoeker.feed.FeedItem;

import java.util.List;

@Dao
public interface FeedItemDao {
    @Query("SELECT * FROM feeditem Order BY timestamp DESC")
    List<FeedItem> getAll();

    @Query("SELECT * FROM feeditem WHERE id = :feedItemId LIMIT 1")
    FeedItem loadById(int feedItemId);

    @Query("SELECT * FROM feeditem Order BY timestamp DESC")
    LiveData<List<FeedItem>> loadLiveData();

    @Query("SELECT * FROM feeditem WHERE read = :read Order BY timestamp DESC")
    LiveData<List<FeedItem>> loadLiveData(boolean read);

    @Query("SELECT * FROM feeditem WHERE feed_id = :feedId Order BY timestamp DESC")
    LiveData<List<FeedItem>> loadLiveData(int feedId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(List<FeedItem> feedItems);

    @Query("UPDATE feeditem SET read = 'true' WHERE id =:feedItemId")
    void markAsRead(int feedItemId);

    @Delete
    void delete(FeedItem feedItem);
}


