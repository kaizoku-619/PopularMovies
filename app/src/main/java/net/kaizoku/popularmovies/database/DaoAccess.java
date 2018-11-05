package net.kaizoku.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import net.kaizoku.popularmovies.database.model.Movie;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoAccess {

    @Insert(onConflict = REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM Movie")
    List<Movie> getMovies();

}