package com.murach.newsreader;


import java.util.ArrayList;
import java.util.HashMap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class ItemsFragment extends Fragment implements OnItemClickListener {



    private RSSFeed feed;
    private FileIO io;

    private TextView titleTextView;
    private ListView itemsListView;



    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_items, container, false);
        io = new FileIO(getActivity().getApplicationContext());


        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        itemsListView = (ListView) view.findViewById(R.id.itemsListView);
        itemsListView.setOnItemClickListener(this);

        new DownloadFeed().execute();

        return view;

    }

    class DownloadFeed extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed downloaded");
            new ReadFeed().execute();

        }
    }

    class ReadFeed extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            feed = io.readFile();

            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed read");

            ItemsFragment.this.updateDisplay();

        }

    }

    public void updateDisplay()

    {
        if (feed == null) {
            titleTextView.setText("Unable to get RSS feed");

            return;

        }

        titleTextView.setText(feed.getTitle());

        ArrayList<RSSItem> items = feed.getAllItems();

        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();

        for (RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());

            data.add(map);

        }

        int resource = R.layout.listview_item;
        String[] from = {"date", "title"};
        int[] to = {R.id.pubDateTextView, R.id.titleTextView};


        SimpleAdapter adapter =
                new SimpleAdapter(getActivity(), data, resource, from, to);
        itemsListView.setAdapter(adapter);
        Log.d("News reader", "Feed displayed");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {

        RSSItem item = feed.getItem(position);

     // Intent Activity
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra("pubdate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);

    }

}
