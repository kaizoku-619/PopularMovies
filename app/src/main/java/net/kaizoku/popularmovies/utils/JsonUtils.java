package net.kaizoku.popularmovies.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.kaizoku.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static ArrayList<Movie> parseMovieJson(String json) throws JSONException {

        JSONObject mainObject = new JSONObject(json);
        JSONArray resultsArray = mainObject.getJSONArray("results");

        ArrayList<Movie> movies;

        movies = new Gson().fromJson(
                resultsArray.toString(),
                new TypeToken<List<Movie>>(){}.getType()
        );

        return movies;
    }
}
