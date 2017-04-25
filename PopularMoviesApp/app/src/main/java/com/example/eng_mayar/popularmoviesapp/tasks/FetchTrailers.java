package com.example.eng_mayar.popularmoviesapp.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eng_mayar.popularmoviesapp.R;
import com.example.eng_mayar.popularmoviesapp.adapters.TrailersAdapter;
import com.example.eng_mayar.popularmoviesapp.models.Trailer;

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

import static com.example.eng_mayar.popularmoviesapp.fragments.DetailsFragment.setListViewHeightBasedOnItems;
import static com.example.eng_mayar.popularmoviesapp.fragments.DetailsFragment.trailersListView;

public class FetchTrailers extends AsyncTask<String, Void, ArrayList<Trailer>> {
    private final String LOG_TAG = FetchTrailers.class.getSimpleName();
    private TrailersAdapter trailersAdapter;
    private View view;
    private Context mContext;

    public FetchTrailers(Context context, TrailersAdapter adapter, View rootView) {
        this.trailersAdapter = adapter;
        this.mContext = context;
        this.view = rootView;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        if (trailers != null) {
            super.onPostExecute(trailers);
            if (trailersAdapter.getCount() > 0) {
                trailersAdapter.clear();
            }
            for (int i = 0; i < trailers.size(); i++) {
                trailersAdapter.add(trailers.get(i));
            }
            if(trailers.size() > 0){
                view.findViewById(R.id.trailers_textview).setVisibility(View.VISIBLE);
            }
            trailersAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnItems(trailersListView);
        } else {
            Toast.makeText(mContext, "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailersJsonStr = null;
        String movieId = params[0];

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId + "/videos";
            final String API_KEY_PARAM = "api_key";
            final String API_KEY = "6ff198d6852593f640f2f599573567eb";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon().appendQueryParameter(API_KEY_PARAM, API_KEY).build();

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
            trailersJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Trailers Stream: " + trailersJsonStr);
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
            return getTrailersDataFromJson(trailersJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<Trailer> getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
        final String TRAILER_RESULTS = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SITE = "site";
        final String TRAILER_SIZE = "size";
        final String TRAILER_TYPE = "type";


        ArrayList<Trailer> trailers = new ArrayList<Trailer>();

        // Root Element of JSON
        JSONObject root = new JSONObject(trailersJsonStr);

        JSONArray results = root.getJSONArray(TRAILER_RESULTS);

        for (int i = 0; i < results.length(); i++) {
            Trailer trailer = new Trailer();

            JSONObject trailerInfo = results.getJSONObject(i);

            trailer.setId(trailerInfo.getString(TRAILER_ID));

            trailer.setKey(trailerInfo.getString(TRAILER_KEY));

            trailer.setName(trailerInfo.getString(TRAILER_NAME));

            trailer.setSite(trailerInfo.getString(TRAILER_SITE));

            trailer.setSize(trailerInfo.getInt(TRAILER_SIZE));

            trailer.setType(trailerInfo.getString(TRAILER_TYPE));

            trailers.add(trailer);
        }
        return trailers;
    }
}
