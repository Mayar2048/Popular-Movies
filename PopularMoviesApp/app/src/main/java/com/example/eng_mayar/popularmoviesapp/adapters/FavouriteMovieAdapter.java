package com.example.eng_mayar.popularmoviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.eng_mayar.popularmoviesapp.contentprovider.FavouriteMovieContract;
import com.example.eng_mayar.popularmoviesapp.R;
import com.squareup.picasso.Picasso;

public class FavouriteMovieAdapter extends CursorAdapter {
    public FavouriteMovieAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView movieView = (ImageView) view.findViewById(R.id.movie_item);
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(FavouriteMovieContract.FavouriteMoviesTable.COLUMN_POSTER_PATH))).into(movieView);
    }
}
