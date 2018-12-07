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
import java.util.List;
import java.util.Map;

import com.example.cst2335_finalproject.cst2335_final_project.MainScreen;
import com.example.cst2335_finalproject.cst2335_final_project.R;



public class SearchTag extends AppCompatActivity {

    ArrayList<String> TagList = new ArrayList<>();

    ArrayList<String> FoodItemList = new ArrayList<>();

    ListView tagList;
    ListView foodList;

    CalorieStatistics calStat;

    FoodItems fooditems;

    FoodDatabaseHelper dbHelp;

    Cursor foodquery;

    SQLiteDatabase db;

    TextView calorieCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_tag_list);
        // setup view objects
        calorieCal = findViewById(R.id.calorieCal);
        tagList = findViewById(R.id.tagList);
        foodList = findViewById(R.id.foodList);
        Snackbar.make(findViewById(android.R.id.content), R.string.SearchTag, Snackbar.LENGTH_SHORT).show();
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar); //set up toolbar
        setSupportActionBar(toolbar);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(true);


        dbHelp = new FoodDatabaseHelper(this);
        db = dbHelp.getWritableDatabase();
        foodquery = db.query(true, FoodDatabaseHelper.TABLE_NAME, new String[]{FoodDatabaseHelper.KEY_TAG}, "Tag not null", null, null, null, null, null);
        foodquery.moveToFirst();
        //fills up tag list view
        for (int i = 0; i < foodquery.getCount(); i++) {
            String t = foodquery.getString(foodquery.getColumnIndex(FoodDatabaseHelper.KEY_TAG));
            TagList.add(t);
            foodquery.moveToNext();
        }
        calStat = new CalorieStatistics();
        tagList.setAdapter(calStat);

        tagList.setOnItemClickListener((parent, view, position, id) -> {
            String tag = calStat.getItem(position);
            getFoodList(tag);
            calorieCal.setText("Total Calories "+ Double.toString(totalCal(tag))
                    + "\n" + "Average Calories "+ Double.toString(avgCal(tag))
                    + "\n" + "Minimum Calories "+ Double.toString(minCal(tag))
                    + "\n" + "Maximum Calories "+ Double.toString(maxCal(tag)));
        });
    }


    public void getFoodList(String tag){
        FoodItemList.clear();
        foodquery = db.query(false, FoodDatabaseHelper.TABLE_NAME, new String[]{FoodDatabaseHelper.KEY_LABEL}, "Tag like ?", new String[]{tag}, null, null, null, null);
        foodquery.moveToFirst();
        for (int i = 0; i < foodquery.getCount(); i++) {
            String t = foodquery.getString(foodquery.getColumnIndex(FoodDatabaseHelper.KEY_LABEL));
            FoodItemList.add(t);
            foodquery.moveToNext();
        }

        fooditems = new FoodItems();
        foodList.setAdapter(fooditems);
    }

    public double totalCal(String tag){
        foodquery =db.rawQuery("SELECT SUM("+FoodDatabaseHelper.KEY_CALORIES+") as TotalCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        foodquery.moveToFirst();
        double totalCal = foodquery.getDouble(foodquery.getColumnIndex("TotalCal"));
        return totalCal;

    }

    public double avgCal(String tag){
        foodquery =db.rawQuery("SELECT AVG("+FoodDatabaseHelper.KEY_CALORIES+") as AvgCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        foodquery.moveToFirst();
        double avgCal = foodquery.getDouble(foodquery.getColumnIndex("AvgCal"));
        return avgCal;
    }

    public double minCal(String tag){
        foodquery =db.rawQuery("SELECT MIN("+FoodDatabaseHelper.KEY_CALORIES+") as MinCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        foodquery.moveToFirst();
        double minCal = foodquery.getDouble(foodquery.getColumnIndex("MinCal"));
        return minCal;
    }


    public double maxCal(String tag){
        foodquery =db.rawQuery("SELECT MAX("+FoodDatabaseHelper.KEY_CALORIES+") as MaxCal FROM "+FoodDatabaseHelper.TABLE_NAME+" WHERE " + FoodDatabaseHelper.KEY_TAG +" LIKE " +"'"+tag+"'",null);
        foodquery.moveToFirst();
        double maxCal = foodquery.getDouble(foodquery.getColumnIndex("MaxCal"));
        return maxCal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy(){
        dbHelp.close();
        super.onDestroy();
    }

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

    private class CalorieStatistics extends BaseAdapter {

        @Override
        public int getCount(){
            return  TagList.size();
        }

        @Override
        public String getItem(int pos){
            return  TagList.get(pos);
        }

        public long getItemId(int pos){
            return pos;
        }

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

    private class FoodItems extends BaseAdapter {

        @Override
        public int getCount(){
            return  FoodItemList.size();
        }


        @Override
        public String getItem(int pos){
            return  FoodItemList.get(pos);
        }


        public long getItemId(int pos){
            return pos;
        }

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