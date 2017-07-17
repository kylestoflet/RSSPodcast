package com.example.kstoflet.rsspodcast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RSSReaderActivity extends AppCompatActivity {

    private int podcastNumber = 1;
    private Button newPodcastButton;
    private GridView podcastGrid;
    private List<String> gridList;
    private HashMap<Integer, String> gridDataMap;
    private ArrayAdapter<String> gridViewArrayAdapter;
    private String feedTitle;
    private String feedLink;
    private Date feedDate;
    private String feedDescription;
    private String feedDownload;
    private ArrayList<RSSFeedModel> feedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssreader);

        newPodcastButton = (Button) findViewById(R.id.newPodcastButton);
        podcastGrid = (GridView)findViewById(R.id.podcastGrid);

        gridList = new ArrayList<String>();
        gridDataMap = new HashMap<Integer, String>();

        gridViewArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, gridList);
        podcastGrid.setAdapter(gridViewArrayAdapter);

        podcastGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {
                    new RetrieveFeedTask().execute(gridDataMap.get(position+1)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), PodcastPageActivity.class);
                intent.putParcelableArrayListExtra("podcastList", feedList);
                intent.putExtra("podcastName", gridList.get(position));
                startActivity(intent);
            }
        });

        newPodcastButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RSSReaderActivity.this);
                builder.setTitle("Podcast details:");

                // Set up the input
                LinearLayout podcastDetailLayout = new LinearLayout(RSSReaderActivity.this);
                podcastDetailLayout.setOrientation(LinearLayout.VERTICAL);
                final EditText podcastName = new EditText(RSSReaderActivity.this);
                podcastName.setHint("Name");
                final EditText podcastFeedURL = new EditText(RSSReaderActivity.this);
                podcastFeedURL.setHint("Feed URL");

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                podcastName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                podcastFeedURL.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                podcastDetailLayout.addView(podcastName);
                podcastDetailLayout.addView(podcastFeedURL);
                builder.setView(podcastDetailLayout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gridList.add(podcastName.getText().toString());
                        gridDataMap.put(podcastNumber, podcastFeedURL.getText().toString());
                        podcastNumber++;
                        gridViewArrayAdapter.notifyDataSetChanged();
                        String addedItemText = gridList.get(gridList.size()-1);
                        Toast.makeText(getApplicationContext(), "Item added : " + addedItemText, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    public void parseFeed(URL rssFeedUrl) throws IOException, FeedException {
        String title;
        String link;
        Date date;
        String description;
        String download = "";
        long length = 0;

        ArrayList<RSSFeedModel> items = new ArrayList<>();
        boolean isItem = false;

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(rssFeedUrl));
        for (int i=0; i < feed.getEntries().size(); i++) {
            title = feed.getEntries().get(i).getTitle();
            link = feed.getEntries().get(i).getLink();
            date = feed.getEntries().get(i).getPublishedDate();
            description = feed.getEntries().get(i).getDescription().getValue();
            if (feed.getEntries().get(i).getEnclosures().get(0) != null) {
                if (feed.getEntries().get(i).getEnclosures().get(0).getUrl() != null && !feed.getEntries().get(i).getEnclosures().get(0).getUrl().equals("")) {
                    download = feed.getEntries().get(i).getEnclosures().get(0).getUrl();
                }
                if (feed.getEntries().get(i).getEnclosures().get(0).getLength() != 0) {
                    length = feed.getEntries().get(i).getEnclosures().get(0).getLength();
                }
            }
            RSSFeedModel item = new RSSFeedModel(title, link, date, description, download, length);
            items.add(item);
            feedTitle = title;
            feedLink = link;
            feedDate = date;
            feedDescription = description;
            feedDownload = download;
            title = null;
            link = null;
            date = null;
            description = null;
            download = null;
            isItem = false;
        }
        feedList = items;
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String rssFeedText = urls[0];
            // Let's make sure the URL isn't empty
            if (TextUtils.isEmpty(rssFeedText))
                return "Please provide an RSS feed URL.";
            //Let's also make sure that it has http or https appended to the front of it
            if(!rssFeedText.startsWith("http://") && !rssFeedText.startsWith("https://"))
                rssFeedText = "http://" + rssFeedText;
            try {
                URL rssFeedURL = new URL(rssFeedText);
                // We will get the XML from an input stream
                InputStream inputStream = rssFeedURL.openConnection().getInputStream();
                parseFeed(rssFeedURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "A MalformedURLException occured. Reference log files for more information.";
            } catch (IOException e) {
                e.printStackTrace();
                return "An IOException occured. Reference log files for more information.";
            } catch (Exception e) {
                e.printStackTrace();
                return "An Exception occured. Reference log files for more information.";
            }
            return "";
        }
    }
}
