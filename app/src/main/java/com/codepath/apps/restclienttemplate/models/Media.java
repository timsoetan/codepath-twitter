package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    // Attributes
    private String mediaUrl;
    private boolean hasMedia;

    public Media() {
    }

    public static Media fromJSON(JSONObject jsonObject) throws JSONException {
        Media tweetMedia = new Media();

        try {
            JSONArray media = jsonObject.getJSONArray("media");
            JSONObject tweet = media.getJSONObject(0);
            tweetMedia.mediaUrl = tweet.getString("media_url_https");
            tweetMedia.hasMedia = true;
        } catch (JSONException e) {

        }

        return tweetMedia;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public boolean hasMedia() {
        return hasMedia;
    }
}
