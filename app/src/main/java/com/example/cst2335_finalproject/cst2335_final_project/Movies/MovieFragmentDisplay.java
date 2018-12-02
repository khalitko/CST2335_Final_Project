package com.example.cst2335_finalproject.cst2335_final_project.Movies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cst2335_finalproject.cst2335_final_project.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MovieFragmentDisplay extends Fragment {

    private ProgressBar progressBar;

    private ImageView posterDisplay;
    private TextView titleDisplay, releaseDisplay, ratingDisplay, runtimeDisplay, plotDisplay, starringDisplay;

    private static String movieURLBase = "http://www.omdbapi.com/?apikey=61355fe7&r=xml&t=";
    private String movieURL, fetchURL;
    private URL movieWebsite, posterURL;
    private HttpURLConnection movieConnection, moviePosterConnnection;

    public MovieFragmentDisplay(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.activity_movie_fragment_display, container, false);

        progressBar = screen.findViewById(R.id.movieDisplayBar);

        posterDisplay = screen.findViewById(R.id.movieDisplayPoster);
        titleDisplay = screen.findViewById(R.id.movieDisplayTitle);
        releaseDisplay = screen.findViewById(R.id.movieDisplayReleaseDate);
        ratingDisplay = screen.findViewById(R.id.movieDisplayRating);
        runtimeDisplay = screen.findViewById(R.id.movieDisplayRuntime);
        plotDisplay = screen.findViewById(R.id.movieDisplayPlot);
        starringDisplay = screen.findViewById(R.id.movieDisplayStarring);

        progressBar.setVisibility(View.VISIBLE);

        if (bundle != null) {
            movieURL = bundle.getString("Title", "UTF-8");
        }

        try {
            movieWebsite = new URL(movieURLBase + URLEncoder.encode(movieURL));
        } catch (Exception e) {
            e.printStackTrace();
        }


        MovieQuery query = new MovieQuery();
        query.execute();

        return screen;
    }

    public class MovieQuery extends AsyncTask<String, Integer, String> {

        private String movieTitle, movieRelease, movieRating, movieRuntime, moviePlot, movieStarring, posterIcon;
        private Bitmap moviePoster;
        private int responseCode;

        @Override
        protected String doInBackground(String... args) {
            try {
                //FIRST CONNECT TO SERVER.
                    //movieURL = URLEncoder.encode(bundle.getString("Title"), "UTF-8");
                movieWebsite = new URL(movieURLBase + URLEncoder.encode(movieURL));
                movieConnection = (HttpURLConnection) movieWebsite.openConnection();
                InputStream response = movieConnection.getInputStream();

                //READ THE XML
                XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
                pullParserFactory.setNamespaceAware(false);
                XmlPullParser xpp = pullParserFactory.newPullParser();
                xpp.setInput(response, "UTF-8");

                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            String name = xpp.getName();
                            if (name.equals("movie")) {
                                movieTitle = xpp.getAttributeValue(null, "title");
                                onProgressUpdate(10);
                                movieRelease = xpp.getAttributeValue(null, "released");
                                onProgressUpdate(20);
                                movieRating = xpp.getAttributeValue(null, "rated");
                                onProgressUpdate(30);
                                movieRuntime = xpp.getAttributeValue(null, "runtime");
                                onProgressUpdate(40);
                                moviePlot = xpp.getAttributeValue(null, "plot");
                                onProgressUpdate(50);
                                movieStarring = xpp.getAttributeValue(null, "actors");
                                onProgressUpdate(60);

                                posterIcon = xpp.getAttributeValue(null, "poster");
                                onProgressUpdate(70);
                            }

                            Log.i("read XML tag:", name);
                            break;

                        case XmlPullParser.TEXT:
                            break;
                    }
                    xpp.next();
                }
                //Build the URL
                URLEncoder.encode(posterIcon, "UTF-8");
                posterURL = new URL(posterIcon);
                moviePosterConnnection = (HttpURLConnection) posterURL.openConnection();

                //FETCH THE PICTURE
                responseCode = moviePosterConnnection.getResponseCode();
                if (responseCode == 200) {
                    moviePoster = BitmapFactory.decodeStream(moviePosterConnnection.getInputStream());
                    onProgressUpdate(90);
                }
                String imageFile = moviePoster + "";
                if (fileExistence(imageFile))
                {
                    FileInputStream fis = null;
                    try {
                        fis = getContext().openFileInput(imageFile);
                    } catch (FileNotFoundException e) {e.printStackTrace();}
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                } else {
                    FileOutputStream outputStream = getContext().openFileOutput(imageFile, Context.MODE_PRIVATE);
                    moviePoster.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
                onProgressUpdate(100);

            } catch (Exception e){
                Log.i("Exception", e.getMessage());
            }
            return "";
        }

        private boolean fileExistence(String fileName){
            File file = getContext().getFileStreamPath(fileName);
            return file.exists();
        }

        @Override
        protected void onProgressUpdate(Integer... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result!=null) {
                String concat;
                concat = getString(R.string.movieDisplayTitle) + movieTitle;
                    titleDisplay.setText(concat);
                concat = getString(R.string.movieDisplayRelease) + movieRelease;
                    releaseDisplay.setText(concat);
                concat = getString(R.string.movieDisplayRating) + movieRating;
                    ratingDisplay.setText(concat);
                concat = getString(R.string.movieDisplayRuntime) + movieRuntime;
                    runtimeDisplay.setText(concat);
                concat = getString(R.string.movieDisplayPlot) + moviePlot;
                    plotDisplay.setText(concat);
                concat = getString(R.string.movieDisplayStarring) + movieStarring;
                    starringDisplay.setText(concat);

                posterDisplay.setImageBitmap(moviePoster);

                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
