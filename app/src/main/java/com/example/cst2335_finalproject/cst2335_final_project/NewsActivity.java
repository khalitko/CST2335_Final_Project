package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends Activity {
    protected static final String ACTIVITY_NAME = "NewsActivity";

    private ExpandableListView listView;
    private ExpandableListView savedListView;
    private TextView article_title;

    public ArrayList<String> newsTitles, newsUrl, newsDescription;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;

    public int counter;

    private TextView savedArticles;
    private TextView newsArticles;

    private GridView optionGrid;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        article_title = findViewById(R.id.article_title);


        newsTitles = new ArrayList<>();
        newsUrl = new ArrayList<>();
        newsDescription = new ArrayList<>();

        savedArticles = findViewById(R.id.saved_articles);
        newsArticles = findViewById(R.id.news_articles);

        NewsQuery query = new NewsQuery();
        query.execute();

        newsAdapter = new NewsAdapter(this,newsTitles,newsUrl,newsDescription);

        savedListView = findViewById(R.id.saved_list);
        //optionGrid = findViewById(R.id.option_grid);



        newsArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.INVISIBLE){
                    savedListView.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);

                    newsArticles.setBackgroundColor(Color.RED);
                    savedArticles.setBackgroundColor(Color.BLACK);
                } else {
                    listView.setVisibility(View.INVISIBLE);

                    newsArticles.setBackgroundColor(Color.BLACK);
                }
            }
        });

        savedArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedListView.getVisibility() == View.INVISIBLE){
                    listView.setVisibility(View.INVISIBLE);
                    savedListView.setVisibility(View.VISIBLE);

                    savedArticles.setBackgroundColor(Color.BLUE);
                    newsArticles.setBackgroundColor(Color.BLACK);
                } else {
                    savedListView.setVisibility(View.INVISIBLE);

                    savedArticles.setBackgroundColor(Color.BLACK);
                }
            }
        });


    }

    /**
     * https://stackoverflow.com/questions/27415449/xmlpullparser-parse-nested-tag
     */
    private class NewsQuery extends AsyncTask<String, Integer, String>{

        public String doInBackground(String ...args){
            String name = new String();
            try {
                URL url = new URL("https://www.cbc.ca/cmlink/rss-world");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                counter = 0;

                while (xpp.getEventType() != XmlResourceParser.END_DOCUMENT){
                    name = xpp.getName();
                    switch (xpp.getEventType()){
                        case XmlResourceParser.START_TAG:
                            if (name.equals("title")){
                                //does the same as next line
//                                if (xpp.next() == XmlPullParser.TEXT){
//                                    newsArticles.add(xpp.getText());
//                                }
                                newsTitles.add(xpp.nextText());
                            } else if (name.equals("link")){
                                newsUrl.add(xpp.nextText());
                            } else if (name.equals("description")){
//                                if (counter == 0){
//                                    counter++;
//                                } else {
//                                    //System.out.println(xpp.nextText());
//                                    String input = xpp.nextText();
//                                    newsDescription.add(nestedTag(input));
//                                }
                            }
                            Log.i("read XML tag", name);
                            break;
                        case XmlResourceParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    xpp.next();
                }
            } catch (Exception e){
                Log.i("Exception", e.getMessage());
            }
            return "";
        }

        private String nestedTag(String input) {
            System.out.println("First Step" + input);
            try{
                XmlPullParserFactory factory1 = XmlPullParserFactory.newInstance();
                factory1.setNamespaceAware(false);
                XmlPullParser xpp1 = factory1.newPullParser();
                xpp1.setInput(new StringReader(input));

                while (xpp1.getEventType() != XmlResourceParser.END_DOCUMENT){
                    String nestedName = xpp1.getName();
                    switch (xpp1.getEventType()){
                        case XmlResourceParser.START_TAG:
                            if (nestedName.equals("img")){
                                System.out.println(" We Made it ");
                            }
                            break;
                        case XmlResourceParser.TEXT:
                            break;
                        case XmlResourceParser.END_TAG:
                            break;
                    }
                }
                xpp1.next();
            } catch (Exception e){
                Log.i("Exception", e.getMessage());
            }
            return "Hello";
        }

        public void onProgressUpdate(Integer ...value){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String result){
            progressBar.setVisibility(View.INVISIBLE);

            listView = findViewById(R.id.newsList);

//            for (int i = 0; i < 2; i++){
//                newsTitles.remove(0);
//                newsUrl.remove(0);
//            }
//            newsDescription.remove(0);
            newsAdapter.notifyDataSetChanged();
            listView.setAdapter(newsAdapter);

            listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (groupPosition != previousGroup){
                        listView.collapseGroup(previousGroup);
                    }
                    previousGroup = groupPosition;
                }
            });

        }
    }

}
