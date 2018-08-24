package com.schmoeker.management;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.schmoeker.EditFeedActivity;
import com.schmoeker.KEYS;
import com.schmoeker.R;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;

import java.util.List;

public class FeedManagementAdapter extends BaseAdapter {
    private Context context;
    private List<Feed> items;

    public FeedManagementAdapter(Context context, List<Feed> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.viewholder_feed_management, parent, false);
        }
        final Feed currentItem = (Feed) getItem(position);

        TextView textViewItemName = (TextView) convertView.findViewById(R.id.text_view_item_name);
        textViewItemName.setText(String.valueOf(currentItem.getId()));

        TextView textViewItemDescription = (TextView) convertView.findViewById(R.id.text_view_item_description);
        textViewItemDescription.setText(currentItem.getTitle());

        Button buttonEditFeed = (Button) convertView.findViewById(R.id.edit_feed);
        buttonEditFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditFeedActivity.class);
                intent.putExtra(KEYS.FEED_ID, currentItem.getId());
                context.startActivity(intent);
            }
        });

        Button buttonDeleteFeed = (Button) convertView.findViewById(R.id.delete_feed);
        buttonDeleteFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase.getInstance(context).getFeedDao().delete(currentItem);
            }
        });

        return convertView;
    }
}