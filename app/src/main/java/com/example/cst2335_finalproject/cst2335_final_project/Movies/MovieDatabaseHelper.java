package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The movie database helper class is a helper function dedicated to keeping the majority of the code
 * for setting up and managing its expandability in one place, as well as keeping static references
 * to each of the database columns.
 *
 * @author Dylan McCarthy
 * @version 1.0
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Movies";
    private static final int VERSION_NUM = 1;
    private static final String ACTIVITY_NAME = "MovieDatabaseHelper";
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_RELEASED = "RELEASE_DATE";
    public static final String KEY_RATING = "RATING";
    public static final String KEY_RUNTIME = "RUNTIME";
    public static final String KEY_PLOT = "PLOT";
    public static final String KEY_STARRING = "STARRING";

    /**
     * default constructor for the MovieDatabaseHelper class.
     *
     * @param ctx   reference of the context passed from the MovieActivity.
     */
    public MovieDatabaseHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * onCreate Method runs when the constructor is first called by the activity, and is used to run
     * the create table sql command.
     *
     * @param  db  a reference to db object created by the constructor call.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(ACTIVITY_NAME, "Calling onCreate()");

        db.execSQL("CREATE TABLE " +DATABASE_NAME +"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_TITLE+" TEXT, "+KEY_RELEASED+" TEXT, "+KEY_RATING+" TEXT, "+KEY_RUNTIME+" TEXT, "+KEY_PLOT+" TEXT, "+KEY_STARRING+" TEXT);");
    }

    /**
     * onUpgrade is run when the database definition is changed/updated, and a new version number is
     * required, as well as a re-designation of the table.
     *
     * @param   db          a reference to db object created by the constructor call.
     * @param   oldVersion  dictates what the active version of the database we're referring to is.
     * @param   newVersion  dictates what the new version of the database we're referring to will be.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(ACTIVITY_NAME, "Calling onUpgrade(), oldVersion="+oldVersion+" newVersion="+newVersion);

        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
        onCreate(db);
    }
}
