package com.example.eng_mayar.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_details, new DetailsFragment(), "tag").commit();
        }
    }
}
