package net.kaizoku.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import net.kaizoku.popularmovies.database.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase INSTANCE;

    public static MovieDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDatabase.class,
                    "movies_db")
                    .build();
        }
        return INSTANCE;
    }

    public abstract DaoAccess daoAccess();
}
