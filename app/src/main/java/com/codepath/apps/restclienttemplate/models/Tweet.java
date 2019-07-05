package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@Parcel
public class Tweet {

    // Attributes
    private String body;
    private long uid; // Database ID for the tweet
    private User user;
    private String createdAt;
    private long retweetCount;
    private long favoriteCount;
    private boolean retweeted;
    private boolean favorited;
    private Media media;

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    public Tweet() {
    }

    // Deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");

        Tweet tweet = new Tweet();

        // Extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"));
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.retweetCount = jsonObject.getLong("retweet_count");
        tweet.favoriteCount = jsonObject.getLong("favorite_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.media = Media.fromJSON(jsonObject.getJSONObject("entities"));

        return tweet;
    }


    public void updateRetweetCount(JsonHttpResponseHandler handler) {
        TwitterApp.getRestClient().retweet(uid, retweeted = !retweeted, handler);
        if (retweeted) {
            retweetCount += 1;
        } else {
            retweetCount -= 1;
        }
    }

    public void updateFavoriteCount(JsonHttpResponseHandler handler) {
        TwitterApp.getRestClient().favorite(uid, favorited = !favorited, handler);
        if (favorited) {
            favoriteCount += 1;
        } else {
            favoriteCount -= 1;
        }
    }

    public static String formatCount(long value) {
        if (value == Long.MIN_VALUE) {
            return formatCount(Long.MIN_VALUE + 1);
        }
        if (value < 0) {
            return "-" + formatCount(-value);
        }
        if (value < 1000) {
            return Long.toString(value);
        }

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public String getBody() {
        return body;
    }

    public Media getMedia() {
        return media;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public boolean isFavorited() {
        return favorited;
    }
}
