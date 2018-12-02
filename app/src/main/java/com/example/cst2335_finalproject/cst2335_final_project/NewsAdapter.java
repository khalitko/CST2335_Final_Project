package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * Adapter for the news list and saved news list.
 * @author Umber Setia
 */
public class NewsAdapter extends BaseExpandableListAdapter {

    /**
     * NewsActivity context.
     */
    private final Activity context;

    /**
     * Items used for holding article data.
     */
    private final ArrayList<String> titleArray, urlArray, descriptionArray, dateArray;
    private final ArrayList<Bitmap> newsPhotoPath;
    NewsDatabaseHelper db;

    /**
     * True for the case of making adapter for the saved article list. Otherwise false.
     */
    private boolean saved = false;

    /**
     * Constructor for NewsAdapter.
     * @param context - The context for the main activity
     * @param titleArray - The array that holds the article title information
     * @param urlArray - The array that holds the article URL information
     * @param descriptionArray - The array that holds the article description information
     * @param newsPhotoPath - The array that holds the article photo path
     * @param dateArray - The array that holds the article date information
     * @param saved - Determines if setting adapter for saved list (true) or news articles list (false)
     */
    public NewsAdapter(Activity context, ArrayList<String> titleArray, ArrayList<String> urlArray, ArrayList<String> descriptionArray, ArrayList<Bitmap> newsPhotoPath, ArrayList<String> dateArray, boolean saved){

        this.context = context;
        this.titleArray = titleArray;
        this.urlArray = urlArray;
        this.descriptionArray = descriptionArray;
        this.newsPhotoPath = newsPhotoPath;
        this.dateArray = dateArray;
        this.saved = saved;

        db = new NewsDatabaseHelper(context);

    }

    /**
     * Gets the children of the ExpandableListView.
     * @param groupPosition - The position of the main group in the ExpandableListView
     * @param childPosition - The position of the child within the main group of the ExpandableListView
     * @param isLastChild - True if on the last child of the main group in the ExpandableListView
     * @param view - The current child view
     * @param parent - The parent view
     * @return rowViewContent - The set information for the child
     */
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        LayoutInflater inflaterChild = context.getLayoutInflater();
        View rowViewContent = inflaterChild.inflate(R.layout.news_content_row, null, true);

        TextView articleDescription = (TextView) rowViewContent.findViewById(R.id.article_description);
        TextView articleUrl = (TextView) rowViewContent.findViewById(R.id.article_url);
        TextView articleTitleContent = (TextView) rowViewContent.findViewById(R.id.article_title_content);
        TextView articleDateContent = (TextView)rowViewContent.findViewById(R.id.article_date_content);
        final ImageView saveArticle = (ImageView) rowViewContent.findViewById(R.id.save_icon);
        ImageView deleteArticle = (ImageView) rowViewContent.findViewById(R.id.delete_icon);

        articleUrl.setPadding(0,50,0,0);
        articleUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlArray.get(groupPosition)));
                context.startActivity(intent);
            }
        });

        articleTitleContent.setText(titleArray.get(groupPosition));
        articleTitleContent.setPadding(0,0,0,30);
        articleTitleContent.setTextSize(20);

        articleDescription.setText(descriptionArray.get(groupPosition));
        articleDescription.setPadding(0,0,0,0);
        articleDescription.setTextColor(Color.WHITE);

        articleDateContent.setText(dateArray.get(groupPosition));
        articleDateContent.setTextSize(10);

        if (saved){
            saveArticle.setVisibility(View.GONE);
        } else {
            deleteArticle.setVisibility(View.GONE);
        }
        saveArticle.setImageResource(R.drawable.news_add_to_favorites);
        saveArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.articleExists(titleArray.get(groupPosition))){
                    Toast.makeText(context,R.string.article_already_saved, Toast.LENGTH_LONG).show();
                } else {
                    boolean insertSuccess = db.databaseInsert(
                            titleArray.get(groupPosition),
                            urlArray.get(groupPosition),
                            newsPhotoPath.get(groupPosition),
                            descriptionArray.get(groupPosition),
                            dateArray.get(groupPosition)
                    );
                    if (insertSuccess){
                        Toast.makeText(context,R.string.article_saved, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context,R.string.news_error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        deleteArticle.setImageResource(R.drawable.news_remove_from_favorites);
        deleteArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteRow(titleArray.get(groupPosition));
                titleArray.remove(groupPosition);
                urlArray.remove(groupPosition);
                descriptionArray.remove(groupPosition);
                newsPhotoPath.remove(groupPosition);
                dateArray.remove(groupPosition);
                notifyDataSetChanged();
                Toast.makeText(context, R.string.article_removed, Toast.LENGTH_LONG).show();
            }
        });
        notifyDataSetChanged();
        return rowViewContent;
    }

    /**
     *
     * @param groupPosition The position of the group
     * @param childPosition The position of the child
     * @return false - child is not selectable
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * The number of groups.
     * @return the size of the title array
     */
    @Override
    public int getGroupCount() {
        return titleArray.size();
    }

    /**
     * True if the ExpandableListView has stable Id's.
     * @return false
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Returns the child at the specified position.
     * @param groupPosition - The group position
     * @param childPosition - The child position
     * @return the child at the specified position.
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return urlArray.get(groupPosition);
    }

    /**
     * Gets the id of the group.
     * @param groupPosition - The group position.
     * @return the group position
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Gets the group.
     * @param groupPosition - The group position.
     * @return the group object
     */
    @Override
    public Object getGroup(int groupPosition) {
        return titleArray.get(groupPosition);
    }

    /**
     * Gets the child id.
     * @param groupPosition - The group position
     * @param childPosition - The child position
     * @return the child position
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Gets the number of children in a specified group.
     * @param groupPosition - The group position.
     * @return The number of children
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        //Return 1 to look at the 1-1 position of the child array
        return 1;
    }

    /**
     * Gets the information of the group for the ExpandableListView.
     * @param groupPosition
     * @param isExpanded
     * @param view
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowViewTitle = inflater.inflate(R.layout.activity_news_title_row, null, true);

        TextView articleTitle = (TextView) rowViewTitle.findViewById(R.id.article_title);
        ImageView articleImage = (ImageView) rowViewTitle.findViewById(R.id.article_image);
        TextView articleDate = (TextView)rowViewTitle.findViewById(R.id.article_date);

        articleTitle.setText(titleArray.get(groupPosition));
        articleTitle.setTextSize(20);
        articleTitle.setPadding(0,0,0,150);

        articleImage.setImageBitmap(newsPhotoPath.get(groupPosition));
        articleImage.setPadding(0,30,0,0);
        articleImage.setColorFilter(Color.argb(200, 0, 0, 0));

        articleDate.setText(dateArray.get(groupPosition));
        articleDate.setTextSize(10);

        if (isExpanded){
            articleTitle.setVisibility(View.GONE);
            articleDate.setVisibility(View.GONE);
            articleImage.setColorFilter(Color.argb(0,0,0,0));
            articleImage.setPadding(20,0,0,0);
        }

        return rowViewTitle;
    }
}