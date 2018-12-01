package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cst2335_finalproject.cst2335_final_project.R;

public class MovieFragmentDisplay extends Fragment {
    public MovieFragmentDisplay(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_display, container, false);

        return screen;
    }
}
