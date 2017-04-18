package com.ulduzsoft.karaoke.karaokesearch;

import android.os.Bundle;
import android.widget.ListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by syunaeva on 12/20/15.
 */
public class PickASong extends Activity {

    //URL to get JSON Array
    private static String addSongUrl = "http://media.sweethome:8000/api/addsong";
    // Actions for Async tasks class
    //JSON Node Names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "title";
    private static final String TAG_SINGER = "artist";

    ListView listView;
    private String loginName;
    private int SongId = 0;
    private String[] songs;
     int[] songs_ID;
    private int song_pos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songslist);
        Intent myIntent = getIntent(); // gets the previously created intent
        loginName = myIntent.getStringExtra("username");
        String jsonArray = myIntent.getStringExtra("json");

        JSONArray jObj = null;
        try {
            jObj = new JSONArray(jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // read jSON to 2 arrays
        // songs + singer
        if (!(jObj == null)) {
            Log.i(JSONParser.class.getName(), "check if the json is sorted ========> " + jObj.toString());
            songs = new String[jObj.length()];
             songs_ID = new int[jObj.length()];

            for (int i = 0; i < jObj.length(); i++)
            {
                try {
                    JSONObject c = jObj.getJSONObject(i);
                    songs[i] = "\"" + c.getString(TAG_NAME) + "\" by " + c.getString(TAG_SINGER);
                    songs_ID[i] = c.getInt(TAG_ID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, songs);
            // Assign adapter to ListView
            listView.setAdapter(adapter);

        }else
            Toast.makeText(getApplicationContext(), "Cannot show songs list", Toast.LENGTH_LONG).show();
        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                //SongId = position;

                SongId = songs_ID[position];
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                song_pos = position;

                  new JSONParse().execute();

            }

        });
    }
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(PickASong.this);
            pDialog.setMessage("Sending Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONObject json = null;
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            try {
                json = jParser.setSearchToQueue(addSongUrl, loginName, SongId );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            // create hash list
            if (!(json == null)) {if (json.length() > 0)  {
                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Song : " + songs[song_pos] + " is added to queue", Toast.LENGTH_LONG)
                        .show();
                Intent PickASongIntent = new Intent(PickASong.this,
                        MainActivity.class);
                PickASongIntent.putExtra("json", json.toString());
                PickASongIntent.putExtra("loginName", loginName);
                // Use the Intent to start the Main Activity
                startActivity(PickASongIntent);

            }else
                Toast.makeText(PickASong.this, "Cannot add the song into queue", Toast.LENGTH_LONG).show();}
            else
                Toast.makeText(PickASong.this, "Cannot add the song into queue", Toast.LENGTH_LONG).show();

        }


    }

}
