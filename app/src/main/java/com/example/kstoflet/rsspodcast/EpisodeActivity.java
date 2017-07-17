package com.example.kstoflet.rsspodcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kstoflet on 4/4/2017.
 */

public class EpisodeActivity extends AppCompatActivity {

    private Integer selectedEpisode;
    private ListView podcastList;
    private TextView episodeLabel;
    private TextView descriptionHtml;
    private List<String> listViewList;
    private ArrayList<RSSFeedModel> feedModelList;
    private ArrayAdapter<String> listViewArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodepage);

        listViewList = new ArrayList<String>();
        podcastList = (ListView) findViewById(R.id.episodeList);
        episodeLabel = (TextView) findViewById(R.id.episodeLabel);

        listViewArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listViewList);
        podcastList.setAdapter(listViewArrayAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            feedModelList = extras.getParcelableArrayList("podcastList");
            selectedEpisode = extras.getInt("selectedEpisode");
            episodeLabel.setText(feedModelList.get(selectedEpisode).getFeedTitle());
        }
        populateEpisodePage();
    }

    public void populateEpisodePage() {
        if (feedModelList.get(selectedEpisode).getFeedLink() != null && !feedModelList.get(selectedEpisode).getFeedLink().equals("")) {
            listViewList.add(feedModelList.get(selectedEpisode).getFeedLink());
        }
        if (feedModelList.get(selectedEpisode).getFeedDate() != null) {
            listViewList.add(feedModelList.get(selectedEpisode).getFeedDate().toString());
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            listViewList.add(dateFormat.format(date));
        }
        String formattedHtml = feedModelList.get(selectedEpisode).getFeedDescription();
        if (Html.fromHtml(formattedHtml) != null && !Html.fromHtml(formattedHtml).equals("")) {
            listViewList.add(Html.fromHtml(formattedHtml).toString());
        }
        if (feedModelList.get(selectedEpisode).getFeedDownload() != null && !feedModelList.get(selectedEpisode).getFeedDownload().equals("")) {
            listViewList.add(feedModelList.get(selectedEpisode).getFeedDownload());
        }
        if (String.valueOf(feedModelList.get(selectedEpisode).getFeedLength()) != null && feedModelList.get(selectedEpisode).getFeedLength() != 0) {
            listViewList.add(String.valueOf(feedModelList.get(selectedEpisode).getFeedLength()));
        }
    }
}
