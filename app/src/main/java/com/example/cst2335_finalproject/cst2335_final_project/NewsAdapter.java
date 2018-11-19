package com.example.cst2335_finalproject.cst2335_final_project;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends BaseExpandableListAdapter {

    private final Activity context;
    private final ArrayList<String> titleArray, urlArray, descriptionArray;


    public NewsAdapter(Activity context, ArrayList<String> titleArray, ArrayList<String> urlArray, ArrayList<String> descriptionArray){

        this.context = context;
        this.titleArray = titleArray;
        this.urlArray = urlArray;
        this.descriptionArray = descriptionArray;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        LayoutInflater inflaterChild = context.getLayoutInflater();
        View rowViewContent = inflaterChild.inflate(R.layout.news_content_row, null, true);

        TextView article_description = (TextView) rowViewContent.findViewById(R.id.article_description);
        TextView article_url = (TextView) rowViewContent.findViewById(R.id.article_url);

        article_url.setText(urlArray.get(groupPosition));
        article_url.setPadding(0,50,0,0);
        article_url.setTextColor(Color.WHITE);

//        if (groupPosition != 0){
//            article_description.setText(descriptionArray.get(groupPosition-1));
//            article_description.setPadding(0,0,0,0);
//        }




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
        return urlArray.get(groupPosition);
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
        View rowViewTitle = inflater.inflate(R.layout.activity_news_title_row, null, true);

        TextView article_title = (TextView) rowViewTitle.findViewById(R.id.article_title);

        article_title.setText(titleArray.get(groupPosition));
        article_title.setTextColor(Color.RED);
        article_title.setTextSize(20);
        article_title.setPadding(0,30,0,0);

        if (isExpanded){
            rowViewTitle.setBackgroundColor(Color.RED);
            article_title.setTextColor(Color.BLACK);
        }

        return rowViewTitle;
    }

}