package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.cst2335_finalproject.cst2335_final_project.R;

/**
 * Movie activity is the only activity within the movies area of the app, it controls switching
 * between the 3 other fragments and the management of the Database Helper class, it also manages
 * the toolbar, which is never changing above the frame layout.
 *
 * @author Dylan McCarthy
 * @version 1.0
 */
public class MovieActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "MovieActivity";
    private boolean inHomeScreen;

    //for the database
    private MovieDatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    /**
     * onCreate Method runs when the activity is first created, registered with in are a handful of
     * initializers and onClickListeners for the various buttons of the toolbar, as well as the
     * database helpers.
     *
     * @param  savedInstanceState  a reference to the bundle object passed into MovieActivity from MainScreen.
     */
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

    /**
     * Helper method within where we inflate the menu object I created in movie_menu.
     *
     * @param  m    a reference to the menu object created by the activity.
     * @return      returns true to say the options menu was created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.movie_menu, m);
        return true;
    }

    /**
     * A helper method to handle the onClick on menu items. The method switches through each button
     * to figure it which one was pressed, then performs the appropriate action.
     *
     * @param  mi a reference to whichever MenuItem was touched by the user.
     * @return      returns true once the press action has been handled.
     */
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

    /**
     * This is a helper method to switch fragments out of the frame layout, this one specifically
     * calls Display, which will query for a movie using the passed string.
     *
     * @param  movieTitle this String contains the title of the movie that was entered into the edit
     *                    text in the main activity, so that It can be passed and used in the movie
     *                    api query in Display.
     */
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

    /**
     * This is a helper method to switch fragments out of the frame layout, this one specifically
     * calls Main, which brings us to the main page of the movie api, where you can enter a query
     * for a new movie.
     *
     * @param  error    error is used to check if the query returns a false movie, error will
     *                  normally be false, unless an false root tag showed up in the query from
     *                  Display.
     */
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

    /**
     * This is a helper method to switch fragments out of the frame layout, this one specifically
     * calls favourites, where the user can view movies they have saved to their device, as well
     * as view statistics on their favourite movies.
     * */
    public void toMovieFavourites(){
        inHomeScreen = false;

        MovieFragmentFavourite movieFragmentFavourite = new MovieFragmentFavourite();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTrans = fm.beginTransaction();
        fTrans.replace(R.id.movieFrameLayout, movieFragmentFavourite)
                .commit();
    }

    /**
     * @return  databaseHelper  returns the DataBaseHelper so that it may be used within the
     *                          fragments, where a new one cannot be declared.
     */
    public MovieDatabaseHelper getDatabaseHelper() {return databaseHelper;}
    /**
     * @return  db              returns the SQLiteDatabase so that it may be used within the
     *                          fragments, where a new one cannot be declared.
     */
    public SQLiteDatabase getDb() {return db;}


    /**
     * Overriding the onDestory so that we don't leave the db open, helping to conserve memory and
     * energy.
     * */
    @Override
    protected void onDestroy(){
        db.close();
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}
