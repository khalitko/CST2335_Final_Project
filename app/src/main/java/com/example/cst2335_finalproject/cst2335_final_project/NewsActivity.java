package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public ArrayList<Bitmap> newsPhotoPath;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;

    public int counter;

    private TextView savedArticles;
    private TextView newsArticles;
    private Button statsButton;

    private GridView optionGrid;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

//        displayToolBar();

        article_title = findViewById(R.id.article_title);

        newsTitles = new ArrayList<>();
        newsUrl = new ArrayList<>();
        newsDescription = new ArrayList<>();
        newsPhotoPath = new ArrayList<>();

        savedArticles = findViewById(R.id.saved_articles);
        newsArticles = findViewById(R.id.news_articles);

        NewsQuery query = new NewsQuery();
        query.execute();

        newsAdapter = new NewsAdapter(this,newsTitles,newsUrl,newsDescription,newsPhotoPath);

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

//    private void displayToolBar(){
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.news_tool_bar);
//        setActionBar(myToolbar);
//        getActionBar().setDisplayShowTitleEnabled(true);
//        myToolbar.setTitle("News Reader");
//    }

    /**
     * https://stackoverflow.com/questions/27415449/xmlpullparser-parse-nested-tag
     */
    private class NewsQuery extends AsyncTask<String, Integer, String>{
        private String iconName;
        private Bitmap bitmap;
        private String result,result1,cdata;

        public boolean fileExistance(String fName){
            File file = getBaseContext().getFileStreamPath(fName);
            return file.exists();
        }

        private void loadImage(String path) throws Exception{
            // if (!fileExistance(path)){
            Log.i("Downloading image ", path);

            bitmap = HttpUtils.getImage(path);
//                FileOutputStream outputStream = openFileOutput("NewsImages.jpg",Context.MODE_PRIVATE);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//                outputStream.flush();
//                outputStream.close();
            //FileInputStream fis = null;
            //} else {
//                Log.i("Found image locally", "NewsImages.jpg");
//                FileInputStream fis = null;
//                try {
//                    fis = openFileInput("NewsImages.jpg");
//                } catch (FileNotFoundException e){
//                    e.printStackTrace();
//                }
            //bitmap = BitmapFactory.decodeStream(fis);
            // }
        }

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

                counter = 20;

                while (xpp.getEventType() != XmlResourceParser.END_DOCUMENT){
                    name = xpp.getName();
                    switch (xpp.getEventType()){
                        case XmlResourceParser.START_TAG:
                            if (name.equals("title")){
                                newsTitles.add(xpp.nextText());
                            } else if (name.equals("link")){
                                newsUrl.add(xpp.nextText());
                            } else if (name.equals("description")){
                                //loop sets up the string for extraction
                                int token = xpp.nextToken();

                                while (token!=XmlPullParser.CDSECT){
                                    token = xpp.nextToken();
                                }

                                cdata = xpp.getText();

                                try {
                                    result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".jpg'")+5);
                                } catch (Exception e){
                                    Log.i("Article Image download", ".JPG instead of .jpg");
                                    result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".JPG'")+5);
                                }

                                if (result.length() > 0){
                                    result = result.substring(0,result.length()-1);
                                    loadImage(result);
                                    newsPhotoPath.add(bitmap);
                                }

                                result = cdata.substring(cdata.indexOf("title='")+7);
                                result = result.substring(result.indexOf("<p>")+3,result.indexOf("</p>")+4);
                                if (result.length() > 4){
                                    result = result.substring(0,result.length()-4);
                                }
                                newsDescription.add(result);

                                if (counter < 100){
                                    publishProgress(counter);
                                    counter += 10;
                                } else {
                                    publishProgress(100);
                                }
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

        public void onProgressUpdate(Integer ...value){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String result){

            listView = findViewById(R.id.newsList);

            for (int i = 0; i < 2; i++){
                newsTitles.remove(0);
                newsUrl.remove(0);
                //newsDescription.remove(0);
                //newsPhotoPath.remove(0);
            }
            newsDescription.remove(0);
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
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
