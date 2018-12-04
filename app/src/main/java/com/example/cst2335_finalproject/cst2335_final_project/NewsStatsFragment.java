package com.example.cst2335_finalproject.cst2335_final_project;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Fragment for the users statistics.
 * @author Umber Setia
 */
public class NewsStatsFragment extends Fragment implements View.OnClickListener {

    /**
     * Button used to close fragment.
     */
    private Button returnButton;

    /**
     * Number of currently saved articles.
     */
    private TextView articleCount;

    /**
     * Highest number of words in an article.
     */
    private TextView maxWordCount;

    /**
     * Lowest number of words in an article.
     */
    private TextView minWordCount;

    /**
     * Average number of words for all articles.
     */
    private TextView avgWordCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragView = inflater.inflate(R.layout.activity_news_stats_fragment,container,false);

        returnButton = (Button) fragView.findViewById(R.id.close_stats);
        returnButton.setOnClickListener(this);

        Integer temp = ((NewsActivity)getActivity()).savedNewsPhotoPath.size();

        articleCount = (TextView) fragView.findViewById(R.id.articles_count_ans);
        articleCount.setText(temp.toString());

        temp = 0;

        avgWordCount = (TextView) fragView.findViewById(R.id.avg_count_ans);
        avgWordCount.setText(temp.toString());

        temp = 0;

        maxWordCount = (TextView) fragView.findViewById(R.id.max_count_ans);
        maxWordCount.setText(temp.toString());

        temp = 0;

        minWordCount = (TextView) fragView.findViewById(R.id.min_count_ans);
        minWordCount.setText(temp.toString());

        return fragView;
    }

    /**
     * Removes Fragment on button click.
     * https://stackoverflow.com/questions/5901298/how-to-get-a-fragment-to-remove-itself-i-e-its-equivalent-of-finish
     * @param v - fragment view
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.close_stats:
                Log.i("Stats Fragment","Closing fragment");

                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                ((NewsActivity)getActivity()).inStatsFragment = false;
                if (((NewsActivity)getActivity()).topNewsVisible){
                    ((NewsActivity)getActivity()).listView.setVisibility(View.VISIBLE);
                    ((NewsActivity)getActivity()).topArticles.setBackgroundColor(Color.RED);
                } else {
                    ((NewsActivity)getActivity()).savedListView.setVisibility(View.VISIBLE);
                    ((NewsActivity)getActivity()).favArticles.setBackgroundColor(Color.RED);
                }

                break;
        }
    }

}
