package com.codepath.apps.twittor.adapters;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.twittor.R;
//import com.codepath.apps.twittor.databinding.ItemTweetBinding;
import com.codepath.apps.twittor.models.Tweet;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.List;
//TODO: Fix binding
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    //ItemTweetBinding binding;

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //binding = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false);
        //return new ViewHolder(binding);
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        Log.i("TweetsAdapter", "created viewholder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("TweetsAdapter", "going to bind " + position);
        holder.bind(tweets.get(position));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        Log.i("TweetsAdapter", "Tweets in adapter's list: " + Integer.toString(list.size()));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTime;
        ImageView ivMedia;

        public ViewHolder(@NonNull View itemView) {
            //super(binding.getRoot());
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivMedia = itemView.findViewById(R.id.ivMedia);
        }

        public void bind(Tweet tweet) {
            //binding.tvBody.setText(tweet.body);
            //binding.tvScreenName.setText(tweet.user.screenName);
            //Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            tvBody.setText(tweet.body);
            tvScreenName.setText("(@" + tweet.user.screenName + ")");
            tvName.setText(tweet.user.name);
            tvTime.setText(Tweet.getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
            Glide.with(context).load(tweet.mediaPath).transform(new RoundedCorners(60)).into(ivMedia);
            Log.i("TweetsAdapter", "Tweet from" + tweet.user.screenName + " bound");
        }
    }
}
