package com.example.eng_mayar.popularmoviesapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eng_mayar.popularmoviesapp.R;
import com.example.eng_mayar.popularmoviesapp.models.Review;

import java.util.ArrayList;

public class ReviewsAdapter extends BaseAdapter {
    private static LayoutInflater inflater;
    private ArrayList<Review> reviews;
    private Context mContext;

    public ReviewsAdapter(Context context) {
        this.mContext = context;
        this.reviews = new ArrayList<Review>();
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;
        if (convertView == null) {
            rootView = inflater.inflate(R.layout.review_item, parent, false);      // Not Recycled View
        } else {
            rootView = convertView;                                                 // Recycled View
        }

        TextView reviewAuthor = (TextView) rootView.findViewById(R.id.review_author);
        reviewAuthor.setText(reviews.get(position).getAuthor());

        TextView reviewContent = (TextView) rootView.findViewById(R.id.review_content);
        reviewContent.setEllipsize(TextUtils.TruncateAt.END);
        reviewContent.setText(reviews.get(position).getContent());
        return rootView;
    }

    public void add(Review review) {
        reviews.add(review);
    }

    public void clear() {
        this.reviews.clear();
    }
}
