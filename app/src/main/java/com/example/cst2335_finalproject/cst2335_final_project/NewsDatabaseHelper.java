package com.example.cst2335_finalproject.cst2335_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Creates a database to store values that the user favorites.
 * @author Umber Setia
 */
public class NewsDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database information
     */
    private final static String DATABASE_NAME = "SavedNews.db";
    private final static int VERSION_NUM = 3;
    /**
     * Table name followed by column titles
     */
    public final static String TABLE_NAME = "SavedNews";
    public final static String KEY_ID = "ID"; //Column 0
    public final static String KEY_NEWS_TITLE = "Title"; //Column 1
    public final static String KEY_NEWS_URL = "Url"; //Column 2
    public final static String KEY_NEWS_IMAGE = "Image"; //Column 3
    public final static String KEY_NEWS_DESCRIPTION = "Description"; //Column 4
    public final static String KEY_NEWS_DATE = "Date"; //Column 5

    private SQLiteDatabase db;

    public NewsDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * onCreate for the NewsDatabaseHelper.
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NEWS_TITLE + " String,"
                + KEY_NEWS_URL + " String,"
                + KEY_NEWS_IMAGE + " Blob,"
                + KEY_NEWS_DESCRIPTION + " String,"
                + KEY_NEWS_DATE + " String );"
        );
        Log.i("NewsDatabaseHelper","Calling onCreate");
    }

    /**
     * Called when the database has been upgraded.
     * @param db The database
     * @param oldVersion The old version of the database
     * @param newVersion The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.i("ChatDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }

    /**
     * Called when the database has been downgraded
     * @param db The database
     * @param oldVersion The previous version of the database
     * @param newVersion The new version of the database
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        Log.i("ChatDatabaseHelper", "Calling onDowngrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
        onCreate(db);
    }

    /**
     * https://www.youtube.com/watch?v=T0ClYrJukPA
     * https://stackoverflow.com/questions/6341776/how-to-save-bitmap-in-database
     * Inserts information into the database.
     * @param title - Title of the article
     * @param url - URL to reach the article
     * @param image - Image related to the article
     * @param description - Description of the article
     * @param date - Date of publication
     * @return true if information was stored
     */
    public boolean databaseInsert(String title, String url, Bitmap image, String description, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();

        cValues.put(KEY_NEWS_TITLE, title);
        cValues.put(KEY_NEWS_URL, url);

        Bitmap bitmap = image;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bArray = bos.toByteArray();

        cValues.put(KEY_NEWS_IMAGE, bArray);
        cValues.put(KEY_NEWS_DESCRIPTION, description);
        cValues.put(KEY_NEWS_DATE, date);

        long result = db.insert(TABLE_NAME,null,cValues);
        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    /**
     * https://stackoverflow.com/questions/20415309/android-sqlite-how-to-check-if-a-record-exists
     * Checks if the user is trying to save an article that has already been saved.
     * @return true if article has already been saved
     */
    public boolean articleExists(String title){
        SQLiteDatabase dbExists = this.getReadableDatabase();

        Cursor c = dbExists.rawQuery("SELECT * FROM SavedNews WHERE Title = ?",new String[] {title});
        if (c.getCount() <= 0){
            c.close();
            return false;
        }
        c.close();
        return true;
    }

    /**
     * Gets the contents of the database as a cursor object.
     * @return content of the database
     */
    public Cursor getTableData(){
        db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM SavedNews", null);
        return result;
    }

    /**
     * Deleted row id of the database.
     * @param id - Primary key row
     * @return delete value
     */
    public boolean deleteRow(String title){
        db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_NEWS_TITLE + "=?", new String[]{title}) > 0;
    }
}
