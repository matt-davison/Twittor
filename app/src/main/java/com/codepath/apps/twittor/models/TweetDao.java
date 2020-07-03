package com.codepath.apps.twittor.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

            @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.long_id AS tweet_long_id, Tweet.id AS tweet_id, Tweet.user_id AS tweet_user_id, User.* " +
            "FROM Tweet INNER JOIN User ON Tweet.user_id = User.long_id ORDER BY Tweet.createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
