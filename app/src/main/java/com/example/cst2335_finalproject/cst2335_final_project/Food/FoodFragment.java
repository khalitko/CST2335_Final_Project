package com.example.cst2335_finalproject.cst2335_final_project.Food;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.cst2335_finalproject.cst2335_final_project.MainScreen;
import com.example.cst2335_finalproject.cst2335_final_project.R;


public class FoodFragment extends Fragment {

    FoodDatabaseHelper dbHelper;
    SQLiteDatabase db;
    FoodActivity parent;
    Button delete;
    Toolbar toolbar;
    Button addTag;
    EditText editTagText;
    long tagID;
    Cursor foods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle infoToPass = getArguments();
//        super.onCreate(savedInstanceState);

        dbHelper = new FoodDatabaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        parent = new FoodActivity();
        View view = inflater.inflate(R.layout.fragment_food,container,false);
        addTag = view.findViewById(R.id.addTag);
        editTagText = view.findViewById(R.id.editListView);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar); //set up toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        if (this.getActivity().getActionBar() != null) this.getActivity().getActionBar().setDisplayShowTitleEnabled(false);
//        setHasOptionsMenu(false); //needed to make option menus to appear

        Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.favorites, Snackbar.LENGTH_SHORT).show();


        foods = db.rawQuery("select * from " + FoodDatabaseHelper.TABLE_NAME, null);
        ListView favList= (ListView)view.findViewById(R.id.favList);


        String[] columns = new String[] { FoodDatabaseHelper.KEY_LABEL, FoodDatabaseHelper.KEY_CALORIES, FoodDatabaseHelper.KEY_FAT, FoodDatabaseHelper.KEY_CARBS, FoodDatabaseHelper.KEY_TAG };
        // THE XML DEFINED VIEWS WHICH THE DATA WILL BE BOUND TO
        int[] to = new int[] { R.id.foodLabel, R.id.caloriesV, R.id.fatV, R.id.carbsV, R.id.tagV };
        favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));



        favList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.DeleteMenu).setTitle(R.string.Delete)
                        .setCancelable(true)

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which ) {
                                SQLiteDatabase db = dbHelper.getReadableDatabase();
                                db.delete(FoodDatabaseHelper.TABLE_NAME, FoodDatabaseHelper.KEY_ID + "=?", new String[] {Long.toString(id)});
                                Toast.makeText(getActivity(), "Deleted id:" + id, Toast.LENGTH_SHORT).show();
                                Cursor foods = db.rawQuery("select * from " + FoodDatabaseHelper.TABLE_NAME, null);
                                favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));
                            }

                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                dialogBuilder.show();
            }
        });

        favList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                String tagTemp = editTagText.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(FoodDatabaseHelper.KEY_TAG, tagTemp);
                db.update(FoodDatabaseHelper.TABLE_NAME, cv, FoodDatabaseHelper.KEY_ID + "=" + tagID, null);
                foods = db.rawQuery("SELECT " + FoodDatabaseHelper.KEY_ID + "," + FoodDatabaseHelper.KEY_LABEL + "," + FoodDatabaseHelper.KEY_CALORIES + "," + FoodDatabaseHelper.KEY_FAT + "," + FoodDatabaseHelper.KEY_TAG + " FROM " + FoodDatabaseHelper.TABLE_NAME, null);
                favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));
                editTagText.setText("");
                Toast toast = Toast.makeText(getActivity(), "tag added!", Toast.LENGTH_LONG); //success message
                toast.show();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),R.string.favoritesList, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        } else if (id == R.id.action_home) {
            Intent intent = new Intent(getActivity(), MainScreen.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_tag) {
            Intent intent = new Intent(getActivity(), SearchTag.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.search) {
            Intent intent = new Intent(getActivity(), FoodActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.Author) + "\n" + "\n" + getString(R.string.HowTo)).setTitle(R.string.Help).setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            }).show();
            return true;
        }
        else if (id == R.id.action_settings) {
            Toast.makeText(getActivity(), "Settings clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}