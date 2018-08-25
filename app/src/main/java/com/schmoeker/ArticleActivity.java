package com.schmoeker;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.FeedItem;
import com.schmoeker.widget.UpdateWidgetService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.article_head)
    TextView articleHead;
    @BindView(R.id.article_description)
    WebView articleDescription;
    @BindView(R.id.article_open_link)
    Button articleOpenLink;

    private AppDatabase appDatabase;
    private FeedItem feedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        initViews();

        Intent startServiceIntent = new Intent(getApplicationContext(), UpdateWidgetService.class);
        getApplicationContext().startService(startServiceIntent);

        appDatabase = AppDatabase.getInstance(getApplicationContext());
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEYS.FEED_ITEM_ID)) {
            final int feedItemId = intent.getIntExtra(KEYS.FEED_ITEM_ID, 0);
            AsyncTask task =new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    feedItem = appDatabase.getFeedItemDao().loadById(feedItemId);
                    setContent(feedItem);
                    markArticleAsRead();
                    return null;
                }
            };
            task.execute();
        }
    }

    //Source: https://stackoverflow.com/questions/34756092/webview-in-asynctask-doinbackground-method
    private void setContent(final FeedItem feedItem){
        runOnUiThread(new Runnable() {
            public void run() {
                articleHead.setText(feedItem.getTitle());
                articleDescription.loadData("<html><body>"+feedItem.getDescription()+"</body></html>", "text/html", "UTF-8");
            }
        });
    }

    private void markArticleAsRead() {
        AppDatabase.getInstance(getApplicationContext()).getFeedItemDao().markAsRead(feedItem.getId());
    }

    @OnClick(R.id.article_open_link)
    public void onClickOpenLink(){
        openLink(feedItem.getLink());
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    //Source https://stackoverflow.com/questions/4930228/open-a-url-on-click-of-ok-button-in-android
    private void openLink(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
