package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;
    private EditText etvComposeTweet;
    private Button bTweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etvComposeTweet = findViewById(R.id.etvComposeTweet);
        bTweet = findViewById(R.id.bTweet);

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
                            }

                            Intent i = new Intent();
                            i.putExtra("new_tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            finish();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });
    }
}
