package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.cst2335_finalproject.cst2335_final_project.Food.FoodActivity;
import com.example.cst2335_finalproject.cst2335_final_project.MainScreen;
import com.example.cst2335_finalproject.cst2335_final_project.R;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar movieBar = findViewById(R.id.movieBar);
        setSupportActionBar(movieBar);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.movie_menu, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        int id = mi.getItemId();
        switch (id){
            case R.id.action_one_favourites:
                Log.d("Toolbar", "Action_One Selected");

                break;

            case R.id.action_two_home:
                Log.d("Toolbar", "Action_Two Selected");
                Intent resultIntent = new Intent( );
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;

            case R.id.action_three_help:
                Log.d("Toolbar", "Action_Three Selected");

                break;
        }
        return true;
    }
}
