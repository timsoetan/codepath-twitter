package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;

    private MenuItem miActionProgressItem;

    private SwipeRefreshLayout swipeContainer;
    private final int COMPOSE_TWEET_REQUEST_CODE = 50;
    private final int REPLY_TWEET_REQUEST_CODE = 100;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout_home);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.twitter_blue);

        client = TwitterApp.getRestClient();

        // Find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        // Instantiate the array list (data source)
        tweets = new ArrayList<>();

        // Construct the adapter from the data source
        tweetAdapter = new TweetAdapter(tweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // RecyclerView setup
        rvTweets.setLayoutManager(linearLayoutManager);

        // Set the adapter
        rvTweets.setAdapter(tweetAdapter);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
    }

    public void loadNextDataFromApi(int offset) {
        if (tweets.isEmpty()) {
            return;
        } else {
            long maxId = tweets.get(tweets.size() - 1).getUid() - 1;
            populateTimeline(maxId);
        }
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.=
        tweetAdapter.clear();
        populateTimeline(0);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeTweet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_TWEET_REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        } else if (requestCode == REPLY_TWEET_REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("replied_tweet"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    public void composeTweet() {
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, COMPOSE_TWEET_REQUEST_CODE);
    }

    public void replyTo(String starter, long uid) {
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("reply", starter);
        i.putExtra("reply_id", uid);
        startActivityForResult(i, REPLY_TWEET_REQUEST_CODE);
    }

    private void populateTimeline(final long maxId) {
        showProgressBar();
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("TwitterClient", response.toString());

                // Iterate through the JSON array

                // For each entry, deserialize the JSON object
                if (maxId == 0) {
                    tweetAdapter.clear();
                }
                for (int i = 0; i < response.length(); i++) {
                    // Convert each object to a Tweet model
                    // Add Tweet model to data source
                    // Notify the adapter that item was added
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                hideProgressBar();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        populateTimeline(0);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
            // Show progress item
            miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
            // Hide progress item
            miActionProgressItem.setVisible(false);
    }
}
