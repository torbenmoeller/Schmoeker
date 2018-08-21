package com.schmoeker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.rometools.rome.io.FeedException;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.FeedState;
import com.schmoeker.feed.Subscription;
import com.schmoeker.settings.SettingsActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.feed_items_list_view)
    ListView listView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    List<Feed> feeds;
    FeedState feedState = FeedState.ALL;
    int feedId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        initViews();

        Intent intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra(KEYS.FEED_STATE)) {
                feedState = (FeedState) intent.getSerializableExtra(KEYS.FEED_STATE);
            }
            if (intent.hasExtra(KEYS.FEED_ID)) {
                feedId = intent.getIntExtra(KEYS.FEED_ID, 0);
            }
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        updateNavigation();

        FeedItemsViewModelFactory factory = new FeedItemsViewModelFactory(AppDatabase.getInstance(this),feedState, feedId);
        final FeedItemsViewModel viewModel = ViewModelProviders.of(this, factory).get(FeedItemsViewModel.class);
        viewModel.getTask().observe(this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(@Nullable List<FeedItem> feeds) {
//                viewModel.getTask().removeObserver(this);
                populateUI(feeds);
            }
        });
    }

    private void updateNavigation(){
        final AsyncTask navigationUpdateTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                feeds = AppDatabase.getInstance(getApplicationContext()).getFeedDao().getAll();
                for (int i = 0; i < feeds.size(); i++){
                    Feed feed = feeds.get(i);
                    navigationView.getMenu().add(R.id.menu_feeds, feed.getId(), 215 + i, feed.getTitle());
                }
                return null;
            }
        };
        navigationUpdateTask.execute();
    }

    public void populateUI(@Nullable List<FeedItem> feeds) {
        FeedItemsAdapter adapter = new FeedItemsAdapter(this, feeds);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            try {
                List<FeedItem> asdf = AppDatabase.getInstance(this).getFeedItemDao().getAll();
                sync();
            } catch (IOException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            } catch (FeedException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sync() throws IOException, FeedException {
        AsyncTask task = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    synchronizeFeeds();
                } catch (IOException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                } catch (FeedException e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
//                populateUI(AppDatabase.getInstance(getApplicationContext()).getFeedItemDao().getAll());
                return null;
            }
        };
        task.execute();
    }
    private void synchronizeFeeds() throws IOException, FeedException {
        List<Feed> feeds = AppDatabase.getInstance(this).getFeedDao().getAll();
        for(Feed feed : feeds){
            Subscription subscription = new Subscription(new URL(feed.getLink()));
            List<FeedItem> feedItems = subscription.getFeedItems(feed.getId());
            AppDatabase.getInstance(this).getFeedItemDao().insertAll(feedItems);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String itemTitle = item.getTitle().toString();

        if (id == R.id.nav_all_feeds) {
            Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
            intent.putExtra(KEYS.FEED_STATE, FeedState.ALL);
            startActivity(intent);
        } else if (id == R.id.nav_all_unread) {
            Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
            intent.putExtra(KEYS.FEED_STATE, FeedState.UNREAD);
            startActivity(intent);
        } else if (id == R.id.nav_manage_feeds) {
            Intent intent = new Intent(FeedActivity.this, FeedManagementActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(FeedActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        for (Feed feed: feeds){
            if(itemTitle.equals(feed.getTitle())){
                Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
                intent.putExtra(KEYS.FEED_STATE, FeedState.FEED);
                intent.putExtra(KEYS.FEED_ID, feed.getId());
                startActivity(intent);
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);
    }
}
