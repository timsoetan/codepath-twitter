package com.codepath.apps.restclienttemplate;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    // The movie to display
    private Tweet tweet;

    // The view objects
    private ImageView ivProfileImage;
    private ImageView ivTweetMedia;
    private ImageView ivBack;
    private TextView tvProfileName;
    private TextView tvBody;
    private TextView tvUserName;
    private TextView tvTime;
    private TextView tvRetweets;
    private TextView tvFavorites;
    private Button bRetweet;
    private Button bFavorite;
    private Button bReply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout_tweet);

        ivBack = (ImageView) findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivTweetMedia = (ImageView) findViewById(R.id.ivTweetMedia);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvRetweets = (TextView) findViewById(R.id.Retweets);
        tvFavorites = (TextView) findViewById(R.id.tvFavorites);
        bRetweet = (Button) findViewById(R.id.bRetweet);
        bFavorite = (Button) findViewById(R.id.bFavorite);
        bReply = (Button) findViewById(R.id.bReply);

        tvProfileName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvUserName.setText("@" + tweet.getUser().getScreenName());
        tvRetweets.setText(Tweet.formatCount(tweet.getRetweetCount()));
        tvFavorites.setText(Tweet.formatCount(tweet.getFavoriteCount()));
        bFavorite.setBackgroundResource(initialFavorite(tweet.isFavorited()));
        bRetweet.setBackgroundResource(initialRetweet(tweet.isRetweeted()));

        Glide.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .asBitmap().centerCrop()
                .into(new BitmapImageViewTarget(ivProfileImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory
                                .create(TweetDetailActivity.this.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivProfileImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

        if (tweet.getMedia().hasMedia()) {
            Glide.with(this)
                    .load(tweet.getMedia().getMediaUrl())
                    .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                    .into(ivTweetMedia);
            ivTweetMedia.setVisibility(View.VISIBLE);
        } else {
            ivTweetMedia.setVisibility(View.GONE);
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
}
