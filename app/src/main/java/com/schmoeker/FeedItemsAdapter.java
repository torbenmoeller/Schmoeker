/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.schmoeker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;

import java.util.List;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class FeedItemsAdapter extends BaseAdapter {
    private Context context; //context
    private List<FeedItem> items; //data source of the list adapter

    //public constructor
    public FeedItemsAdapter(Context context, List<FeedItem> items) {
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
                    inflate(R.layout.viewholder_feed_item, parent, false);
        }
        final FeedItem currentItem = (FeedItem) getItem(position);

        TextView textViewItemName = (TextView) convertView.findViewById(R.id.text_feed_item_title);
        textViewItemName.setText(String.valueOf(currentItem.getTitle()));

//        TextView textViewItemDescription = (TextView) convertView.findViewById(R.id.text_view_item_description);
//        textViewItemDescription.setText(currentItem.getTitle());
//
//        Button buttonEditFeed = (Button) convertView.findViewById(R.id.edit_feed);
//        buttonEditFeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, EditFeedActivity.class);
//                intent.putExtra(KEYS.FEED_ID, currentItem.getId());
//                context.startActivity(intent);
//            }
//        });
//
//        Button buttonDeleteFeed = (Button) convertView.findViewById(R.id.delete_feed);
//        buttonDeleteFeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppDatabase.getInstance(context).getFeedDao().delete(currentItem);
//            }
//        });

        return convertView;
    }
}