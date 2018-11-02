package net.kaizoku.popularmovies.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.model.MovieReview;
import net.kaizoku.popularmovies.model.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static String TAG = "JsonUtils";

    public static ArrayList<Movie> parseMovieJson(String json) throws JSONException {

        if (json.isEmpty()) {
            Log.i(TAG, "parseMovieJson: json is empty or null");
            return null;
        } else {
            JSONObject mainObject = new JSONObject(json);
            JSONArray resultsArray = mainObject.getJSONArray("results");

            ArrayList<Movie> movies;

            movies = new Gson().fromJson(
                    resultsArray.toString(),
                    new TypeToken<List<Movie>>(){}.getType()
            );

            return movies;
        }
    }

    public static ArrayList<MovieTrailer> parseMovieTrailerJson(String json) throws JSONException {
        if (json.isEmpty()) {
            Log.i(TAG, "parseMovieTrailerJson: json is empty or null");
            return null;
        } else {
            JSONObject mainObject = new JSONObject(json);
            JSONArray resultsArray = mainObject.getJSONArray("results");

            ArrayList<MovieTrailer> movieTrailers;

            movieTrailers = new Gson().fromJson(
                    resultsArray.toString(),
                    new TypeToken<List<MovieTrailer>>() {
                    }.getType()
            );

            return movieTrailers;
        }
    }

    public static ArrayList<MovieReview> parseMovieReview(String json) throws JSONException {
        if (json.isEmpty()) {
            Log.i(TAG, "parseMovieTrailerJson: json is empty or null");
            return null;
        } else {
            Log.i(TAG, "parseMovieReview: entered else block");
            JSONObject mainObject = new JSONObject(json);
            JSONArray resultsArray = mainObject.getJSONArray("results");

            ArrayList<MovieReview> movieReviews;

            movieReviews = new Gson().fromJson(
                    resultsArray.toString(),
                    new TypeToken<List<MovieReview>>() {
                    }.getType()
            );

            Log.i(TAG, "parseMovieReview: " + movieReviews);

            return movieReviews;
        }
    }
}
