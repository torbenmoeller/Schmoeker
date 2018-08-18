package com.schmoeker.feed;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    SyndFeed syndFeed;

    public Subscription(URL url) throws IOException, FeedException {
        syndFeed = new SyndFeedInput().build(new XmlReader(url));
    }

    public Subscription(java.io.File file) throws IOException, FeedException {
        InputStream targetStream = new FileInputStream(file);
        syndFeed = new SyndFeedInput().build(new XmlReader(targetStream));
    }


    public Feed getFeed(){
        Feed feed = new Feed();
        feed.setTitle(syndFeed.getTitle());
        feed.setLink(syndFeed.getLink());
        return feed;
    }

    public List<FeedItem> getFeedItems(int feedId){
        List<FeedItem> feedItemList = new ArrayList<>();
        for (SyndEntry entry: syndFeed.getEntries()) {
            FeedItem feedItem = new FeedItem();
            feedItem.setFeed_id(feedId);
            feedItem.setUri(entry.getUri() != null ? entry.getUri() : "");
            feedItem.setDate(entry.getPublishedDate() != null ? entry.getPublishedDate().toString() : "");
            feedItem.setAuthors(entry.getAuthor() != null ? entry.getAuthor() : "");
            feedItem.setLink(entry.getLink() != null ? entry.getLink() : "");
            feedItem.setTitle(entry.getTitle() != null ? entry.getTitle() : "");
            feedItem.setDescription(entry.getDescription() != null ? entry.getDescription().getValue() : "");
            feedItemList.add(feedItem);
        }
        return feedItemList;
    }

}
