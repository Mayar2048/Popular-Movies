package com.example.eng_mayar.popularmoviesapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.eng_mayar.popularmoviesapp.fragments.DetailsFragment;
import com.example.eng_mayar.popularmoviesapp.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.movie_details_container, new DetailsFragment(), "tag").commit();
        }
    }
}
