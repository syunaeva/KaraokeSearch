package com.ulduzsoft.karaoke.karaokesearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity{
    //URL to get JSON Array
    private static String searchUrl = "http://media.sweethome:8000/api/search";
            // "http://192.168.0.5:8000/api/search";
    private EditText edittext;
    private String loginName;
    AutoCompleteTextView myView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        loginName = myIntent.getStringExtra("loginName");

       setContentView(R.layout.activity_main);
        // // Set and register the adapter for the AutoCompleteTextView
             myView = (AutoCompleteTextView) findViewById(R.id.editText);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.songslist, saveSearches(""));
            myView.setAdapter(adapter);

            TextView tv=(TextView)findViewById(R.id.textView);
            tv.append(" , " + loginName + "!");

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //registerForContextMenu(tv);
            addKeyListener();



    }

    // Create Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // Process clicks on Options Menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent logout = new Intent(MainActivity.this,
                        LoginScreen.class);

                // Use the Intent to start the Main Activity
                startActivity(logout);
                return true;
        }
        return false;
    }


    public void addKeyListener() {

        // get edittext component
        edittext = (EditText) findViewById(R.id.editText);
        // add a keylistener to keep track user input
        edittext.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // if keydown and "enter" is pressed
               if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        String search = edittext.getText().toString();
                        edittext.setText("");
                        if (search.length() > 0)
                            new JSONParse().execute(searchUrl, search);
                        //    new JSONParse().execute(ACTION_ADDSONG, addSongUrl, loginName, songID);
                        return true;
                    }

                return false;
            }
        });
    }

    public String[] saveSearches(String search) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();

        if (!prefs.contains("initialized")) {
            editor.putBoolean("initialized", true);
            if (search.equals("")) return new String[0];
            sb.append(search);
            System.out.println("====init the prefs====> " + search);
            editor.putString("searchList", sb.toString());
            editor.commit();
        } else {
            String searchList = prefs.getString("searchList", "DEFAULT");
            if (search.equals("")) return  searchList.split(",");

            sb.append(searchList).append(","+ search);
            System.out.println("====append====> " + sb.toString());
            editor.putString("searchList", sb.toString());
            editor.commit();
        }
        String searchList = prefs.getString("searchList", "DEFAULT");

        System.out.println("====return this searchlist====> " + searchList);
        String[] searchLists = searchList.split(",");
        return searchLists;
    }



    private class JSONParse extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONArray doInBackground(String... args) {
            String searchString = "";
            JSONArray json = null;
            String URL = args[0];
            searchString = args[1];
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
             json = jParser.getJSONFromUrl(URL, searchString);
            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {
            pDialog.dismiss();
                    // create hash list
                    if (!(json == null))
                    {if (json.length() > 0)  {
                        Intent PickASongIntent = new Intent(MainActivity.this,
                                PickASong.class);
                        PickASongIntent.putExtra("json", json.toString());
                        PickASongIntent.putExtra("username", loginName);
                        // Use the Intent to start the Main Activity
                        startActivity(PickASongIntent);

                    }else
                        Toast.makeText(MainActivity.this, "No songs found", Toast.LENGTH_LONG).show();}
                    else
                        Toast.makeText(MainActivity.this, "No songs found", Toast.LENGTH_LONG).show();
                    }


    }
}
