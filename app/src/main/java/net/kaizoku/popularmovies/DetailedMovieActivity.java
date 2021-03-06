package net.kaizoku.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.kaizoku.popularmovies.database.MovieDatabase;
import net.kaizoku.popularmovies.database.model.Movie;
import net.kaizoku.popularmovies.database.model.MovieReview;
import net.kaizoku.popularmovies.database.model.MovieTrailer;
import net.kaizoku.popularmovies.utils.HttpHelper;
import net.kaizoku.popularmovies.utils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import static net.kaizoku.popularmovies.MainActivity.API_KEY;

public class DetailedMovieActivity extends AppCompatActivity {

    private static final String TAG = "DetailedMovieActivity";
    public static String TRAILER_URL, REVIEW_URL;
    private TextView title, desc, rating, releaseDate, reviewTv;
    private ImageView poster;
    private ListView listView;
    private FloatingActionButton fab;
    private ArrayList<MovieTrailer> myMovieTrailers;
    private ArrayList<MovieReview> myMovieReviews;
    private Movie movie;
    private static boolean isFavorite;

    private MovieDatabase movieDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);

        movieDatabase = MovieDatabase.getDatabase(getApplicationContext());

        initView();

        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");

        TRAILER_URL = "http://api.themoviedb.org/3/movie/" +
                movie.getId()
                + "/videos?api_key=" + API_KEY;
        REVIEW_URL = "https://api.themoviedb.org/3/movie/" +
                movie.getId() +
                "/reviews?api_key=" + API_KEY;

        TrailerAsyncTask trailerAsyncTask = new TrailerAsyncTask();
        trailerAsyncTask.execute(TRAILER_URL);

        ReviewAsyncTask reviewAsyncTask = new ReviewAsyncTask();
        reviewAsyncTask.execute(REVIEW_URL);

        showMovieDetails(movie);

        UpdateFavoriteMovieAsyncTask updateFavoriteMovieAsyncTask = new UpdateFavoriteMovieAsyncTask();
        updateFavoriteMovieAsyncTask.execute();

        Log.i(TAG, "onCreate: movie = " + movie.toString());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    watchYoutubeVideo(getApplicationContext(), myMovieTrailers.get(position).getKey());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    DeleteMovieAsyncTask deleteMovieAsyncTask = new DeleteMovieAsyncTask();
                    deleteMovieAsyncTask.execute();
                    fab.setImageResource(R.drawable.baseline_favorite_border_red_18dp);
                    Snackbar.make(v, "Movie removed from favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    AddMovieAsyncTask addMovieAsyncTask = new AddMovieAsyncTask();
                    addMovieAsyncTask.execute();
                    fab.setImageResource(R.drawable.baseline_favorite_red_18dp);
                    Snackbar.make(v, "Movie added to favorites", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        listView = (findViewById(R.id.listView));

        View headerView = getLayoutInflater().inflate(R.layout.activity_detailed_movie_header, null);
        listView.addHeaderView(headerView);

        View footerView = getLayoutInflater().inflate(R.layout.activity_detailed_movie_footer, null);
        listView.addFooterView(footerView);

        reviewTv = (findViewById(R.id.review_tv));

        title = (findViewById( R.id.movie_title));
        desc = (findViewById(R.id.movie_desc));
        rating = (findViewById(R.id.movie_rating));
        releaseDate = (findViewById(R.id.release_date));
        poster = (findViewById(R.id.movie_poster));
        fab = findViewById(R.id.fab);
    }

    private void showMovieDetails(Movie movie) {
        title.setText(movie.getOriginalTitle());
        desc.setText(movie.getOverview());
        rating.setText(movie.getVoteAverage() + " / 10");
        releaseDate.setText(movie.getReleaseDate());

        Picasso.with(getApplicationContext())
                .load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(poster);
    }

    private void showMovieReviews(ArrayList<MovieReview> movieReviews) {
        for (MovieReview movieReview : movieReviews) {
            try {
                reviewTv.append(
                    "\nAuthor: \n" + movieReview.getAuthor() +
                    "\n\nContent: \n" + movieReview.getContent() +
                    "\n____________________________________________________________\n"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void watchYoutubeVideo(Context context, String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    private class TrailerAsyncTask extends AsyncTask<String, String, String> {

        String response;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = HttpHelper.downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                myMovieTrailers = JsonUtils.parseMovieTrailerJson(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (MovieTrailer trailer : myMovieTrailers) {
                Log.i(TAG, "onPostExecute: " + trailer.toString());
            }
            ArrayList<String> trailerNames = new ArrayList<>();
            for (MovieTrailer trailer : myMovieTrailers) {
                trailerNames.add(trailer.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    trailerNames
            );
            listView.setAdapter(adapter);
        }
    }

    private class ReviewAsyncTask extends AsyncTask<String, String, String> {

        String response;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = HttpHelper.downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                myMovieReviews = JsonUtils.parseMovieReview(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showMovieReviews(myMovieReviews);
        }
    }

    private class AddMovieAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            movieDatabase.daoAccess().insertMovie(movie);
            return null;
        }
    }

    private class UpdateFavoriteMovieAsyncTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            if (movieDatabase.daoAccess().getMovie(movie.getId()) == null) {
                return false;
            } else if (movie.getId() == movieDatabase.daoAccess().getMovie(movie.getId()).getId()) {
                return true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                fab.setImageResource(R.drawable.baseline_favorite_red_18dp);
                isFavorite = true;
                Log.i(TAG, "onPostExecute: favorite = true");
            } else {
                fab.setImageResource(R.drawable.baseline_favorite_border_red_18dp);
                isFavorite = false;
                Log.i(TAG, "onPostExecute: favorite = false");
            }
        }
    }

    private class DeleteMovieAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            movieDatabase.daoAccess().deleteMovie(movie);
            return null;
        }
    }

}
