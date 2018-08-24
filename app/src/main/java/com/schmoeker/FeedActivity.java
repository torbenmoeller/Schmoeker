package com.schmoeker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.feed.FeedState;

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

    List<Feed> feeds;
    FeedState feedState = FeedState.ALL;
    int feedId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        initViews();

        MobileAds.initialize(this, admobkey);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra(KEYS.FEED_STATE)) {
                feedState = (FeedState) intent.getSerializableExtra(KEYS.FEED_STATE);
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
        viewModel.getTask().observe(this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(@Nullable List<FeedItem> feeds) {
//                viewModel.getTask().removeObserver(this);
                populateUI(feeds);
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(KEYS.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
            Intent startServiceIntent = new Intent(getApplicationContext(), SyncService.class);
            getApplicationContext().startService(startServiceIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
//            intent.putExtra( SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName() );
//            intent.putExtra( SettingsActivity.EXTRA_NO_HEADERS, true );
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
