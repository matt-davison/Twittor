package com.codepath.apps.twittor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.twittor.adapters.TweetsAdapter;
import com.codepath.apps.twittor.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.apps.twittor.databinding.ActivityTimelineBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    TwittorClient client;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    //ActivityTimelineBinding binding;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeContainer;
    MenuItem miNetworkProgress;

    private boolean networkInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        rvTweets = findViewById(R.id.rvTweets);
        swipeContainer = findViewById(R.id.swipeContainer);

        client = TwittorApp.getRestClient(this);

        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data!");
                populateHomeTimelineAsync(3);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateHomeTimelineAsync(3);

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Tweet selectedTweet = tweets.get(position);
                        Intent i = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
                        i.putExtra("tweet", Parcels.wrap(selectedTweet));
                        startActivity(i);
                    }
                }
        );
    }

    private void populateHomeTimelineAsync(final int attempts) {
        showProgressBar();
        if (attempts <= 0) {
            Toast.makeText(this, "Fetch Failed! Check network connection!", Toast.LENGTH_LONG);
            swipeContainer.setRefreshing(false);
            miNetworkProgress.setVisible(false);
            return;
        }
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    Log.i(TAG, "Tweets displayed");
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json Exception", e);
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + Integer.toString(attempts-1) + " retries left. " + response, throwable);
                populateHomeTimelineAsync(attempts-1);
            }
        });
    }

    //TODO: Do I need to override this method or can this be done in onCreateOptionsMenu()?
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miNetworkProgress = menu.findItem(R.id.miNetworkProgress);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miNetworkProgress = menu.findItem(R.id.miNetworkProgress);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Toast.makeText(this, "Compose!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showProgressBar() {
        // Show progress item
        if (miNetworkProgress != null) {
            miNetworkProgress.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        if (miNetworkProgress != null) {
            miNetworkProgress.setVisible(false);
        }
    }
}