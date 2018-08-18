package com.schmoeker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.db.FeedDao;
import com.schmoeker.feed.Feed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {
    private FeedDao mFeedDao;
    private AppDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        mFeedDao = mDb.getFeedDao();
    }

    @After
    public void closeDb() throws IOException {
        mDb.close();
    }

    @Test
    public void writeUserAndReadInList() throws Exception {
        Feed feed = new Feed();
        feed.setTitle("test");


        mFeedDao.insertAll(feed);
        Feed result = mFeedDao.findByTitle("test");
        assertEquals(feed.getTitle(), result.getTitle());
    }
}
