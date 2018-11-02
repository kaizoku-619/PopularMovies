package net.kaizoku.popularmovies.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.kaizoku.popularmovies.DetailedMovieActivity;
import net.kaizoku.popularmovies.MainActivity;
import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.model.MovieReview;
import net.kaizoku.popularmovies.model.MovieTrailer;
import net.kaizoku.popularmovies.utils.HttpHelper;
import net.kaizoku.popularmovies.utils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MovieService extends IntentService {

    /*
                                *** Service Job ***
        1- fetch Movies endpoint ,parse Movies Json and return a list of Movies
        2- fetch Trailers endpoint, parse Trailers Json and return a list of Trailers
        3- fetch Reviews endpoint, Reviews Json and return a list of Reviews
     */

    public static final String TAG = "MovieService";

    public static final String MY_MOVIE_SERVICE_PAYLOAD = "MyMovieServicePayload";
    public static final String MY_MOVIE_SERVICE_MESSAGE = "MyMovieServiceMessage";

    public static final String MY_TRAILER_AND_REVIEW_SERVICE_MESSAGE = "MyTrailerAndReviewServiceMessage";

    public static final String MY_REVIEW_SERVICE_PAYLOAD = "MyReviewServicePayload";
    public static final String MY_REVIEW_SERVICE_MESSAGE = "MyReviewServiceMessage";

    public static final String MY_TRAILER_SERVICE_PAYLOAD = "MyTrailerServicePayload";
    public static final String MY_TRAILER_SERVICE_MESSAGE = "MyTrailerServiceMessage";

    String moviesResponse = null;
    String trailersResponse = null;
    String reviewsResponse = null;


    public MovieService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            if (intent.getData().toString().equals(MainActivity.MY_URL1) ||
                    intent.getData().toString().equals(MainActivity.MY_URL2)) {
                Uri uri = intent.getData();
                startMovieService(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "onCreate: OnHandleIntent intent.getData() failed");
        } finally {
            Log.i(TAG, "onCreate: entered finaly block");
            try {
                if (intent.getStringExtra("TrailerUrl").equals(DetailedMovieActivity.TRAILER_URL) ||
                        intent.getStringExtra("ReviewUrl").equals(DetailedMovieActivity.REVIEW_URL)
                        ) {
                    Uri trailerUrl = Uri.parse(intent.getStringExtra("TrailerUrl"));
                    Uri reviewUrl = Uri.parse(intent.getStringExtra("ReviewUrl"));
                    Log.i(TAG, "onCreate: trailerUrl = " + trailerUrl.toString());
                    Log.i(TAG, "onHandleIntentReviewUrl: " + reviewUrl.toString());
                    startTrailerAndReviewService(trailerUrl, reviewUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "onCreate: OnHandleIntent intent.getStringExtra() failed");
            }
        }

    }

    public void startMovieService(Uri movieUrl) {
        try {
            moviesResponse = HttpHelper.downloadUrl(movieUrl.toString());
            Log.i(TAG, "onHandleIntentMovie: data was fetched from API");
            Log.i(TAG, "onHandleIntentMovie: response = " + moviesResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<Movie> movies = null;
        try {
            movies = JsonUtils.parseMovieJson(moviesResponse);
            Log.i(TAG, "onHandleIntentMovie: response was parsed");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent messageIntent = new Intent(MY_MOVIE_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_MOVIE_SERVICE_PAYLOAD, movies);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
        Log.i(TAG, "onHandleIntentMovie: response = " + moviesResponse);
    }

    public void startTrailerAndReviewService(Uri trailerUri, Uri reviewUri) {
        try {
            trailersResponse = HttpHelper.downloadUrl(trailerUri.toString());
            Log.i(TAG, "onHandleIntentTrailer: data was fetched from API");
            Log.i(TAG, "onHandleIntentTrailer: response = " + trailersResponse);

            reviewsResponse = HttpHelper.downloadUrl(reviewUri.toString());
            Log.i(TAG, "onHandleIntentReview: data was fetched from API");
            Log.i(TAG, "onHandleIntentReview: response = " + reviewsResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<MovieTrailer> movieTrailers = null;
        ArrayList<MovieReview> movieReviews = null;

        try {
            movieTrailers = JsonUtils.parseMovieTrailerJson(trailersResponse);
            Log.i(TAG, "onHandleIntentTrailer: response was parsed");

            movieReviews = JsonUtils.parseMovieReview(reviewsResponse);
            Log.i(TAG, "onHandleIntentReview: response was parsed");
            for (MovieReview review : movieReviews) {
                Log.i(TAG, "startTrailerAndReviewService: " + review.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent messageIntent = new Intent(MY_TRAILER_AND_REVIEW_SERVICE_MESSAGE);
        messageIntent.putExtra(MY_TRAILER_SERVICE_PAYLOAD, movieTrailers);
        messageIntent.putExtra(MY_REVIEW_SERVICE_PAYLOAD, movieReviews);
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Service Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
