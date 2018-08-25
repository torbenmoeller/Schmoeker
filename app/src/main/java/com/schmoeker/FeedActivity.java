package com.schmoeker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.schmoeker.analytics.AnalyticsUtil;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.FeedState;
import com.schmoeker.management.FeedManagementActivity;
import com.schmoeker.settings.SettingsActivity;
import com.schmoeker.sync.SchedulerUtil;
import com.schmoeker.sync.SyncService;

import java.util.List;

import butterknife.BindString;
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
    @BindString(R.string.admobkey)
    String admobkey;
    @BindString(R.string.all_feeds)
    String allFeeds;
    @BindString(R.string.all_unread)
    String allUnread;

    List<Feed> feeds;
    FeedState feedState = FeedState.ALL;
    int feedId = 0;
    FeedItemsAdapter adapter = null;
    LiveData<List<FeedItem>> feedItemLiveData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        initViews();
        initAds();

        Intent intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra(KEYS.FEED_STATE)) {
                feedState = (FeedState) intent.getSerializableExtra(KEYS.FEED_STATE);
                switch(feedState){
                    case ALL:
                        getSupportActionBar().setTitle(allFeeds);
                        break;
                    case UNREAD:
                        getSupportActionBar().setTitle(allUnread);
                        break;
                    case FEED:
                        //do nothing set title later
                        break;
                }
            }
            if (intent.hasExtra(KEYS.FEED_ID)) {
                feedId = intent.getIntExtra(KEYS.FEED_ID, 0);
            }
        }
        AnalyticsUtil.logViewFeed(getApplicationContext(), feedState, feedId);
        createNotificationChannel();
        SchedulerUtil.scheduleChargingReminder(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        updateNavigation();


        FeedItemsViewModelFactory factory = new FeedItemsViewModelFactory(AppDatabase.getInstance(this),feedState, feedId);
        final FeedItemsViewModel viewModel = ViewModelProviders.of(this, factory).get(FeedItemsViewModel.class);
        feedItemLiveData = viewModel.getData();
        viewModel.getData().observe(this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(@Nullable List<FeedItem> feeds) {
                populateUI(feeds);
            }
        });
    }

    private void initAds() {
        MobileAds.initialize(this, admobkey);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(KEYS.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void updateNavigation(){
        final AsyncTask navigationUpdateTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                feeds = AppDatabase.getInstance(getApplicationContext()).getFeedDao().getAll();
                runOnUiThread(new Runnable() {
                    public void run() {
                        for (int i = 0; i < feeds.size(); i++){
                            Feed feed = feeds.get(i);
                            if(feed.getId() == feedId){
                                getSupportActionBar().setTitle(feed.getTitle());
                            }
                            navigationView.getMenu().add(R.id.menu_feeds, feed.getId(), 215 + i, feed.getTitle());
                        }
                    }
                });
                return null;
            }
        };
        navigationUpdateTask.execute();
    }

    public void populateUI(@Nullable List<FeedItem> feeds) {
        adapter = new FeedItemsAdapter(this, feeds);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            Intent startServiceIntent = new Intent(getApplicationContext(), SyncService.class);
            startServiceIntent.putExtra(KEYS.AUTOUPDATE, false);
            getApplicationContext().startService(startServiceIntent);
            return true;
        }else if (id == R.id.action_all_read) {

            final AsyncTask task = new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] objects) {
                    for(FeedItem feedItem : feedItemLiveData.getValue()){
                        AppDatabase.getInstance(getApplicationContext()).getFeedItemDao().markAsRead(feedItem.getId());
                    }
                    return null;
                }
            };
            task.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
