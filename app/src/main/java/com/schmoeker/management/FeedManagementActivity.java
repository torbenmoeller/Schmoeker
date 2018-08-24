package com.schmoeker.management;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.schmoeker.EditFeedActivity;
import com.schmoeker.R;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedManagementActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.feed_list_view)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_management);
        ButterKnife.bind(this);
        initViews();

        FeedManagementViewModelFactory factory = new FeedManagementViewModelFactory(AppDatabase.getInstance(this));
        final FeedManagementViewModel viewModel = ViewModelProviders.of(this, factory).get(FeedManagementViewModel.class);
        viewModel.getTask().observe(this, new Observer<List<Feed>>() {
            @Override
            public void onChanged(@Nullable List<Feed> feeds) {
                populateUI(feeds);
            }
        });
    }



    public void populateUI(@Nullable List<Feed> feeds) {

        FeedManagementAdapter adapter = new FeedManagementAdapter(this, feeds);
        listView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_feed) {
            Intent intent = new Intent(FeedManagementActivity.this, EditFeedActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

}
