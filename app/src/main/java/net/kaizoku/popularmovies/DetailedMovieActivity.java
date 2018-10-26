package net.kaizoku.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.model.MovieTrailer;
import net.kaizoku.popularmovies.services.MovieService;

import java.util.ArrayList;

public class DetailedMovieActivity extends AppCompatActivity {

    private static final String TAG = "DetailedMovieActivity";
    public static String TRAILER_URL;
    private TextView title, desc, rating, releaseDate;
    private ImageView poster;
    private ListView listView;
    private Movie movie;
    ArrayList<MovieTrailer> myMovieTrailers;
    ArrayList<String> trailerNames;
    ArrayAdapter<String> adapter;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myMovieTrailers = intent.getParcelableArrayListExtra(MovieService.MY_TRAILER_SERVICE_PAYLOAD);
            Log.i(TAG, "onReceive: " + myMovieTrailers);
            trailerNames = new ArrayList<>();
            for (MovieTrailer trailer : myMovieTrailers) {
                trailerNames.add(trailer.getName());
            }
            Log.i(TAG, "onReceive: trailerNames = " + trailerNames);
            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, trailerNames);
            listView.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);

        initView();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MovieService.MY_TRAILER_SERVICE_MESSAGE));

        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");


        TRAILER_URL = "http://api.themoviedb.org/3/movie/" +
                movie.getId()
                + "/videos?api_key=" + MainActivity.API_KEY;

        showMovieDetails();

        try {
            Intent mIntent = new Intent(this, MovieService.class);
            mIntent.setData(Uri.parse(TRAILER_URL));
            startService(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, "onItemClick: " + myMovieTrailers.get(position).getName());
                watchYoutubeVideo(getApplicationContext(), myMovieTrailers.get(position).getKey());
            }
        });
    }

    private void initView() {
        title = (findViewById(R.id.movie_title));
        desc = (findViewById(R.id.movie_desc));
        rating = (findViewById(R.id.movie_rating));
        releaseDate = (findViewById(R.id.release_date));
        poster = (findViewById(R.id.movie_poster));
//        trailer = (findViewById(R.id.trailer));
        listView = (findViewById(R.id.listView));
    }

    private void showMovieDetails() {
        title.setText(movie.getOriginalTitle());
        desc.setText(movie.getOverview());
        rating.setText(movie.getVoteAverage() + " / 10");
        releaseDate.setText(movie.getReleaseDate());

        Picasso.with(getApplicationContext())
                .load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(poster);
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
}
