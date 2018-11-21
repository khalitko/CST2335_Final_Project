package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.support.v4.view.accessibility.AccessibilityEventCompat.setAction;

public class MainScreen extends Activity {


    private CoordinatorLayout coordinatorLayout;

    private ImageButton SplashScreen_FoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }

        public void foodClick (View view){
            startActivity(new Intent(this, FoodActivity.class));

        }

        public void newsButton (View view){

            startActivity(new Intent(this, NewsActivity.class));
        }

        public void movieButton (View view){

            startActivity(new Intent(this, MovieActivity.class));
        }

        public void busButton (View view){

            startActivity(new Intent(this, BusActivity.class));
        }

}

