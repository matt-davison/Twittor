package com.codepath.apps.twittor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.twittor.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    public static final String TAG = "TweetDetailsActivity";

    ImageButton ibLike;
    ImageButton ibRetweet;
    ImageButton ibReply;
    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvName;
    TextView tvTime;
    ImageView ivMedia;
    Tweet tweet;
    TwittorClient client;
    MenuItem miNetworkProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ibLike = findViewById(R.id.ibLike);
        ibReply = findViewById(R.id.ibReply);
        ibRetweet = findViewById(R.id.ibRetweet);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvName = findViewById(R.id.tvName);
        tvTime = findViewById(R.id.tvTime);
        ivMedia = findViewById(R.id.ivMedia);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.body);
        tvScreenName.setText("(@" + tweet.user.screenName + ")");
        tvName.setText(tweet.user.name);
        tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
        Glide.with(this).load(tweet.mediaPath).transform(new RoundedCorners(60)).into(ivMedia);
        Log.i("TweetsAdapter", "Tweet from" + tweet.user.screenName + " bound");

        client = TwittorApp.getRestClient(this);

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess");
                        hideProgressBar();
                        Toast toast = Toast.makeText(TweetDetailsActivity.this, "You've liked " + tweet.user.name + "'s tweet!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        //TODO: change v's color to red!
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure" + response, throwable);
                        Toast toast = Toast.makeText(TweetDetailsActivity.this, "Sorry, ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        hideProgressBar();
                    }
                });
            }
        });

        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                client.retweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess");
                        hideProgressBar();
                        Toast toast = Toast.makeText(TweetDetailsActivity.this, "You've retweeted " + tweet.user.name + "'s tweet!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        //TODO: turn v green!
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure" + response, throwable);
                        Toast toast = Toast.makeText(TweetDetailsActivity.this, "Sorry, ", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        hideProgressBar();
                    }
                });
            }
        });

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Open compose tweet and pass @screenname thru intent
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miNetworkProgress = menu.findItem(R.id.miNetworkProgress);
        menu.findItem(R.id.compose).setVisible(false);
        return true;
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