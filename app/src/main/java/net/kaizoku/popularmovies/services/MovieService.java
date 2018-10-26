package net.kaizoku.popularmovies.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.gson.Gson;


import net.kaizoku.popularmovies.DetailedMovieActivity;
import net.kaizoku.popularmovies.MainActivity;
import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.model.MovieTrailer;
import net.kaizoku.popularmovies.utils.HttpHelper;
import net.kaizoku.popularmovies.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MovieService extends IntentService {

    public static final String TAG = "MovieService";

    public static final String MY_MOVIE_SERVICE_PAYLOAD = "MyMovieServicePayload";
    public static final String MY_MOVIE_SERVICE_MESSAGE = "MyMovieServiceMessage";

    public static final String MY_TRAILER_SERVICE_PAYLOAD = "MyTrailerServicePayload";
    public static final String MY_TRAILER_SERVICE_MESSAGE = "MyTrailerServiceMessage";

    public MovieService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Uri uri = intent.getData();
        Log.i(TAG, "onHandleIntent: " + uri.toString());

        String response = null;

        if (uri.toString().equals(MainActivity.MY_URL1) ||
                uri.toString().equals(MainActivity.MY_URL2)) {

            try {
                response = HttpHelper.downloadUrl(uri.toString());
                Log.i(TAG, "onHandleIntentMovie: data was fetched from API");
                Log.i(TAG, "onHandleIntentMovie: response = " + response);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ArrayList<Movie> movies = null;
            try {
                movies = JsonUtils.parseMovieJson(response);
                Log.i(TAG, "onHandleIntentMovie: response was parsed");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Intent messageIntent = new Intent(MY_MOVIE_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_MOVIE_SERVICE_PAYLOAD, movies);
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
            Log.i(TAG, "onHandleIntentMovie: response = " + response);
        } else if (uri.toString().startsWith(DetailedMovieActivity.TRAILER_URL) &&
                uri.toString().contains("videos")) {

            try {
                response = HttpHelper.downloadUrl(uri.toString());
                Log.i(TAG, "onHandleIntentTrailer: data was fetched from API");
                Log.i(TAG, "onHandleIntentTrailer: response = " + response);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ArrayList<MovieTrailer> movieTrailers = null;
            try {
                movieTrailers = JsonUtils.parseMovieTrailerJson(response);
                Log.i(TAG, "onHandleIntentTrailer: response was parsed");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent messageIntent = new Intent(MY_TRAILER_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_TRAILER_SERVICE_PAYLOAD, movieTrailers);
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);

            Log.i(TAG, "onHandleIntentTrailer: response = " + response);
        }
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
