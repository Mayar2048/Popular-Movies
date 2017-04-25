package com.example.eng_mayar.popularmoviesapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eng_mayar.popularmoviesapp.R;
import com.example.eng_mayar.popularmoviesapp.adapters.ReviewsAdapter;
import com.example.eng_mayar.popularmoviesapp.adapters.TrailersAdapter;
import com.example.eng_mayar.popularmoviesapp.contentprovider.FavouriteMovieContract;
import com.example.eng_mayar.popularmoviesapp.models.Movie;
import com.example.eng_mayar.popularmoviesapp.models.Review;
import com.example.eng_mayar.popularmoviesapp.models.Trailer;
import com.example.eng_mayar.popularmoviesapp.tasks.FetchReviews;
import com.example.eng_mayar.popularmoviesapp.tasks.FetchTrailers;

import java.io.IOException;
import java.net.URL;

public class DetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String DETAIL_KEY = "KEY";
    public static ListView trailersListView;
    public static ListView reviewsListView;
    private TrailersAdapter mTrailerAdapter;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;
    private Menu menu;
    private Movie movie;
    private View rootView;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        boolean flag = false;
        if (intent != null && intent.hasExtra("movie")) {
            Bundle data = intent.getExtras();
            this.movie = data.getParcelable("movie");
            flag = true;
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                this.movie = arguments.getParcelable(DetailsFragment.DETAIL_KEY);
                flag = true;
            }
        }
        if (flag) {
            setHasOptionsMenu(true);
            RetreiveMovieThumbnail movieThumbnail = new RetreiveMovieThumbnail();
            movieThumbnail.execute(movie);
            ScrollView sv = (ScrollView) rootView.findViewById(R.id.scrollView);
            sv.setScrollY(0);
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getTitle());
            ((TextView) rootView.findViewById(R.id.movie_vote_average)).setText(Double.toString(movie.getVoteAverage()));
            rootView.findViewById(R.id.movie_rating_bar).setVisibility(View.VISIBLE);
            ((RatingBar) rootView.findViewById(R.id.movie_rating_bar)).setRating((float) (movie.getVoteAverage() / 2.0));
            rootView.findViewById(R.id.release_date).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(movie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(movie.getOverview());

            this.fetchTrailers(rootView);
            this.fetchReviews(rootView);
        }
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.favourite_button, menu);
        if (movie != null) {
            updateFavouriteMenuState();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getActivity().getIntent();
        Bundle data;
        if (movie != null) {
            if (movie.isFavourite()) {
                movie.setFavourite(false);
                try {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_favourite_unpressed));
                } catch (IndexOutOfBoundsException e) {
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favourite_unpressed));
                }
                removeFavouriteMovie();
            } else {
                movie.setFavourite(true);
                try {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_favourite_pressed));
                } catch (IndexOutOfBoundsException e) {
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favourite_pressed));
                }
                addFavouriteMovie();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    // Fetch Movie's Reviews and Trailers (AND) Play Trailers.

    public void fetchTrailers(View rootView) {
        mTrailerAdapter = new TrailersAdapter(getContext());
        trailersListView = (ListView) rootView.findViewById(R.id.trailers_list_view);
        trailersListView.setAdapter(mTrailerAdapter);
        trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer trailer = (Trailer) mTrailerAdapter.getItem(position);
                playTrailer(trailer.getKey());
            }
        });
        FetchTrailers trailersTask = new FetchTrailers(getContext(), mTrailerAdapter, rootView);
        trailersTask.execute(movie.getId());
    }

    public void fetchReviews(View rootView) {
        final ReviewsAdapter mReviewAdapter = new ReviewsAdapter(getContext());
        reviewsListView = (ListView) rootView.findViewById(R.id.reviews_list_view);
        reviewsListView.setAdapter(mReviewAdapter);
        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.review_popup, null);
                popupWindow = new PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWindow.setOutsideTouchable(true);
                Review review = (Review) mReviewAdapter.getItem(position);
                ((TextView) container.findViewById(R.id.popup_content)).setText(review.getContent());
                (container.findViewById(R.id.close_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
        FetchReviews reviewsTask = new FetchReviews(getContext(), mReviewAdapter, rootView);
        reviewsTask.execute(movie.getId());
    }

    private void playTrailer(String trailerId) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerId)));
    }

    // Add/Remove Movie to/from Favourites (AND) update Favourite icon state.

    public void removeFavouriteMovie() {
        String URL = "content://com.example.eng_mayar.popularmoviesapp/movies/" + movie.getId();
        Uri uri = Uri.parse(URL);
        int count = getContext().getContentResolver().delete(uri, null, null);
        if (count == 1) {
            Toast.makeText(getContext(), movie.getTitle() + " is deleted from Favourites successfully", Toast.LENGTH_LONG).show();
        }
    }

    public void addFavouriteMovie() {
        ContentValues values = new ContentValues();
        values.put(FavouriteMovieContract.FavouriteMoviesTable._ID, movie.getId());
        values.put(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_TITLE, movie.getTitle());
        values.put(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_OVERVIEW, movie.getOverview());
        try {
            getContext().getContentResolver().insert(FavouriteMovieContract.CONTENT_URI, values);
            Toast.makeText(getContext(), movie.getTitle() + " is inserted into Favourites successfully", Toast.LENGTH_LONG).show();
        } catch (android.database.SQLException e) {
            Toast.makeText(getContext(), "This movie has been added to Favourites before!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateFavouriteMenuState() {
        String URL = "content://com.example.eng_mayar.popularmoviesapp/movies/" + movie.getId();
        Uri uri = Uri.parse(URL);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, FavouriteMovieContract.FavouriteMoviesTable._ID);
        if (cursor.moveToFirst()) {
            movie.setFavourite(true);
            try {
                menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_favourite_pressed));
            } catch (IndexOutOfBoundsException e) {
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favourite_pressed));
            }
        }
    }

    public static void setListViewHeightBasedOnItems(ListView listView) {
        BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
        if (adapter != null) {
            int numberOfItems = adapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = adapter.getView(itemPos, null, listView);
                item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Set list height.
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + 50;        // 50 is an extra height
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    class RetreiveMovieThumbnail extends AsyncTask<Movie, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Movie... params) {
            URL url = null;
            try {
                url = new URL(params[0].getPosterPath());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bmp;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_thumbnail);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
