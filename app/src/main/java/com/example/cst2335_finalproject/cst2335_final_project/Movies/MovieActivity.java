package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.cst2335_finalproject.cst2335_final_project.R;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar movieBar = findViewById(R.id.movieBar);
        setSupportActionBar(movieBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.movie_menu, m);
        return true;
    }
}
