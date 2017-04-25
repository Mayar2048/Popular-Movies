package com.example.eng_mayar.popularmoviesapp.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.eng_mayar.popularmoviesapp.adapters.ImageAdapter;
import com.example.eng_mayar.popularmoviesapp.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FetchMovies extends AsyncTask<String, Void, ArrayList<Movie>> {
    private final String LOG_TAG = FetchMovies.class.getSimpleName();
    private ImageAdapter imageAdapter;
    private Context mContext;

    public FetchMovies(Context context, ImageAdapter adapter) {
        this.imageAdapter = adapter;
        this.mContext = context;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;
        String sortingCriteria = params[0];

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + sortingCriteria;
            final String API_KEY_PARAM = "api_key";
            final String API_KEY = "6ff198d6852593f640f2f599573567eb";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Movie Stream: " + moviesJsonStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        if (movies != null) {
            super.onPostExecute(movies);
            if (imageAdapter.getCount() > 0) {
                imageAdapter.clear();
            }
            for (int i = 0; i < movies.size(); i++) {
                imageAdapter.add(movies.get(i));
            }
        } else {
            Toast.makeText(mContext, "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Movie> getMovieDataFromJson(String moviesStr) throws JSONException {
        final String MDB_RESULTS = "results";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_ADULT = "adult";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_POPULARITY = "popularity";
        final String MDB_VOTE_COUNT = "vote_count";
        final String MDB_VIDEO = "video";
        final String MDB_VOTE_AVERAGE = "vote_average";

        ArrayList<Movie> movies = new ArrayList<Movie>();

        // Root Element of JSON
        JSONObject root = new JSONObject(moviesStr);

        JSONArray results = root.getJSONArray(MDB_RESULTS);

        for (int i = 0; i < results.length(); i++) {
            Movie movie = new Movie();

            JSONObject movieInfo = results.getJSONObject(i);

            movie.setPosterPath(movieInfo.getString(MDB_POSTER_PATH));

            movie.setAdult(movieInfo.getBoolean(MDB_ADULT));

            movie.setOverview(movieInfo.getString(MDB_OVERVIEW));

            movie.setReleaseDate(movieInfo.getString(MDB_RELEASE_DATE));

            movie.setId(movieInfo.getString(MDB_ID));

            movie.setTitle(movieInfo.getString(MDB_TITLE));

            movie.setPopularity(movieInfo.getDouble(MDB_POPULARITY));

            movie.setVoteCount(movieInfo.getLong(MDB_VOTE_COUNT));

            movie.setVideo(movieInfo.getBoolean(MDB_VIDEO));

            movie.setVoteAverage(movieInfo.getDouble(MDB_VOTE_AVERAGE));

            movies.add(movie);
        }
        return movies;
    }
}
