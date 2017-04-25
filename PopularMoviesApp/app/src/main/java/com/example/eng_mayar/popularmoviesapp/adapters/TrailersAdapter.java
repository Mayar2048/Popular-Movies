package com.example.eng_mayar.popularmoviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eng_mayar.popularmoviesapp.R;
import com.example.eng_mayar.popularmoviesapp.models.Trailer;

import java.util.ArrayList;

public class TrailersAdapter extends BaseAdapter {
    private static LayoutInflater inflater;
    private ArrayList<Trailer> trailers;
    private Context mContext;

    public TrailersAdapter(Context context) {
        this.mContext = context;
        this.trailers = new ArrayList<Trailer>();
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Object getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;
        if (convertView == null) {
            rootView = inflater.inflate(R.layout.trailer_item, parent, false);      // Not Recycled View
        } else {
            rootView = convertView;                                                 // Recycled View
        }
        TextView trailerName = (TextView) rootView.findViewById(R.id.trailer_name);
        trailerName.setText(trailers.get(position).getName());
        return rootView;
    }

    public void add(Trailer trailer) {
        trailers.add(trailer);
    }

    public void clear() {
        this.trailers.clear();
    }
}
