package com.ulduzsoft.karaoke.karaokesearch;

/**
 * Created by syunaeva on 12/19/15.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class JSONParser {
    static StringBuffer result = null;
    private static final String TAG_QUERY = "query";

    // constructor
    public JSONParser() {}
    public JSONArray getJSONFromUrl(String url , String param) {

        JSONObject json = new JSONObject();
        try {
            json.put(TAG_QUERY, param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Making HTTP request
        try {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json ;charset=utf-8");
            httpPost.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));

            // request a list of songs and read it into the buffer
            HttpResponse httpResponse = httpClient.execute(httpPost);
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = "";
            result = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // try parse the string to a JSON object
        JSONArray jObj = null;

        try {

            // check here for NPE
            if (!(result == null))
                jObj = new JSONArray(result.toString());

            else
                Log.i(JSONParser.class.getName(), "results is empty");

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    public JSONObject setSearchToQueue(String url , String userName , int songId) throws JSONException {
        // Making HTTP request
        try {
            // create json request: curl -d "{ \"i\": 20898, \"s\": \"safiya\" }" http://localhost:8000/api/addsong
            StringEntity params =new StringEntity(
                    "{ \"id\": " + songId + "," + "\"singer\":\"" + userName + "\"}");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(params);

            Log.e("JSON Parser", "Json to send ===========> " + params.toString());
            HttpResponse httpResponse = httpClient.execute(httpPost);

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = "";
            result = new StringBuffer();
            while ((line = rd.readLine()) != null)
                result.append(line);
            Log.i(JSONParser.class.getName(), "response from the server" + result);

        } catch (UnsupportedEncodingException e) {
            Log.e("JSON Parser", "Error =====> UnsupportedEncodingException : " + e.toString());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e("JSON Parser", "Error ======> ClientProtocolException " + e.toString());
        } catch (IOException e) {
            Log.e("JSON Parser", "Error ======> IOException " + e.toString());
            e.printStackTrace();
        }


        JSONObject jObj = null;
            try {

                jObj = new JSONObject(result.toString());
                Log.i(JSONParser.class.getName(), jObj.toString());

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

        return jObj;
    }
}
