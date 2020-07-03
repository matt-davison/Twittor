package com.codepath.apps.twittor.models;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="long_id", childColumns="user_id"))
public class Tweet {

    @ColumnInfo
    @PrimaryKey
    public Long long_id;

    @ColumnInfo
    public Long user_id;

    @ColumnInfo
    public String id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @Ignore
    public User user;

    @Ignore
    public String mediaPath;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getString("id_str");
        tweet.long_id = jsonObject.getLong("id");
        tweet.user_id = tweet.user.long_id;
        try {
            tweet.mediaPath = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");

        } catch (JSONException e) {
            tweet.mediaPath = null;
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets =new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
