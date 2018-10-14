package net.kaizoku.popularmovies.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.gson.Gson;


import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.utils.HttpHelper;
import net.kaizoku.popularmovies.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MyService extends IntentService {

    public static final String TAG = "MyService";
    public static final String MY_SERVICE_MESSAGE = "MyServiceMessage";
    public static final String MY_SERVICE_PAYLOAD = "MyServicePayload";

    public MyService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Uri uri = intent.getData();
        Log.i(TAG, "onHandleIntent: " + uri.toString());

        String response;

        try {
            response = HttpHelper.downloadUrl(uri.toString());
            Log.i(TAG, "onHandleIntent: data was fetched from API");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<Movie> movies = null;
        try {
            movies = JsonUtils.parseMovieJson(response);
            Log.i(TAG, "onHandleIntent: response was parsed");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_SERVICE_PAYLOAD, movies);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);

        Log.i(TAG, "onHandleIntent: response = " + response);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
