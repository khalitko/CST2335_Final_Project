package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.cst2335_finalproject.cst2335_final_project.R;

public class MovieFragmentMain extends Fragment {

    MovieActivity parent = new MovieActivity();
    private Button searchMovieBtn, favouritesMovieBtn;
    private EditText enteredMovieTitle;

    public MovieFragmentMain(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_main, container, false);

        searchMovieBtn = screen.findViewById(R.id.queryMovieBtn);
        enteredMovieTitle = screen.findViewById(R.id.movieToQueryEt);
        favouritesMovieBtn = screen.findViewById(R.id.mainMovieFavButton);

        searchMovieBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).searchForAMovie(enteredMovieTitle.getText().toString());
        }));

        favouritesMovieBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).toMovieFavourites();
        }));

        return screen;
    }
}
