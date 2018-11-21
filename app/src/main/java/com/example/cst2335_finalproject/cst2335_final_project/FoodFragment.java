package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toolbar;


public class FoodFragment extends Fragment {

    FoodDatabaseHelper dbHelper;
    SQLiteDatabase db;
    FoodActivity parent;

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
        int[] to = new int[] { R.id.foodTitle, R.id.nutrition };
        favList.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.food_info, foods, columns, to));

        return view;
    }

}
