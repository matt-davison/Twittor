package com.codepath.apps.twittor;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.twittor.models.SampleModel;
import com.codepath.apps.twittor.models.SampleModelDao;
import com.codepath.apps.twittor.models.Tweet;
import com.codepath.apps.twittor.models.TweetDao;
import com.codepath.apps.twittor.models.User;

@Database(entities={SampleModel.class, Tweet.class, User.class}, version=5)
public abstract class MyDatabase extends RoomDatabase {
    public abstract SampleModelDao sampleModelDao();

    public abstract TweetDao tweetDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
