package com.schmoeker.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.schmoeker.feed.Feed;

import java.util.List;

@Dao
public interface FeedDao {
    @Query("SELECT * FROM feed")
    List<Feed> getAll();

    @Query("SELECT * FROM feed WHERE id = :feedId LIMIT 1")
    List<Feed> loadAllByIds(int feedId);

    @Query("SELECT * FROM feed WHERE title LIKE :title LIMIT 1")
    Feed findByTitle(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Feed... users);

    @Delete
    void delete(Feed user);
}


