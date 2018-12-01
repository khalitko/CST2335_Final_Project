package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.cst2335_finalproject.cst2335_final_project.R;


public class MovieActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "MovieActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Log.i(ACTIVITY_NAME, "In onCreate");

        Toolbar movieBar = findViewById(R.id.movieBar);
        setSupportActionBar(movieBar);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(true);

        View movieFrameLayout = findViewById(R.id.movieFrameLayout);
        MovieFragmentMain movieFragmentMain = new MovieFragmentMain();
        Bundle infoToPass = new Bundle();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        fTrans.replace(R.id.movieFrameLayout, movieFragmentMain)
            .commit();
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
                Log.d("Toolbar", "Action_One [Favourites] Selected");

                break;

            case R.id.action_two_home:
                Log.d("Toolbar", "Action_Two [Home] Selected");
                Intent resultIntent = new Intent( );
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;

            case R.id.action_three_help:
                Log.d("Toolbar", "Action_Three [Help] Selected");
                AlertDialog.Builder builderAboutMovies = new AlertDialog.Builder(MovieActivity.this);
                builderAboutMovies.setTitle(R.string.aboutDialogTitle)
                        .setMessage(R.string.aboutMessageString)
                        .setNegativeButton(R.string.BtnOkay, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            //THEN DO NOTHING//
                        }
                        });
                // Create the AlertDialog
                AlertDialog dialog = builderAboutMovies.create();
                dialog.show();
                break;
        }
        return true;
    }
}
