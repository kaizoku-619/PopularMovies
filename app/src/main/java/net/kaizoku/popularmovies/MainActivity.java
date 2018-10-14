package net.kaizoku.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import net.kaizoku.popularmovies.model.Movie;
import net.kaizoku.popularmovies.services.MyService;
import net.kaizoku.popularmovies.utils.NetworkHelper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static String sort = "popular";
    private boolean networkOk;
    private RecyclerView recyclerView;
    private ArrayList<Movie> myMovies = new ArrayList<>();
    private MoviesAdapter customAdapter;
    private static String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "Place your API key here";

    private static String MY_URL1 = BASE_URL + "popular" + "?api_key=" + API_KEY;
    private static String MY_URL2 = BASE_URL + "top_rated" + "?api_key=" + API_KEY;

//    private static String MY_URL = BASE_URL + sort + "?api_key=" + API_KEY;



    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Movie> movies =
                    intent.getParcelableArrayListExtra(MyService.MY_SERVICE_PAYLOAD);
            myMovies = movies;

            for (Movie movie : myMovies) {
                if (movie == null) {
                    Log.i(TAG, "onReceive: movie is null");
                } else {
                    Log.i(TAG, "onReceive: " + movie.toString());
                    customAdapter.setmMovieList(myMovies);
                }
            }
        }
    };


    private void initRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        customAdapter = new MoviesAdapter(MainActivity.this, myMovies);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecycler();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

        networkOk = NetworkHelper.hasNetworkAccess(getApplicationContext());

        if (networkOk) {
            if (sort.equals("popular")) {
                Intent intent = new Intent(this, MyService.class);
                intent.setData(Uri.parse(MY_URL1));
                startService(intent);
            } else if (sort.equals("top_rated")){
                Intent intent = new Intent(this, MyService.class);
                intent.setData(Uri.parse(MY_URL2));
                startService(intent);
            }

        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                sort = "popular";
                Intent intent1 = new Intent(this, MyService.class);
                intent1.setData(Uri.parse(MY_URL1));
                startService(intent1);
                Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                sort = "top_rated";
                Intent intent2 = new Intent(this, MyService.class);
                intent2.setData(Uri.parse(MY_URL2));
                startService(intent2);
                Toast.makeText(getApplicationContext(), item.getTitle(),Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
