package com.example.eng_mayar.popularmoviesapp;

/**
 * Created by Eng-Mayar on 02-Nov-16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private static LayoutInflater inflater;
    private ArrayList<Movie> movies;
    private Context mContext;
    private int mResource;

    public ImageAdapter(Context context, int movieResource) {
        this.mContext = context;
        this.mResource = movieResource;
        this.movies = new ArrayList<Movie>();
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView movieView;
        if (convertView == null) {
            movieView = (ImageView) inflater.inflate(R.layout.movie_item, parent, false);       // Not Recycled ImagView
        } else {
            movieView = (ImageView) convertView;                                                // Recycled ImageView
        }
        Picasso.with(mContext).load(movies.get(position).getPosterPath()).into(movieView);
        return movieView;
    }

    public void add(Movie movie) {
        movies.add(movie);
    }

    public void clear() {
        this.movies.clear();
    }
}
