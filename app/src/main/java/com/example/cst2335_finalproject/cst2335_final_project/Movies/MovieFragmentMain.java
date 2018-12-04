package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cst2335_finalproject.cst2335_final_project.R;

/**
 * The main fragment is the first screen the user sees, and contains the basis of the entire program
 * the query field. It is from within this fragment that the user can query for unique titles by
 * entering a string into the edit text and clicking the search button. It can movie to the display
 * and favourites fragments through the search and favourites buttons respectively.
 *
 * @author Dylan McCarthy
 * @version 1.0
 */
public class MovieFragmentMain extends Fragment {

    MovieActivity parent = new MovieActivity();
    private Button searchMovieBtn, favouritesMovieBtn;
    private EditText enteredMovieTitle;
    private TextView errorMessage;

    /**
     * default constructor for the MovieFragmentMain class.
     */
    public MovieFragmentMain(){}

    /**
     * onCreateView is similar to the on create on an activity accept it is called only when a new
     * view is declared, such as the fragment Main we're calling here. Inside, all of our variables
     * and layout objects are initialized.
     *
     * @param inflater              a reference to the activity inflater so the fragment can be
     *                              inflated.
     * @param container             a reference to the viewGroup passed from the activity.
     * @param  savedInstanceState   a reference to the bundle object passed into MovieActivity
     *                              from MainScreen.
     * @return screen               returns the View created and inflated here.
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_main, container, false);

        searchMovieBtn = screen.findViewById(R.id.queryMovieBtn);
        enteredMovieTitle = screen.findViewById(R.id.movieToQueryEt);
        favouritesMovieBtn = screen.findViewById(R.id.mainMovieFavButton);
        errorMessage = screen.findViewById(R.id.errorInQueryTxt);

        boolean error = bundle.getBoolean("Error");
        if (error) {errorMessage.setVisibility(View.VISIBLE);}
        else {errorMessage.setVisibility(View.INVISIBLE);}

        searchMovieBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).searchForAMovie(enteredMovieTitle.getText().toString());
        }));

        favouritesMovieBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).toMovieFavourites();
        }));

        return screen;
    }
}
