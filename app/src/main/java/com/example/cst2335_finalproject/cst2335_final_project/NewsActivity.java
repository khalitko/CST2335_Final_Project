package com.example.cst2335_finalproject.cst2335_final_project;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Main activity for news.
 * @author Umber Setia
 */
public class NewsActivity extends AppCompatActivity {
    //protected static final String ACTIVITY_NAME = "NewsActivity";

    /**
     * Items that are seen on the screen.
     */
    private ExpandableListView listView;
    private ExpandableListView savedListView;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    /**
     * Holds items that are extracted from the CBC website.
     */
    private ArrayList<String> newsTitles, newsUrl, newsDescription, newsDate;
    private ArrayList<Bitmap> newsPhotoPath;

    /**
     * Holds items that are saved in the database.
     */
    private ArrayList<String> savedNewsTitles, savedNewsUrl, savedNewsDescription, savedNewsDate;
    private ArrayList<Bitmap> savedNewsPhotoPath;

    /**
     * Puts items in an ExpandableListView.
     */
    private NewsAdapter newsAdapter;
    private NewsAdapter savedNewsAdapter;

    /**
     * Pulls items from the CBC website.
     */
    private NewsQuery query;

    /**
     * Used for increasing the progressbar value.
     */
    private int counter;

    /**
     * Used to look through database.
     */
    private Cursor result;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        createToolBar();

        newsTitles = new ArrayList<>();
        newsUrl = new ArrayList<>();
        newsDescription = new ArrayList<>();
        newsPhotoPath = new ArrayList<>();
        newsDate = new ArrayList<>();

        savedNewsTitles = new ArrayList<>();
        savedNewsUrl = new ArrayList<>();
        savedNewsDescription = new ArrayList<>();
        savedNewsPhotoPath = new ArrayList<>();
        savedNewsDate = new ArrayList<>();

        query = new NewsQuery();
        query.execute();

        newsAdapter = new NewsAdapter(this,newsTitles,newsUrl,newsDescription,newsPhotoPath, newsDate, false);

        savedListView = findViewById(R.id.saved_list);

        updateSavedList();
    }

    /**
     * Updates the user's saved articles list.
     */
    public void updateSavedList(){
        if (savedNewsTitles.size() > 0){
            savedNewsTitles.clear();
            savedNewsUrl.clear();
            savedNewsPhotoPath.clear();
            savedNewsDescription.clear();
            savedNewsDate.clear();
        }

        result = newsAdapter.db.getTableData();
        result.moveToFirst();
        if (result.getCount() != 0){
            while (!result.isAfterLast()){
                //System.out.println(result.getString(1));
                savedNewsTitles.add(result.getString(1));
                savedNewsUrl.add(result.getString(2));
                savedNewsPhotoPath.add(byteArrayToBitmap(result.getBlob(3)));
                savedNewsDescription.add(result.getString(4));
                savedNewsDate.add(result.getString(5));
                result.moveToNext();
            }
            savedNewsAdapter = new NewsAdapter(this,savedNewsTitles,savedNewsUrl,savedNewsDescription,savedNewsPhotoPath, savedNewsDate,true);
            savedListView.setAdapter(savedNewsAdapter);
        }
        Log.i("Saved List","Updated");
    }
    /**
     * https://stackoverflow.com/questions/7620401/how-to-convert-byte-array-to-bitmap
     * Coverts a blob to a bitmap.
     * @param bArray - byte array that needs to be converted
     * @return the resulting bitmap
     */
    private Bitmap byteArrayToBitmap(byte[] bArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
        return bitmap;
    }

    /**
     * Makes and determines properties of the Toolbar.
     */
    private void createToolBar(){
        toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        toolbar.setTitle(R.string.news_reader);
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    /**
     * onCreate for the menu
     * @param m - menu for the Toolbar
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.toolbar_menu_news,m);
        return true;
    }

    /**
     * Determines what happens when the user clicks a menu item.
     * @param mi - menu item that is selected
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case R.id.top_articles:
                Log.d("Toolbar","Top articles selected");
                savedListView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(R.id.snackbar_text_holder),R.string.top_articles,Snackbar.LENGTH_LONG).show();
                break;
            case R.id.favorite_articles:
                Log.d("Toolbar","Favorite articles selected");
                listView.setVisibility(View.GONE);
                savedListView.setVisibility(View.VISIBLE);
                updateSavedList();
                Snackbar.make(findViewById(R.id.snackbar_text_holder),R.string.saved_articles,Snackbar.LENGTH_LONG).show();
                break;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder((this));
                builder.setTitle(R.string.news_help);
                builder.setIcon(R.drawable.news_help);
                builder.setMessage(R.string.news_toolbar_help);
                builder.setPositiveButton(R.string.news_dialog_ok,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Done", "User clicked OK");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return true;
    }

    /**
     * https://stackoverflow.com/questions/27415449/xmlpullparser-parse-nested-tag
     * Extracts information from the CBC website.
     * @author Umber Setia
     */
    private class NewsQuery extends AsyncTask<String, Integer, String>{
        /**
         * Stores extracted image.
         */
        private Bitmap bitmap;

        /**
         * Temporary variables to hold strings from the CBC website.
         */
        private String result,cdata;

        /**
         * Figures out if the file already exists on the system.
         * @param fName - path of the file
         * @return true if the file already exists
         */
        public boolean fileExistance(String fName){
            File file = getBaseContext().getFileStreamPath(fName);
            return file.exists();
        }

        /**
         * Stores the image for an article.
         * @param path - path of the image
         * @throws Exception
         */
        private void loadImage(String path) throws Exception{
            // if (!fileExistance(path)){
            Log.i("Downloading image ", path);

            bitmap = HttpUtils.getImage(path);
            //https://stackoverflow.com/questions/18342463/stretch-image-to-fit
            bitmap = Bitmap.createScaledBitmap(bitmap,1300,500,true);
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

        /**
         * Extracts items from the CBC website.
         * @param args Any number of String arguments
         * @return
         */
        public String doInBackground(String ...args){
            String name;
            try {
                URL url = new URL("https://www.cbc.ca/cmlink/rss-world");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
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
                            } else if (name.equals("pubDate")) {
                                newsDate.add(xpp.nextText());
                            } else if (name.equals("description")){
                                //loop sets up the string for extraction
                                int token = xpp.nextToken();

                                while (token!=XmlPullParser.CDSECT){
                                    token = xpp.nextToken();
                                }

                                cdata = xpp.getText();

                                try {
                                    Log.i("Article Image download", "trying .jpg");
                                    result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".jpg'")+5);
                                } catch (Exception e){
                                    fileTypeJpeg();
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

        /**
         * Used for loading the correct image type.
         */
        private void fileTypeJpeg(){
            try {
                Log.i("Article Image download", "trying .JPG");
                result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".JPG'")+5);
            } catch (Exception e){
                Log.i("Article Image download", "trying .jpeg");
                result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".jpeg'")+6);
            }
        }

        /**
         * Updates the progressbar.
         * @param value the progress values
         */
        public void onProgressUpdate(Integer ...value){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        /**
         * Updates the ExpandableListView responsible for displaying new articles.
         * @param result
         */
        public void onPostExecute(String result){

            listView = findViewById(R.id.newsList);

            if (newsTitles.size() != 0){
                for (int i = 0; i < 2; i++){
                    newsTitles.remove(0);
                    newsUrl.remove(0);
                }
                newsDescription.remove(0);
            }

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
            savedListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (groupPosition != previousGroup){
                        savedListView.collapseGroup(previousGroup);
                    }
                    previousGroup = groupPosition;
                }
            });
            progressBar.setVisibility(View.GONE);
        }
    }

}
