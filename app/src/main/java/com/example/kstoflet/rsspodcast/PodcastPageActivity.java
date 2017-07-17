package com.example.kstoflet.rsspodcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kstoflet on 4/3/2017.
 */

public class PodcastPageActivity extends AppCompatActivity {

    private ListView podcastList;
    private TextView podcastLabel;
    private List<String> listViewList;
    private ArrayList<RSSFeedModel> feedModelList;
    private ArrayAdapter<String> listViewArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcastpage);

        listViewList = new ArrayList<String>();
        podcastList = (ListView) findViewById(R.id.podcastList);
        podcastLabel = (TextView) findViewById(R.id.podcastLabel);

        listViewArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listViewList);
        podcastList.setAdapter(listViewArrayAdapter);

        Bundle extras = getIntent().getExtras();
        podcastLabel.setText("Episodes for " + extras.getString("podcastName"));
        if (extras != null) {
            feedModelList = extras.getParcelableArrayList("podcastList");
        }
        populatePodcastPage();

        podcastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EpisodeActivity.class);
                intent.putParcelableArrayListExtra("podcastList", feedModelList);
                intent.putExtra("selectedEpisode", position);
                startActivity(intent);
            }
        });
    }

    public void populatePodcastPage() {
        for( int i = 0; i < feedModelList.size(); i++) {
            listViewList.add(feedModelList.get(i).getFeedTitle());
        }
    }
}
