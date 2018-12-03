package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.ListAdapter;

import com.example.cst2335_finalproject.cst2335_final_project.R;

import java.util.ArrayDeque;
import java.util.ArrayList;

import static com.example.cst2335_finalproject.cst2335_final_project.Movies.MovieActivity.ACTIVITY_NAME;

public class MovieFragmentFavourite extends Fragment {

    private ListView favListView;
    //for Database
    private Cursor c;
    //private MovieDatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private String[] movieDatabaseHeaders;
    private int[] movieLayoutComponents;
    SimpleCursorAdapter adapter;

    public MovieFragmentFavourite(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_favourites, container, false);

        //databaseHelper = ((MovieActivity)getActivity()).getDatabaseHelper();
        db = ((MovieActivity)getActivity()).getDb();

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

        return screen;
    }

    private void loadListView(){
        c = db.rawQuery("SELECT * FROM "+ MovieDatabaseHelper.DATABASE_NAME, null);

        adapter = new SimpleCursorAdapter((MovieActivity)getActivity(), R.layout.activity_movie_favourite_layout, c, movieDatabaseHeaders, movieLayoutComponents);
        favListView.setAdapter(adapter);
    }
}

