package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;
    private EditText etvComposeTweet;
    private ImageView ivProfileImage;
    private TextView charCount;
    private Button bTweet;
    private TextView tvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient();

        etvComposeTweet = findViewById(R.id.etvComposeTweet);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        charCount = findViewById(R.id.tvCharCount);
        bTweet = findViewById(R.id.bTweet);
        tvCancel = findViewById(R.id.tvCancel);

        etvComposeTweet.requestFocus();

        etvComposeTweet.addTextChangedListener(tweetCharWatcher);

        String reply = getIntent().getStringExtra("reply");
        final long uid = (long) getIntent().getLongExtra("reply_id", 0);
        etvComposeTweet.setText(reply);

        bTweet.setEnabled(false);
        bTweet.setAlpha((float) 0.3);

        bTweet.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (etvComposeTweet.getText().toString().contains("@")) {
                                              client.replyTo(etvComposeTweet.getText().toString(), uid, new JsonHttpResponseHandler() {
                                                  @Override
                                                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                      super.onSuccess(statusCode, headers, response);
                                                      Intent i = new Intent();
                                                      Tweet tweet = new Tweet();

                                                      try {
                                                          tweet = Tweet.fromJSON(response);
                                                          i.putExtra("replied_tweet", Parcels.wrap(tweet));
                                                          setResult(RESULT_OK, i);
                                                          finish();
                                                      } catch (JSONException e) {
                                                          e.printStackTrace();
                                                      } catch (ParseException e) {
                                                          e.printStackTrace();
                                                      }
                                                  }

                                                  @Override
                                                  public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                      super.onFailure(statusCode, headers, throwable, errorResponse);
                                                      Log.d("DEBUG", errorResponse.toString());
                                                  }
                                              });
                                          } else {
                                              client.sendTweet(etvComposeTweet.getText().toString(), new JsonHttpResponseHandler() {
                                                  @Override
                                                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                      super.onSuccess(statusCode, headers, response);
                                                      Intent i = new Intent();
                                                      Tweet tweet = new Tweet();

                                                      try {
                                                          tweet = Tweet.fromJSON(response);
                                                          i.putExtra("tweet", Parcels.wrap(tweet));
                                                          setResult(RESULT_OK, i);
                                                          finish();
                                                      } catch (JSONException e) {
                                                          e.printStackTrace();
                                                      } catch (ParseException e) {
                                                          e.printStackTrace();
                                                      }
                                                  }

                                                  @Override
                                                  public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                      super.onFailure(statusCode, headers, throwable, errorResponse);
                                                      Log.d("DEBUG", errorResponse.toString());
                                                  }
                                              });
                                          }
                                      }
                                  });

        etvComposeTweet.setSelection(etvComposeTweet.getText().length());

        User.getCurrentUser(new User.CurrentUserListener() {
            @Override
            public void onUserAuthenticated(User currentUser) {
                Glide.with(ComposeActivity.this)
                        .load(currentUser.getProfileImageUrl())
                        .asBitmap().centerCrop()
                        .into(new BitmapImageViewTarget(ivProfileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory
                                                .create(ComposeActivity.this.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ivProfileImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private final TextWatcher tweetCharWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            charCount.setText(String.valueOf(280 - s.length()));
            if (s.length() > 0 && s.toString().trim().length() > 0) {
                bTweet.setEnabled(true);
                bTweet.setAlpha((float) 1);
                charCount.setTextColor(getResources().getColor(R.color.medium_gray));
            } else {
                bTweet.setEnabled(false);
                bTweet.setAlpha((float) 0.3);
            }
            if (s.length() >= 260 && s.length() < 280) {
                charCount.setTextColor(getResources().getColor(R.color.twitter_yellow));
            }
            if (s.length() == 280) {
                charCount.setTextColor(getResources().getColor(R.color.medium_red));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
