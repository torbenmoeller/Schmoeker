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
    List<FeedItem> loadAllByIds(int feedItemId);

    @Query("SELECT * FROM feeditem WHERE id = :feedId")
    List<FeedItem> loadAllByFeed(int feedId);

    @Query("SELECT * FROM feeditem WHERE feed_id = :feedId")
    LiveData<List<FeedItem>> loadLiveDataByFeed(int feedId);
    @Query("SELECT * FROM feeditem")
    LiveData<List<FeedItem>> loadLiveData();

//    @Query("SELECT * FROM feed WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
//    Feed findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FeedItem> feedItems);

    @Delete
    void delete(FeedItem feedItem);
}


