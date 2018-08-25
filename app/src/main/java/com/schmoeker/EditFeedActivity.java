package com.schmoeker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import com.schmoeker.analytics.AnalyticsUtil;
import com.schmoeker.db.AppDatabase;
import com.schmoeker.feed.Feed;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditFeedActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_url)
    EditText editUrl;
    @BindView(R.id.edit_title)
    EditText editTitle;
    @BindString(R.string.save)
    String save;
    @BindView(R.id.save_feed)
    Button saveButton;

    AppDatabase appDatabase;
    Mode mode = Mode.ADD;
    Feed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feed);

        ButterKnife.bind(this);
        initViews();

        appDatabase = AppDatabase.getInstance(getApplicationContext());
        //Source: https://stackoverflow.com/questions/2763022/android-how-can-i-validate-edittext-input/11838715#11838715
        editUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (URLUtil.isValidUrl(editUrl.getText().toString())) {
                    enableSaveButton(true);
                } else {
                    enableSaveButton(false);
                }
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEYS.FEED_ID)) {
            final int feedId = intent.getIntExtra(KEYS.FEED_ID, 0);
            enableSaveButton(true);
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    feed = appDatabase.getFeedDao().loadById(feedId);
                    mode = Mode.EDIT;
                    editTitle.setText(feed.getTitle());
                    editUrl.setText(feed.getLink());
                    editUrl.setText(feed.getLink());
                    saveButton.setText(save);
                    return null;
                }
            };
            task.execute();
        } else{
            enableSaveButton(false);
            getSupportActionBar().setTitle(getResources().getString(R.string.add_feed));
        }
    }

    //Source: https://stackoverflow.com/questions/22803476/animators-may-only-be-run-on-looper-threads-on-sherlock-action-bar
    private void enableSaveButton(final boolean enable){
        runOnUiThread(new Runnable() {

            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveButton.setEnabled(enable);
                    }
                }, 50);

            }
        });
    }

    @OnClick(R.id.save_feed)
    public void saveFeed() {
        try {
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    String stringUrl = editUrl.getText().toString();
                    String stringTitle = editTitle.getText().toString();
                    if (mode == Mode.ADD) {
                        feed = new Feed();
                        feed.setTitle(stringTitle);
                        feed.setLink(stringUrl);
                        appDatabase.getFeedDao().insertAll(feed);
                        AnalyticsUtil.logNewFeed(getApplicationContext(), feed);
                    } else { //Mode.Edit
                        feed.setTitle(stringTitle);
                        feed.setLink(stringUrl);
                        appDatabase.getFeedDao().update(feed);
                    }
                    finish();
                    return null;
                }
            };
            task.execute();
        } catch (Exception e) {
            Log.e("Tag", e.getMessage());

        }

    }
    enum Mode {
        EDIT,
        ADD
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

}
