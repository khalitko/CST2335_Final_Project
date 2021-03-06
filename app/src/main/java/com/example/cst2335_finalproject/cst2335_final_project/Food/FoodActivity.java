package com.example.cst2335_finalproject.cst2335_final_project.Food;


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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cst2335_finalproject.cst2335_final_project.MainScreen;
import com.example.cst2335_finalproject.cst2335_final_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class FoodActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "FoodActivity";

    FoodQuery query;

    Button searchItem;
    EditText itemText;
    ListView list;
    ProgressBar progress;
    Toolbar favorite;
    double calorieValue;
    String search, label, fatValue, carbValue;
    String[] tagValue;
    FoodDatabaseHelper dbHelper;
    SQLiteDatabase db;
    String str;
    ArrayList<String[]> foods = new ArrayList<>();
    ArrayList<HashMap<String, String>> foodItemList;
    TextView bf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        searchItem = (Button) findViewById(R.id.searchItem);
        itemText = (EditText) findViewById(R.id.itemText);
        list = (ListView) findViewById(R.id.list);
        progress = (ProgressBar)findViewById(R.id.progress);

        Snackbar.make(findViewById(android.R.id.content), R.string.foodSearch, Snackbar.LENGTH_SHORT).show();
        progress.setVisibility(View.VISIBLE);
        foodItemList = new ArrayList<>();

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

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FoodActivity.this);
//                dialogBuilder.setMessage("Select a tag and add to favourites");
                dialogBuilder.setTitle("Select a tag and add to favourites");
//                        .setCancelable(true);
                String[] tagValue = {"BreakFast", "Lunch", "Dinner"};

                dialogBuilder.setSingleChoiceItems(tagValue, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:
                                bf = (TextView) findViewById(R.id.tagV);
                                str = bf.getText().toString();
                                str = str.replace(tagValue[0], "");
                                str = str.replace(tagValue[1], "");
                                str = str.replace(tagValue[2], "");
                                str = str.replace("", tagValue[0]);
//                                bf.setText(str);
                                break;
                            case 1:
                                bf = (TextView) findViewById(R.id.tagV);
                                str = bf.getText().toString();
                                str = str.replace(tagValue[0], "");
                                str = str.replace(tagValue[1], "");
                                str = str.replace(tagValue[2], "");
                                str = str.replace("", tagValue[1]);
                                break;
                            case 2:
                                bf = (TextView) findViewById(R.id.tagV);
                                str = bf.getText().toString();
                                str = str.replace(tagValue[0], "");
                                str = str.replace(tagValue[1], "");
                                str = str.replace(tagValue[2], "");
                                str = str.replace("", tagValue[2]);
                                break;
                            default:
                                break;
                        }
                        bf.setText(str);
                    }
                });
                dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SQLiteDatabase db = dbHelper.getWritableDatabase();

                                HashMap<String, String> m = foodItemList.get(0);//it will get the first HashMap Stored in array list

                                String[] strArr = new String[m.size()];
                                int i = 0;
                                for (HashMap<String, String> hash : foodItemList) {

                                    for (String current : hash.values()) {
                                        strArr[i] = current;
                                        i++;
                                    }
                                }

                                ContentValues cValues = new ContentValues();
                                cValues.put(FoodDatabaseHelper.KEY_LABEL,strArr[0]);
                                cValues.put(FoodDatabaseHelper.KEY_CALORIES,calorieValue);
                                cValues.put(FoodDatabaseHelper.KEY_FAT,strArr[2]);
                                cValues.put(FoodDatabaseHelper.KEY_CARBS,strArr[3]);
                                cValues.put(FoodDatabaseHelper.KEY_TAG, str);
                                db.insert(FoodDatabaseHelper.TABLE_NAME,"NullColumnName",cValues);
                                Toast toast = Toast.makeText(getApplicationContext(),R.string.Saved, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
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
        } else if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_tag) {
            Intent intent = new Intent(this, SearchTag.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.search) {
            Snackbar.make(this.findViewById(android.R.id.content),R.string.foodSearch, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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
        protected void onPreExecute() {
            super.onPreExecute();
            Snackbar.make(FoodActivity.this.findViewById(android.R.id.content),R.string.jsonDL, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();


        }

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
                        String  label = foodObject.getString("label");
                        publishProgress(40);
                        JSONObject nutriObject = foodObject.getJSONObject("nutrients");
                        Log.i(ACTIVITY_NAME, nutriObject.toString());
                        publishProgress(60);
                        calorieValue = nutriObject.getDouble("ENERC_KCAL");
                        publishProgress(80);
                        String  fatValue = nutriObject.getString("FAT");
                        publishProgress(90);
                        String  carbValue = nutriObject.getString("CHOCDF");
                        publishProgress(100);

                        HashMap<String, String> food = new HashMap<>();
                        food.put("Label", label);
                        food.put("Calories", "Calories: " + calorieValue );
                        food.put("Fat", "Fat: " + fatValue + "g");
                        food.put("Carbs", "Carb: " + carbValue+ "g");


                        foodItemList.add(food);

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
            super.onPostExecute(s);
            if (jArray == null || jArray.isNull(0)) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.Error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
                View view = toast.getView();
                view.setBackgroundColor(Color.RED);
                toast.show();
            }else{
                ListAdapter adapter = new SimpleAdapter(FoodActivity.this, foodItemList,
                        R.layout.food_info, new String[]{ "Label", "Calories", "Fat", "Carbs", "Tag"},
                        new int[]{R.id.foodLabel, R.id.caloriesV, R.id.fatV, R.id.carbsV, R.id.tagV});
                list.setAdapter(adapter);
            }
            progress.setVisibility(View.INVISIBLE);
        }

    }
}
