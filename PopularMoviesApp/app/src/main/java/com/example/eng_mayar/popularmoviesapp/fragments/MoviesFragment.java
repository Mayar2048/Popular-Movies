package com.example.eng_mayar.popularmoviesapp.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.example.eng_mayar.popularmoviesapp.R;
import com.example.eng_mayar.popularmoviesapp.adapters.FavouriteMovieAdapter;
import com.example.eng_mayar.popularmoviesapp.adapters.ImageAdapter;
import com.example.eng_mayar.popularmoviesapp.contentprovider.FavouriteMovieContract;
import com.example.eng_mayar.popularmoviesapp.models.Movie;
import com.example.eng_mayar.popularmoviesapp.tasks.FetchMovies;

public class MoviesFragment extends Fragment implements ActionBar.OnNavigationListener {
    private ImageAdapter imageAdapter;

    private String sortingCriteria;
    private String sortingSpinner[] = {"popular", "top_rated"};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GridView gridview;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MoviesFragment() {
        this.sortingCriteria = sortingSpinner[0];
    }

    public static MoviesFragment newInstance(String param1, String param2) {
        MoviesFragment fragment = new MoviesFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        imageAdapter = new ImageAdapter(getActivity(), R.id.movie_item);
        gridview = (GridView) rootView.findViewById(R.id.movies_grid);
        sortingCriteria = sortingSpinner[0];
        updateMovies();
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    showAllfavouriteMovies();
                } else {
                    sortingCriteria = sortingSpinner[position];
                    updateMovies();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sorting_criteria, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void updateMovies() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) imageAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });
        gridview.setAdapter(imageAdapter);
        FetchMovies task = new FetchMovies(getContext(), imageAdapter);
        task.execute(sortingCriteria);
    }

    public void showAllfavouriteMovies() {
        String URL = "content://com.example.eng_mayar.popularmoviesapp/movies";
        Uri uri = Uri.parse(URL);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, FavouriteMovieContract.FavouriteMoviesTable._ID);
        FavouriteMovieAdapter favouriteMovieAdapter = new FavouriteMovieAdapter(getActivity(), cursor);
        gridview.setAdapter(favouriteMovieAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor subCursor = (Cursor) parent.getItemAtPosition(position);
                if (subCursor != null) {
                    Movie movie = new Movie();
                    movie.setFavourite(true);
                    movie.setId(subCursor.getString(subCursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable._ID)));
                    movie.setPosterPath(subCursor.getString(subCursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_POSTER_PATH)));
                    movie.setTitle(subCursor.getString(subCursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_TITLE)));
                    movie.setVoteAverage(subCursor.getDouble(subCursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_VOTE_AVERAGE)));
                    movie.setReleaseDate(subCursor.getString(subCursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_RELEASE_DATE)));
                    movie.setOverview(subCursor.getString(subCursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_OVERVIEW)));
                    ((Callback) getActivity()).onItemSelected(movie);
                }
            }
        });
    }

    public interface Callback {
        public void onItemSelected(Movie movie);
    }
}
