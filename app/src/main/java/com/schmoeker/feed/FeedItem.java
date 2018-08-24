package com.schmoeker.feed;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@Entity(foreignKeys = @ForeignKey(
        entity = Feed.class,
        parentColumns = "id",
        childColumns = "feed_id"),
        indices = {@Index(
                value = {"link"},
                unique = true)}
        )
public class FeedItem {

    @PrimaryKey(autoGenerate = true)
    int id;
    //Foreign Key
    int feed_id;
    String uri;
    String date;
    String authors;
    String link;
    String title;
    String description;
    boolean read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(int feed_id) {
        this.feed_id = feed_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static String toJson(List<FeedItem> feedItemList) {
        try {
            Gson gson = new Gson();
            return gson.toJson(feedItemList);
        } catch (Exception e) {
            Log.e("Parsing object to json", "FeedItem_Parsing");
            return null;
        }
    }

    public static List<FeedItem> fromJson(String json) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<FeedItem>>() {}.getType();
            List<FeedItem> feedItemList = gson.fromJson(json, listType);
            return feedItemList;
        } catch (Exception e) {
            Log.e("Parsing json", "FeedItem_Parsing");
            return null;
        }
    }
}
