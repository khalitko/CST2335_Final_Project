package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.cst2335_finalproject.cst2335_final_project.R;

/**
 * The favourites fragment displays the listview full of all the movies placed into the favourites
 * database, and allows the user to easily view a movie again, or remove it from their list.
 * The user can also use this fragment to view statistics on the collection of their favourite films.
 *
 * @author Dylan McCarthy
 * @version 1.0
 */
public class MovieFragmentFavourite extends Fragment {

    private ListView favListView;
    private Button statisticsBtn;
    //for Database
    private Cursor c;
    //private MovieDatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private String[] movieDatabaseHeaders;
    private int[] movieLayoutComponents;
    SimpleCursorAdapter adapter;

    /**
     * default constructor for the MovieFragmentFavourite class.
     */
    public MovieFragmentFavourite(){}

    /**
     * onCreateView is similar to the on create on an activity accept it is called only when a new
     * view is declared, such as the fragment Favourite we're calling here. Inside, all of our variables
     * and layout objects are initialized.
     *
     * @param inflater              a reference to the activity inflater so the fragment can be
     *                              inflated.
     * @param container             a reference to the viewGroup passed from the activity.
     * @param  savedInstanceState   a reference to the bundle object passed into MovieActivity
     *                              from MainScreen.
     * @return screen               returns the View created and inflated here.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_favourites, container, false);

        //databaseHelper = ((MovieActivity)getActivity()).getDatabaseHelper();
        db = ((MovieActivity)getActivity()).getDb();

        statisticsBtn = screen.findViewById(R.id.movieStatisticsBtn);
        favListView = screen.findViewById(R.id.favouriteMovieList);
        movieDatabaseHeaders = new String[]{MovieDatabaseHelper.KEY_TITLE, MovieDatabaseHelper.KEY_RELEASED, MovieDatabaseHelper.KEY_RATING,
                MovieDatabaseHelper.KEY_RUNTIME};
        movieLayoutComponents = new int[]{R.id.movieFavTitle, R.id.movieFavReleased, R.id.movieFavRating, R.id.movieFavRuntime};
        loadListView();

        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                c = db.rawQuery("SELECT "+MovieDatabaseHelper.KEY_TITLE+" FROM "+ MovieDatabaseHelper.DATABASE_NAME+" WHERE "+MovieDatabaseHelper.KEY_ID+"=?", new String[]{Long.toString(id)});

                c.moveToFirst();
                final String movieTitle = c.getString(c.getColumnIndex(MovieDatabaseHelper.KEY_TITLE));

                builder.setTitle(movieTitle);
                // Add the buttons
                builder.setNeutralButton(R.string.BtnView, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        ((MovieActivity)getActivity()).searchForAMovie(movieTitle);
                    }
                });
                builder.setNegativeButton(R.string.BtnDelete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        db.delete(MovieDatabaseHelper.DATABASE_NAME, MovieDatabaseHelper.KEY_ID+"=?", new String[]{Long.toString(id)});
                        Snackbar.make(screen.findViewById(R.id.favouriteMovieList), movieTitle+" was removed from Database", Snackbar.LENGTH_SHORT).show();
                        loadListView();
                    }
                });
                builder.setPositiveButton(R.string.BtnCancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        // User cancelled the dialog //
                        // THEN DO NOTHING //
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        statisticsBtn.setOnClickListener((v -> {
            int shortestRun = 14401 //the longest movie ever is 14'400 min.
                    , longestRun = 0, averageRun = 0;
            String runtimeStringActive;
            int runtimeIntActive;
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            c = db.rawQuery("SELECT "+MovieDatabaseHelper.KEY_RUNTIME+" FROM "+ MovieDatabaseHelper.DATABASE_NAME, null);
            try {
                while (c.moveToNext()){
                    runtimeStringActive = c.getString(c.getColumnIndex(MovieDatabaseHelper.KEY_RUNTIME));
                    String justTime = runtimeStringActive.replaceAll(" min", "");
                    runtimeIntActive = Integer.parseInt(justTime);
                    averageRun += runtimeIntActive;
                    if (runtimeIntActive < shortestRun) {shortestRun = runtimeIntActive;}
                    if (runtimeIntActive > longestRun) {longestRun = runtimeIntActive;}
                }
            } finally {
                averageRun = averageRun / c.getCount();
                c.close();
            }

            builder.setTitle(R.string.movieStatsBtn);
            String message = getContext().getString(R.string.statsDisplay1)+
                    Integer.toString(shortestRun)+" min"+
                    getContext().getString(R.string.statsDisplay2)+
                    Integer.toString(longestRun)+" min"+
                    getContext().getString(R.string.statsDisplay3)+
                    Integer.toString(averageRun)+" min";
            builder.setMessage(message);
            builder.setNegativeButton(R.string.BtnOkay, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    // User cancelled the dialog //
                    // THEN DO NOTHING //
                }
            });
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }));

        return screen;
    }

    /**
     * This is a helper method to load the data we have saved into the database into the
     * favourites list view for the user to see. This is called onViewCreate, as well as after
     * an item is deleted from database.
     * */
    private void loadListView(){
        c = db.rawQuery("SELECT * FROM "+ MovieDatabaseHelper.DATABASE_NAME, null);

        adapter = new SimpleCursorAdapter((MovieActivity)getActivity(), R.layout.activity_movie_favourite_layout, c, movieDatabaseHeaders, movieLayoutComponents);
        favListView.setAdapter(adapter);
    }
}

