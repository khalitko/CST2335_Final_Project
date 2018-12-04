package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cst2335_finalproject.cst2335_final_project.Food.FoodActivity;
import com.example.cst2335_finalproject.cst2335_final_project.Movies.MovieActivity;

public class MainScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }

    public void foodClick (View view) {
        startActivity(new Intent(this, FoodActivity.class));
    }

    public void newsButton(View view) {
        Intent intent = new Intent(MainScreen.this, NewsActivity.class);
        startActivity(intent);
    }

        /*
        public void newsButton (View view){

            startActivity(new Intent(this, NewsActivity.class));
        }
        */
        public void movieButton (View view){

            startActivity(new Intent(this, MovieActivity.class));
        }

        public void busButton (View view){

            startActivity(new Intent(this, BusActivity.class));
        }

}
