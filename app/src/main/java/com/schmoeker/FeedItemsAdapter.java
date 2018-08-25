package com.schmoeker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.schmoeker.feed.FeedItem;

import java.util.List;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class FeedItemsAdapter extends BaseAdapter {
    private Context context;
    private List<FeedItem> items;


    public FeedItemsAdapter(Context context, List<FeedItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.viewholder_feed_item, parent, false);
        }
        final FeedItem currentItem = (FeedItem) getItem(position);

        TextView textViewItemName = (TextView) convertView.findViewById(R.id.text_feed_item_title);
        textViewItemName.setContentDescription(String.valueOf(currentItem.getTitle()));
        textViewItemName.setText(String.valueOf(currentItem.getTitle()));

        textViewItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArticleActivity.class);
                intent.putExtra(KEYS.FEED_ITEM_ID, currentItem.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}