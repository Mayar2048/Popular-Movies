package com.example.eng_mayar.popularmoviesapp.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eng_mayar.popularmoviesapp.R;
import com.example.eng_mayar.popularmoviesapp.adapters.ReviewsAdapter;
import com.example.eng_mayar.popularmoviesapp.models.Review;

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

import static com.example.eng_mayar.popularmoviesapp.fragments.DetailsFragment.reviewsListView;
import static com.example.eng_mayar.popularmoviesapp.fragments.DetailsFragment.setListViewHeightBasedOnItems;

public class FetchReviews extends AsyncTask<String, Void, ArrayList<Review>> {
    private final String LOG_TAG = FetchReviews.class.getSimpleName();
    private ReviewsAdapter reviewsAdapter;
    private Context mContext;
    private View view;

    public FetchReviews(Context context, ReviewsAdapter adapter, View rootView) {
        this.reviewsAdapter = adapter;
        this.mContext = context;
        this.view = rootView;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewsJsonStr = null;
        String movieId = params[0];

        try {
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews";
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
            reviewsJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Reviews Stream: " + reviewsJsonStr);
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
            return getReviewsDataFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        if (reviews != null) {
            super.onPostExecute(reviews);
            if (reviewsAdapter.getCount() > 0) {
                reviewsAdapter.clear();
            }
            for (int i = 0; i < reviews.size(); i++) {
                reviewsAdapter.add(reviews.get(i));
            }
            if(reviews.size() > 0){
                view.findViewById(R.id.reviews_textview).setVisibility(View.VISIBLE);
            }
            reviewsAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnItems(reviewsListView);
        } else {
            Toast.makeText(mContext, "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Review> getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
        final String REVIEW_RESULTS = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        ArrayList<Review> reviews = new ArrayList<Review>();

        JSONObject root = new JSONObject(reviewsJsonStr);

        JSONArray results = root.getJSONArray(REVIEW_RESULTS);

        for (int i = 0; i < results.length(); i++) {
            Review review = new Review();

            JSONObject reviewInfo = results.getJSONObject(i);

            review.setId(reviewInfo.getString(REVIEW_ID));

            review.setAuthor(reviewInfo.getString(REVIEW_AUTHOR));

            review.setContent(reviewInfo.getString(REVIEW_CONTENT));

            review.setUrl(reviewInfo.getString(REVIEW_URL));

            reviews.add(review);
        }
        return reviews;
    }
}
