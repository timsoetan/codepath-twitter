package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
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

        etvComposeTweet.requestFocus();

        etvComposeTweet.addTextChangedListener(tweetCharWatcher);

        bTweet.setEnabled(false);
        bTweet.setAlpha((float) 0.3);

        bTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.sendTweet(etvComposeTweet.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Tweet tweet = null;

                            try {
                                tweet = Tweet.fromJSON(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        Intent i = new Intent();
                            i.putExtra("new_tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            onBackPressed();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });
    }

    private final TextWatcher tweetCharWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            charCount.setText(String.valueOf(240 - s.length()));
            if (s.length() > 0 && s.toString().trim().length() > 0) {
                bTweet.setEnabled(true);
                bTweet.setAlpha((float) 1);
            } else {
                bTweet.setEnabled(false);
                bTweet.setAlpha((float) 0.3);
            }
            if (s.length() >= 220 && s.length() < 240) {
                charCount.setTextColor(getResources().getColor(R.color.twitter_yellow));
            }
            if (s.length() == 240) {
                charCount.setTextColor(getResources().getColor(R.color.medium_red));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
