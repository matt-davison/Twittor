package com.codepath.apps.twittor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.twittor.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;


public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;
    TextInputLayout tilCharacterCounter;
    TwittorClient client;
    MenuItem miNetworkProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tilCharacterCounter = findViewById(R.id.tilCharacterCounter);
        tilCharacterCounter.setCounterMaxLength(MAX_TWEET_LENGTH);

        client = TwittorApp.getRestClient(this);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry your tweet cannot be empty", Toast.LENGTH_LONG).show();
                } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                } else {
                    //Make tweet!
                    showProgressBar();
                    Log.i(TAG, "Sending tweet");
                    client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to publish tweet");
                            hideProgressBar();
                            Tweet tweet = null;
                            try {
                                tweet = Tweet.fromJson(json.jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent data = new Intent();
                            data.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, data);
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to publish tweet: \n" + response, throwable);
                            Toast toast = Toast.makeText(ComposeActivity.this, "Sorry, your tweet could not be posted!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //miNetworkProgress = menu.findItem(R.id.miNetworkProgress);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miNetworkProgress = menu.findItem(R.id.miNetworkProgress);
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