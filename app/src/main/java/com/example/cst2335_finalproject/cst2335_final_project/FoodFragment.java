package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.Toolbar;


public class FoodFragment extends Fragment {

    FoodDatabaseHelper dbHelper;
    SQLiteDatabase db;
    FoodActivity parent;
    Button delete;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle infoToPass = getArguments();

        dbHelper = new FoodDatabaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        parent = new FoodActivity();
        View view = inflater.inflate(R.layout.fragment_food,container,false);

        Toolbar toolbar = view.findViewById(R.id.toolbar); //set up toolbar
        if (this.getActivity().getActionBar() != null) this.getActivity().getActionBar().setDisplayShowTitleEnabled(true);
        setHasOptionsMenu(true); //needed to make option menus to appear

        Cursor foods = db.rawQuery("select * from " + FoodDatabaseHelper.TABLE_NAME, null);
        ListView favList= (ListView)view.findViewById(R.id.favList);


        String[] columns = new String[] { FoodDatabaseHelper.KEY_LABEL, FoodDatabaseHelper.KEY_CALORIES };
        // THE XML DEFINED VIEWS WHICH THE DATA WILL BE BOUND TO
        int[] to = new int[] { R.id.foodLabel, R.id.nutrition };
        favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));


//        favList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                //if you want to delete
//                db.delete(FoodDatabaseHelper.TABLE_NAME, FoodDatabaseHelper.KEY_ID + "=?", new String[] {Long.toString(id)});
//                Toast.makeText(getActivity(), "Deleted id:" + id, Toast.LENGTH_SHORT);
//                Cursor foods = db.rawQuery("select * from " + FoodDatabaseHelper.TABLE_NAME, null);
//                favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));
//                return false;
//            }
//        });

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

        return view;
    }

}
