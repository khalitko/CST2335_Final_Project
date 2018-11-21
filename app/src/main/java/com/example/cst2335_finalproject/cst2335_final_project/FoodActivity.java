package com.example.cst2335_finalproject.cst2335_final_project;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "FoodActivity";

    FoodQuery query;

    Button searchItem;
    EditText itemText;
    ListView list;
    ProgressBar progress;
    Toolbar favorite;
    String search, label, calorieValue, fatValue, carbValue;
    FoodDatabaseHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<String[]> log = new ArrayList<>();

    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        searchItem = findViewById(R.id.searchItem);
        itemText = findViewById(R.id.itemText);
        list = findViewById(R.id.list);
        progress = findViewById(R.id.progress);

        Snackbar.make(findViewById(android.R.id.content), "Food Search", Snackbar.LENGTH_SHORT).show();
        progress.setVisibility(View.VISIBLE);

        adapter = new Adapter();
        list.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar); //set up toolbar
        setSupportActionBar(toolbar);
        if (getActionBar() != null) getActionBar().setDisplayShowTitleEnabled(true);

        dbHelper = new FoodDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        searchItem.setOnClickListener(click->{
            search = itemText.getText().toString();
            search = search.replace(" ","%20");
            FoodQuery querry = new FoodQuery();
            querry.execute(); // call http request
            progress.setVisibility(View.VISIBLE);
            itemText.setText("");
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FoodActivity.this)
                        .setMessage("Do you want to add this to favourites").setTitle("Save")
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                String[] current = log.get(position);
                                ContentValues cValues = new ContentValues();
                                cValues.put(FoodDatabaseHelper.KEY_LABEL,current[0]);
                                cValues.put(FoodDatabaseHelper.KEY_CALORIES,current[1]);
                                cValues.put(FoodDatabaseHelper.KEY_FAT,current[2]);
                                cValues.put(FoodDatabaseHelper.KEY_CARBS,current[3]);
                                db.insert(FoodDatabaseHelper.TABLE_NAME,"NullColumnName",cValues);
                                Toast toast = Toast.makeText(getApplicationContext(),R.string.Saved, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                dialogBuilder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            Intent intent = new Intent(FoodActivity.this, FoodFavorites.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivity.this);
            builder.setMessage(getString(R.string.Author) + "\n" + "\n" + getString(R.string.HowTo)).setTitle(R.string.Help).setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            }).show();
            return true;
        }
        else if (id == R.id.action_settings) {
            Toast.makeText(FoodActivity.this, "Settings clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FoodQuery extends AsyncTask<String, Integer, String> {

        JSONArray jArray;

        @Override
        protected String doInBackground(String... strings) {


            try {
                URL url = new URL("https://api.edamam.com/api/food-database/parser?app_id=e5bc806d&app_key=5f7521ffeefe491b936cea6271e13d3d&ingr=" + search);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder jsonResults = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonResults.append(line + "\n");
                }
                String result = jsonResults.toString();

                JSONObject jsonObj = new JSONObject(result);
                publishProgress(20);

                jArray = jsonObj.getJSONArray("parsed");

                for (int index = 0; index < jArray.length(); index++)
                    try {
                        JSONObject indexObject = jArray.getJSONObject(index);
                        JSONObject foodObject = indexObject.getJSONObject("food");
                        // Pulling items from the array
                        label = foodObject.getString("label");
                        publishProgress(40);
                        JSONObject nutriObject = foodObject.getJSONObject("nutrients");
                        Log.i(ACTIVITY_NAME, nutriObject.toString());
                        publishProgress(60);
                        calorieValue = nutriObject.getString("ENERC_KCAL");
                        publishProgress(80);
                        fatValue = nutriObject.getString("FAT");
                        publishProgress(90);
                        carbValue = nutriObject.getString("CHOCDF");
                        publishProgress(100);
                    } catch (JSONException e) {
                        // Oops
                    }
            }catch (Exception e)
            {
                Log.i("Exception", e.getMessage());
            }
            return "done";
        }


        @Override
        public void onProgressUpdate(Integer ... args){
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(args[0]);
        }

        @Override
        public void onPostExecute(String s){
            if (jArray == null || jArray.isNull(0)) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.Error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                View view = toast.getView();
                view.setBackgroundColor(Color.RED);
                toast.show();
            }else{
                String[] temp = new String[]{label,calorieValue,fatValue, carbValue};
                log.add(temp);
                adapter.notifyDataSetChanged();
            }
            progress.setVisibility(View.INVISIBLE);
        }

    }
    private class Adapter extends BaseAdapter {
        @Override
        public int getCount(){
            return  log.size();
        }
        @Override
        public String[] getItem(int pos){
            return  log.get(pos);
        }
        @Override
        public long getItemId(int pos){
            return pos;
        }
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = FoodActivity.this.getLayoutInflater();
            View result = null;
            result = inflater.inflate(R.layout.food_info, null);
            TextView foodV = result.findViewById(R.id.foodTitle);
            TextView nutriV = result.findViewById(R.id.nutrition);
            String[] temp = getItem(pos);
            foodV.setText(temp[0]);

            nutriV.setText("Calories: "+ temp[1] + " kcal Fat: " + temp[2] + "g Carb: " + temp[3] + "g");
            return result;

        }
    }
}
