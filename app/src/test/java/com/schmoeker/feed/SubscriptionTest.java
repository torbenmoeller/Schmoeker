package com.schmoeker.feed;

import com.rometools.rome.io.FeedException;
import com.schmoeker.feed.Subscription;

import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SubscriptionTest {
    @Test
    public void createAndReadFeed() throws IOException, FeedException {
        File resourcesDirectory = new File("src/test/data");
        if(!resourcesDirectory.exists()){
            resourcesDirectory.mkdirs();
        }
        File testFeed = new File(resourcesDirectory.getPath() + "/test.feed");
        if(!testFeed.exists()){
            testFeed.createNewFile();
        }
        String filename = testFeed.getAbsolutePath();
        FeedTestCreator.create(filename);
        File file = new File (filename);
        Subscription subscription = new Subscription(file);
        Feed feed = subscription.getFeed();
        assertEquals("Sample Feed (created with ROME)", feed.getTitle());
    }

    @Test
    public void readRealZeitFeed() throws IOException, FeedException {
        URL url = new URL("http://newsfeed.zeit.de/index");
        Subscription subscription = new Subscription(url);
        Feed feed = subscription.getFeed();
        assertEquals("ZEIT ONLINE | Nachrichten, Hintergründe und Debatten", feed.getTitle());
    }

    @Test
    public void readRealCBFeed() throws IOException, FeedException {
        URL url = new URL("https://www.computerbase.de/rss/artikel.xml");
        Subscription subscription = new Subscription(url);
        Feed feed = subscription.getFeed();
        assertEquals("ZEIT ONLINE | Nachrichten, Hintergründe und Debatten", feed.getTitle());
    }
}