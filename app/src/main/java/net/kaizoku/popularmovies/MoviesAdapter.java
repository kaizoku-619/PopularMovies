package net.kaizoku.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import net.kaizoku.popularmovies.model.Movie;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{

    private static final String TAG = "MoviesAdapter";
    private List<Movie> mMovieList;
    private LayoutInflater mInflater;
    private Context mContext;

    public MoviesAdapter(Context context, List<Movie> mMovieList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMovieList = mMovieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/w300/" + movie.getPosterPath())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "you clicked on " + movie.getTitle(), Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(mContext, DetailedMovieActivity.class);
                intent.putExtra("movie", movie);
                Log.i(TAG, "onClick: movie sent to DetailedActivity = " + movie.toString());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mMovieList == null) ? 0 : mMovieList.size();
    }

    public void setmMovieList(List<Movie> movieList) {
        this.mMovieList.clear();
        this.mMovieList.addAll(movieList);
        // The adapter needs to know that the data has changed.
        // If we don't call this, app will crash.
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

    }

}
