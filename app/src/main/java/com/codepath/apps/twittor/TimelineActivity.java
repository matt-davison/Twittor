package com.codepath.apps.twittor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twittor.adapters.TweetsAdapter;
import com.codepath.apps.twittor.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.apps.twittor.databinding.ActivityTimelineBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";

    TwittorClient client;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    //ActivityTimelineBinding binding;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeContainer;

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
    }

    private void populateHomeTimelineAsync(final int attempts) {
        if (attempts <= 0) {
            Toast.makeText(this, "Fetch Failed! Check network connection!", Toast.LENGTH_LONG);
            swipeContainer.setRefreshing(false);
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
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + Integer.toString(attempts-1) + " retries left. " + response, throwable);
                populateHomeTimelineAsync(attempts-1);
            }
        });
    }
}