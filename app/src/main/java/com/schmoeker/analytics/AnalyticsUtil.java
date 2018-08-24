package com.schmoeker.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedState;

public class AnalyticsUtil {
    public static void logNewFeed(Context context, Feed feed) {
        Bundle params = new Bundle();
        params.putInt(FirebaseAnalytics.Param.ITEM_ID, feed.getId());
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, feed.getTitle());
        FirebaseAnalytics.getInstance(context).logEvent("new_feed_added", params);
    }

    public static void logViewFeed(Context context, FeedState feedState, int feedId) {
        Bundle params = new Bundle();
        params.putInt(FirebaseAnalytics.Param.ITEM_ID, feedId);
        params.putString(FirebaseAnalytics.Param.VALUE, feedState.toString());
        FirebaseAnalytics.getInstance(context).logEvent(
                FirebaseAnalytics.Event.VIEW_ITEM, params);
    }

    public static void logDisableNotifications(Context context) {
        FirebaseAnalytics.getInstance(context).logEvent(
                "notification_disabled", null);
    }
}
