package com.schmoeker.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.schmoeker.KEYS;
import com.schmoeker.R;
import com.schmoeker.feed.FeedItem;

import java.util.List;


public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<FeedItem> feedItems;

    public ListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        feedItems = Prefs.loadRecipe(mContext);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.schmoeker_app_widget_item);

        final Intent fillInIntent = new Intent();
        int id = feedItems.get(position).getId();
        fillInIntent.putExtra(KEYS.FEED_ITEM_ID, id);
        row.setOnClickFillInIntent(R.id.item, fillInIntent);

        row.setTextViewText(R.id.item, feedItems.get(position).getTitle());
        row.setContentDescription(R.id.item, feedItems.get(position).getTitle());
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
