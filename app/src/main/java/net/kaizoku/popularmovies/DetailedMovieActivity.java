package net.kaizoku.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.model.MovieReview;
import net.kaizoku.popularmovies.model.MovieTrailer;
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
    private ArrayList<MovieTrailer> myMovieTrailers;
    private ArrayList<MovieReview> myMovieReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);

        initView();

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

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
}
