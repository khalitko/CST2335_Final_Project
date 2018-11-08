package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends Activity {
    protected static final String ACTIVITY_NAME = "NewsActivity";
    private ListView listView;
    private TextView article_title;
    public ArrayList<String> newsArticles;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        article_title = findViewById(R.id.article_title);

        newsArticles = new ArrayList<>();
        NewsQuery query = new NewsQuery();
        query.execute();

        newsAdapter = new NewsAdapter(this,newsArticles);
    }

    private class NewsQuery extends AsyncTask<String, Integer, String>{

        public String doInBackground(String ...args){
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
                            String name = xpp.getName();
                            if (name.equals("title")){
                                //does the same as next line
//                                if (xpp.next() == XmlPullParser.TEXT){
//                                    newsArticles.add(xpp.getText());
//                                }
                                newsArticles.add(xpp.nextText());
                            }
                            Log.i("read XML tag", name);
                            break;
                        case XmlPullParser.TEXT:
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

            listView = (ListView) findViewById(R.id.newsList);
            listView.setAdapter(newsAdapter);
        }
    }

    private class NewsAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> titleArray;

        public NewsAdapter(Activity context, ArrayList<String> titleArray){
            super(context, R.layout.news_title_row, titleArray);

            this.context = context;
            this.titleArray = titleArray;
        }

        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.news_title_row, null, true);

            TextView article_title = (TextView) rowView.findViewById(R.id.article_title);

            article_title.setText(titleArray.get(position));

            return rowView;
        }

    }
}
