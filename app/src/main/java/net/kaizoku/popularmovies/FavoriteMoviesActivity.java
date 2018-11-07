package net.kaizoku.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.kaizoku.popularmovies.database.MovieDatabase;
import net.kaizoku.popularmovies.database.model.Movie;

import java.util.List;

public class FavoriteMoviesActivity extends AppCompatActivity {

    private static final String TAG = "FavoriteMoviesActivity";
    private MovieDatabase movieDatabase;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        movieDatabase = MovieDatabase.getDatabase(getApplicationContext());

        ListMoviesAsyncTask task = new ListMoviesAsyncTask();
        task.execute();

    }

    private class ListMoviesAsyncTask extends AsyncTask<String, String, String> {

        List<Movie> movies;

        @Override
        protected String doInBackground(String... strings) {
            movies = movieDatabase.daoAccess().getMovies();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for (Movie movie : movies) {
                Log.i(TAG, "onCreate: " + movie.toString());
            }
            MoviesAdapter customAdapter = new MoviesAdapter(FavoriteMoviesActivity.this, movies);
            recyclerView.setAdapter(customAdapter);
        }
    }
}
