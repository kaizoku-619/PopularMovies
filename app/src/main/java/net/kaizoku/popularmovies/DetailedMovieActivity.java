package net.kaizoku.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import net.kaizoku.popularmovies.model.Movie;

public class DetailedMovieActivity extends AppCompatActivity {

    private TextView title, desc, rating, releaseDate;
    private ImageView poster;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_movie);

        initView();

        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");

        try {
            title.setText(movie.getOriginalTitle());
            desc.setText(movie.getOverview());
            rating.setText(movie.getVoteAverage() + " / 10");
            releaseDate.setText(movie.getReleaseDate());

            Picasso.with(getApplicationContext())
                    .load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(poster);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        title = (findViewById(R.id.movie_title));
        desc = (findViewById(R.id.movie_desc));
        rating = (findViewById(R.id.movie_rating));
        releaseDate = (findViewById(R.id.release_date));
        poster = (findViewById(R.id.movie_poster));
    }
}
