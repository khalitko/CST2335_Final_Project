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
    public ArrayList<String> newsTitles;
    public ArrayList<String> newsContent;
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
        newsContent = new ArrayList<>();

        newsAdapter = new NewsAdapter(this,newsTitles,newsContent);

        NewsQuery query = new NewsQuery();
        query.execute();


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
                                newsTitles.add(xpp.nextText());
                            } else if (name.equals("link")){
                                newsContent.add(xpp.nextText());
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

            listView = findViewById(R.id.newsList);
            listView.setAdapter(newsAdapter);

        }
    }

    private class NewsAdapter extends BaseExpandableListAdapter {

        private final Activity context;
        private final ArrayList<String> titleArray;
        private final ArrayList<String> contentArray;

        public NewsAdapter(Activity context, ArrayList<String> titleArray, ArrayList<String> contentArray){

            this.context = context;
            this.titleArray = titleArray;
            this.contentArray = contentArray;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
            LayoutInflater inflaterChild = context.getLayoutInflater();
            View rowViewContent = inflaterChild.inflate(R.layout.news_content_row, null, true);

            TextView article_content = (TextView) rowViewContent.findViewById(R.id.article_content);

            article_content.setText(contentArray.get(groupPosition));
            article_content.setPadding(50,0,0,0);

            return rowViewContent;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public int getGroupCount() {
            return titleArray.size();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return contentArray.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return titleArray.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //Return 1 to look at the 1-1 position of the child array
            return 1;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowViewTitle = inflater.inflate(R.layout.news_title_row, null, true);

            TextView article_title = (TextView) rowViewTitle.findViewById(R.id.article_title);

            article_title.setText(titleArray.get(groupPosition));
            article_title.setTextColor(Color.BLACK);
            return rowViewTitle;
        }
    }
}
