package com.example.cst2335_finalproject.cst2335_final_project.Food;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.cst2335_finalproject.cst2335_final_project.MainScreen;
import com.example.cst2335_finalproject.cst2335_final_project.R;



public class SearchTag extends AppCompatActivity {
    /**
     * Variable for holding data for list view
     */
    ArrayList<String> log = new ArrayList<>();
    /**
     * Variable for holding data for food list view
     */
    ArrayList<String> foodLog = new ArrayList<>();
    /**
     * Listview variables for displaying tag list and food list
     */
    ListView tagList;
    ListView foodList;
    /**
     * List view adapter variable
     */
    Adapter adapter;
    /**
     * Food list view adapter variable
     */
    FoodAdapter foodAdapter;
    /**
     * Database helper object variable
     */
    FoodDatabaseHelper dbHelp;
    /**
     * Cursor object  variable for loading and deleting data
     */
    Cursor cursor;
    /**
     * Database object variable
     */
    SQLiteDatabase db;
    /**
     * Class variables for view items
     */
    TextView details;
    TextView details2;
    TextView details3;
    TextView details4;

    /**
     * On create method that contains most of class functionality
     * @param savedInstanceState
     */

    ArrayList<HashMap<String, String>> tagItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tagItemList = new ArrayList<>();
        setContentView(R.layout.activity_food_tag_list);
        // setup view objects
        details = findViewById(R.id.details);
        details2 = findViewById(R.id.details2);
        details3 = findViewById(R.id.details3);
        details4 = findViewById(R.id.details4);
        tagList = findViewById(R.id.tagList);
        foodList = findViewById(R.id.foodList);
        Snackbar.make(findViewById(android.R.id.content), R.string.SearchTag, Snackbar.LENGTH_SHORT).show();
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar); //set up toolbar
        setSupportActionBar(toolbar);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(true);


        dbHelp = new FoodDatabaseHelper(this);
        db = dbHelp.getWritableDatabase();
        cursor = db.query(true, FoodDatabaseHelper.TABLE_NAME, new String[]{FoodDatabaseHelper.KEY_TAG}, "Tag not null", null, null, null, null, null);
        cursor.moveToFirst();
        //fills up tag list view
        for (int i = 0; i < cursor.getCount(); i++) {
            String t = cursor.getString(cursor.getColumnIndex(FoodDatabaseHelper.KEY_TAG));
            log.add(t);
            cursor.moveToNext();
        }
        adapter = new Adapter();
        tagList.setAdapter(adapter);

//        foods = db.rawQuery("select * from " + FoodDatabaseHelper.TABLE_NAME, null);
//        ListView favList= (ListView)view.findViewById(R.id.favList);
//
//
//        String[] columns = new String[] { FoodDatabaseHelper.KEY_LABEL, FoodDatabaseHelper.KEY_CALORIES, FoodDatabaseHelper.KEY_FAT, FoodDatabaseHelper.KEY_CARBS, FoodDatabaseHelper.KEY_TAG };
//        // THE XML DEFINED VIEWS WHICH THE DATA WILL BE BOUND TO
//        int[] to = new int[] { R.id.foodLabel, R.id.caloriesV, R.id.fatV, R.id.carbsV, R.id.tagV };
//        favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));
        //when item is clicked it displays the details of the tag
        tagList.setOnItemClickListener((parent, view, position, id) -> {
            String tag = adapter.getItem(position);
            getFoodList(tag);
            details.setText("Total Calories "+ Double.toString(totalCal(tag))+"kcal");
            details2.setText("Average Calories "+ Double.toString(avgCal(tag))+"kcal");
            details3.setText("Minimum Calories "+ Double.toString(minCal(tag))+"kcal");
            details4.setText("Maximum Calories "+ Double.toString(maxCal(tag))+"kcal");
        });
    }

    /**
     * Gets the list of foods with the same tag when a tag is selected
     * @param tag
     */
    public void getFoodList(String tag){
        foodLog.clear();
        cursor = db.query(false, FoodDatabaseHelper.TABLE_NAME, new String[]{FoodDatabaseHelper.KEY_LABEL}, "Tag like ?", new String[]{tag}, null, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String t = cursor.getString(cursor.getColumnIndex(FoodDatabaseHelper.KEY_LABEL));
            foodLog.add(t);
            cursor.moveToNext();
        }

//        ListAdapter adapter = new SimpleAdapter(SearchTag.this, tagItemList,
//                R.layout.food_info, new String[]{ "Label","Calories", "Fat", "Carbs", "Tag"},
//                new int[]{R.id.foodLabel, R.id.caloriesV, R.id.fatV, R.id.carbsV, R.id.tagV});
//        foodList.setAdapter(adapter);

        foodAdapter = new FoodAdapter();
        foodList.setAdapter(foodAdapter);
    }
    /**
     * Calculates total calories
     * @param tag
     * @return total
     */
    public double totalCal(String tag){
        cursor =db.rawQuery("SELECT SUM("+FoodDatabaseHelper.KEY_CALORIES+") as TotalCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        cursor.moveToFirst();
        double totalCal = cursor.getDouble(cursor.getColumnIndex("TotalCal"));
        return totalCal;

    }

    /**
     * Calculates average calories
     * @param tag
     * @return average
     */
    public double avgCal(String tag){
        cursor =db.rawQuery("SELECT AVG("+FoodDatabaseHelper.KEY_CALORIES+") as AvgCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        cursor.moveToFirst();
        double avgCal = cursor.getDouble(cursor.getColumnIndex("AvgCal"));
        return avgCal;
    }

    /**
     * Calculates minimum calories
     * @param tag
     * @return min
     */
    public double minCal(String tag){
        cursor =db.rawQuery("SELECT MIN("+FoodDatabaseHelper.KEY_CALORIES+") as MinCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        cursor.moveToFirst();
        double minCal = cursor.getDouble(cursor.getColumnIndex("MinCal"));
        return minCal;
    }

    /**
     * Calculates maximum calories
     * @param tag
     * @return max
     */
    public double maxCal(String tag){
        cursor =db.rawQuery("SELECT MAX("+FoodDatabaseHelper.KEY_CALORIES+") as MaxCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        cursor.moveToFirst();
        double maxCal = cursor.getDouble(cursor.getColumnIndex("MaxCal"));
        return maxCal;
    }
    /**
     * Sets up option menu in toolbar
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * On destroy method
     */
    @Override
    protected void onDestroy(){
        dbHelp.close();
        super.onDestroy();
    }
    /**
     * Sets up option menu for tool bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            Intent intent = new Intent(SearchTag.this, FoodFavorites.class);
            startActivity(intent);
            Snackbar.make(this.findViewById(android.R.id.content),R.string.favorites, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        } else if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
            Snackbar.make(this.findViewById(android.R.id.content),R.string.foodSearch, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        } else if (id == R.id.action_tag) {
            Intent intent = new Intent(this, SearchTag.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.search) {
            Intent intent = new Intent(this, FoodActivity.class);
            startActivity(intent);
            Snackbar.make(this.findViewById(android.R.id.content),R.string.foodSearch, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        else if (id == R.id.action_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchTag.this);
            builder.setMessage(getString(R.string.Author) + "\n" + "\n" + getString(R.string.HowTo)).setTitle(R.string.Help).setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            }).show();
            return true;
        }
        else if (id == R.id.action_settings) {
            Toast.makeText(SearchTag.this, "Settings clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Inner class for list view adapter
     */
    private class Adapter extends BaseAdapter {
        /**
         * Gets count
         * @return count
         */
        @Override
        public int getCount(){
            return  log.size();
        }

        /**
         * Returns the object at position
         * @param pos
         * @return String[]
         */
        @Override
        public String getItem(int pos){
            return  log.get(pos);
        }

        /**
         * Get item id at position
         * @param pos
         * @return pos
         */
        public long getItemId(int pos){
            return pos;
        }
        /**
         * Inflates the list view
         * @param pos
         * @param convertView
         * @param parent
         * @return view
         */
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = SearchTag.this.getLayoutInflater();
            View result = null;
            result = inflater.inflate(R.layout.food_info, null);
            TextView labelV = (TextView) result.findViewById(R.id.foodLabel);
            TextView infoV = (TextView) result.findViewById(R.id.caloriesV);
            String temp = getItem(pos);
            labelV.setText(temp);
            infoV.setVisibility(View.GONE);
            return result;

        }

    }
    /**
     * Inner class for food list view adapter
     */
    private class FoodAdapter extends BaseAdapter {
        /**
         * Gets count
         * @return count
         */
        @Override
        public int getCount(){
            return  foodLog.size();
        }

        /**
         * Returns the object at position
         * @param pos
         * @return String[]
         */
        @Override
        public String getItem(int pos){
            return  foodLog.get(pos);
        }

        /**
         * Get item id at position
         * @param pos
         * @return pos
         */
        public long getItemId(int pos){
            return pos;
        }
        /**
         * Inflates the list view
         * @param pos
         * @param convertView
         * @param parent
         * @return view
         */
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = SearchTag.this.getLayoutInflater();
            View result = null;
            result = inflater.inflate(R.layout.food_info, null);
            TextView labelV = (TextView) result.findViewById(R.id.foodLabel);
            TextView infoV = (TextView) result.findViewById(R.id.caloriesV);
            String temp = getItem(pos);
            labelV.setText(temp);
            infoV.setVisibility(View.GONE);
            return result;

        }

    }
}