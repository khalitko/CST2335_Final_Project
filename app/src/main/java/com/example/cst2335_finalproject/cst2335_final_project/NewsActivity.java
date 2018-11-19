package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends Activity {
    protected static final String ACTIVITY_NAME = "NewsActivity";
    private ExpandableListView listView;
    private TextView article_title;
    public ArrayList<String> newsTitles, newsUrl, newsDescription;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        article_title = findViewById(R.id.article_title);


        newsTitles = new ArrayList<>();
        newsUrl = new ArrayList<>();
        newsDescription = new ArrayList<>();


        NewsQuery query = new NewsQuery();
        query.execute();

        newsAdapter = new NewsAdapter(this,newsTitles,newsUrl,newsDescription);
    }

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

                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT){
                    switch (xpp.getEventType()){
                        case XmlPullParser.START_TAG:
                            name = xpp.getName();
                            if (name.equals("title")){
                                //does the same as next line
//                                if (xpp.next() == XmlPullParser.TEXT){
//                                    newsArticles.add(xpp.getText());
//                                }
                                newsTitles.add(xpp.nextText());
                            } else if (name.equals("link")){
                                newsUrl.add(xpp.nextText());
                            } else if (name.equals("description")){
                                newsDescription.add(xpp.getAttributeValue(null,"title"));
                            }
                            Log.i("read XML tag", name);
                            break;
                        case XmlPullParser.TEXT:
                            System.out.println(name);
                            if (name.equals("description")){
                                System.out.println("Made it!!!");
                            }
                            break;
                    }
                    xpp.next();
                }
            } catch (Exception e){
                Log.i("Exception", e.getMessage());
            }
            return "";
        }

        public void onProgressUpdate(Integer ...value){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String result){
            progressBar.setVisibility(View.INVISIBLE);

            listView = findViewById(R.id.newsList);

            for (int i = 0; i < 2; i++){
                newsTitles.remove(0);
                //newsDescription.remove(0);
                newsUrl.remove(0);
            }
            System.out.println(newsDescription.get(0));
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
