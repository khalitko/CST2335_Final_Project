package com.example.cst2335_finalproject.cst2335_final_project;

import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;

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
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "NewsActivity";

    private ExpandableListView listView;
    private ExpandableListView savedListView;
    private TextView article_title;

    public ArrayList<String> newsTitles, newsUrl, newsDescription;
    public ArrayList<Bitmap> newsPhotoPath;
    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;

    public int counter;
    private Toast toast;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        createToolBar();

        article_title = findViewById(R.id.article_title);

        newsTitles = new ArrayList<>();
        newsUrl = new ArrayList<>();
        newsDescription = new ArrayList<>();
        newsPhotoPath = new ArrayList<>();

        NewsQuery query = new NewsQuery();
        query.execute();

        newsAdapter = new NewsAdapter(this,newsTitles,newsUrl,newsDescription,newsPhotoPath);

        savedListView = findViewById(R.id.saved_list);
    }

    private void createToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        setSupportActionBar(toolbar);
    }

    public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.toolbar_menu_news,m);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case R.id.top_articles:
                Log.d("Toolbar","Top articles selected");
                savedListView.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                //toast.makeText(this,"Top Articles",Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.snackbar_text_holder),"Top Articles",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.favorite_articles:
                Log.d("Toolbar","Favorite articles selected");
                listView.setVisibility(View.INVISIBLE);
                savedListView.setVisibility(View.VISIBLE);
                //toast.makeText(this,"Favorites",Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.snackbar_text_holder),"Favorites",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder((this));
                builder.setTitle("Help");
                builder.setIcon(R.drawable.news_help);
                builder.setMessage("Author: Umber Setia " +
                        "\nVersion: 2018-12-03 " +
                        "\nInstructions: " +
                        "\n  Click pages icon to view top articles." +
                        "\n  Click plain yellow star to view your saved" +
                        "\n  articles." +
                        "\n  Click on an article to see it's description." +
                        "\n  Click the yellow star with a '+' in it to save" +
                        "\n  the article"
                );
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
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
     */
    private class NewsQuery extends AsyncTask<String, Integer, String>{
        private String iconName;
        private Bitmap bitmap;
        private String result,cdata;

        public boolean fileExistance(String fName){
            File file = getBaseContext().getFileStreamPath(fName);
            return file.exists();
        }

        private void loadImage(String path) throws Exception{
            // if (!fileExistance(path)){
            Log.i("Downloading image ", path);

            bitmap = HttpUtils.getImage(path);
            //https://stackoverflow.com/questions/18342463/stretch-image-to-fit
            bitmap = Bitmap.createScaledBitmap(bitmap,1000,500,true);
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

        private void fileTypeJpeg(){
            try {
                Log.i("Article Image download", "trying .JPG");
                result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".JPG'")+5);
            } catch (Exception e){
                Log.i("Article Image download", "trying .jpeg");
                result = cdata.substring(cdata.indexOf("src='")+5,cdata.indexOf(".jpeg'")+6);
            }
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
