package com.example.cst2335_finalproject.cst2335_final_project.Food;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;

import com.example.cst2335_finalproject.cst2335_final_project.R;

/**
 * Activity for favorites list, loads fragment into the blank frame
 */
public class FoodFavorites extends AppCompatActivity {
    /**
     * On Create method, loads fragment into the blank frame
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_favorites);

        FoodFragment newFragment = new FoodFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ftrans = fm.beginTransaction();
        ftrans.replace(R.id.favorites,newFragment); //load a fragment into the framelayout
        ftrans.commit(); //actually load it
    }

}