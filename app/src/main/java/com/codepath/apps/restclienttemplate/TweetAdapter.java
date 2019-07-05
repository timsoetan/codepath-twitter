package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // For each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        final ViewHolder viewHolder = new ViewHolder(tweetView);

        viewHolder.bRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Tweet tweet = mTweets.get(position);
                tweet.updateRetweetCount(new JsonHttpResponseHandler());
                toggleRetweet(viewHolder.bRetweet, tweet.isRetweeted());
                viewHolder.tvRetweets.setText(Tweet.formatCount(tweet.getRetweetCount()));
            }
        });

        viewHolder.bFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Tweet tweet = mTweets.get(position);
                tweet.updateFavoriteCount(new JsonHttpResponseHandler());
                toggleFavorite(viewHolder.bFavorite, tweet.isFavorited());
                viewHolder.tvFavorites.setText(Tweet.formatCount(tweet.getFavoriteCount()));
            }
        });

        viewHolder.bReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof TimelineActivity) {
                    String replyTo = viewHolder.tvUserName.getText().toString();
                    ((TimelineActivity) context).replyTo(replyTo,
                            mTweets.get(viewHolder.getAdapterPosition()).getUid());
                }
            }
        });

        return viewHolder;
    }


    private void toggleRetweet(Button button, Boolean retweeted) {
        if (retweeted) {
            button.setBackgroundResource(R.drawable.ic_vector_retweet_green);
        } else {
            button.setBackgroundResource(R.drawable.ic_vector_retweet);
        }
    }

    private void toggleFavorite(Button button, Boolean favorited) {
        if (favorited) {
            button.setBackgroundResource(R.drawable.ic_vector_red_heart);
        } else {
            button.setBackgroundResource(R.drawable.ic_vector_heart);
        }
    }

    private int initialRetweet(Boolean retweeted) {
        if (retweeted) {
            return R.drawable.ic_vector_retweet_green;
        } else {
            return R.drawable.ic_vector_retweet;
        }
    }

    private int initialFavorite(Boolean favorited) {
        if (favorited) {
            return R.drawable.ic_vector_red_heart;
        } else {
            return R.drawable.ic_vector_heart;
        }
    }

    // Bind the values based on the position of the element
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get the data according to this data
        Tweet tweet = mTweets.get(position);

        // Populate the views according to this data
        holder.tvProfileName.setText(tweet.getUser().getName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvUserName.setText("@" + tweet.getUser().getScreenName());
        holder.tvTime.setText(tweet.getCreatedAt());
        holder.tvRetweets.setText(Tweet.formatCount(tweet.getRetweetCount()));
        holder.tvFavorites.setText(Tweet.formatCount(tweet.getFavoriteCount()));
        holder.bFavorite.setBackgroundResource(initialFavorite(tweet.isFavorited()));
        holder.bRetweet.setBackgroundResource(initialRetweet(tweet.isRetweeted()));

        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .asBitmap().centerCrop()
                .into(new BitmapImageViewTarget(holder.ivProfileImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                .create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.ivProfileImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

        if (tweet.getMedia().hasMedia()) {
            Glide.with(context)
                    .load(tweet.getMedia().getMediaUrl())
                    .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    .into(holder.ivTweetMedia);
            holder.ivTweetMedia.setVisibility(View.VISIBLE);
        } else {
            holder.ivTweetMedia.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivProfileImage;
        public ImageView ivTweetMedia;
        public TextView tvProfileName;
        public TextView tvBody;
        public TextView tvUserName;
        public TextView tvTime;
        public TextView tvRetweets;
        public TextView tvFavorites;
        public Button bRetweet;
        public Button bFavorite;
        public Button bReply;

        public ViewHolder(View itemView) {
            super(itemView);

            // Perform findViewById lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivTweetMedia = (ImageView) itemView.findViewById(R.id.ivTweetMedia);
            tvProfileName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvRetweets = (TextView) itemView.findViewById(R.id.Retweets);
            tvFavorites = (TextView) itemView.findViewById(R.id.tvFavorites);
            bRetweet = (Button) itemView.findViewById(R.id.bRetweet);
            bFavorite = (Button) itemView.findViewById(R.id.bFavorite);
            bReply = (Button) itemView.findViewById(R.id.bReply);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Gets item position
            int position = getAdapterPosition();
            // Make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // Get the movie at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                // Create intent for the new activity
                Intent intent = new Intent(context, TweetDetailActivity.class);
                // Serialize the movie using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // Show the activity
                context.startActivity(intent);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

}
