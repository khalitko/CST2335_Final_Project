package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
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
    private boolean inHomeScreen;

    //for the database
    private MovieDatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Log.i(ACTIVITY_NAME, "In onCreate");

        Toolbar movieBar = findViewById(R.id.movieBar);
        setSupportActionBar(movieBar);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(true);

        //For database
        databaseHelper = new MovieDatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        toMovieMain(false);
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
            case R.id.action_one_home:
                Log.d("Toolbar", "Action_One [Home] Selected");
                if (inHomeScreen) {
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {  toMovieMain(false);  }

                break;

            case R.id.action_two_favourties:
                Log.d("Toolbar", "Action_Two [Favourites] Selected");
                toMovieFavourites();
                break;

            case R.id.action_three_help:
                Log.d("Toolbar", "Action_Three [Help] Selected");
                AlertDialog.Builder builderAboutMovies = new AlertDialog.Builder(MovieActivity.this);
                builderAboutMovies.setTitle(R.string.aboutDialogTitle)
                        .setMessage(R.string.aboutMessageString)
                        .setNegativeButton(R.string.BtnOkay, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog // THEN DO NOTHING //
                        }});
                // Create the AlertDialog
                AlertDialog dialog = builderAboutMovies.create();
                dialog.show();
                break;
        }
        return true;
    }

    public void searchForAMovie(String movieTitle){
        Bundle infoToPass = new Bundle();
        infoToPass.putString("Title", movieTitle);
        inHomeScreen = false;

        MovieFragmentDisplay movieFragmentDisplay = new MovieFragmentDisplay();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        movieFragmentDisplay.setArguments(infoToPass);
        fTrans.replace(R.id.movieFrameLayout, movieFragmentDisplay)
                .commit();
    }

    public void toMovieMain(Boolean error){
        MovieFragmentMain movieFragmentMain = new MovieFragmentMain();
        inHomeScreen = true;
        Bundle infoToPass = new Bundle();
        infoToPass.putBoolean("Error", error);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        movieFragmentMain.setArguments(infoToPass);
        fTrans.replace(R.id.movieFrameLayout, movieFragmentMain)
                .commit();
    }

    public void toMovieFavourites(){
        inHomeScreen = false;

        MovieFragmentFavourite movieFragmentFavourite = new MovieFragmentFavourite();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        fTrans.replace(R.id.movieFrameLayout, movieFragmentFavourite)
                .commit();
    }

    //Getters for the database helper objects.
    public MovieDatabaseHelper getDatabaseHelper() {return databaseHelper;}
    public SQLiteDatabase getDb() {return db;}

    @Override
    protected void onDestroy(){
        db.close();
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}
