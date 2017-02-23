package com.example.hp.swiperefreshlayouttest01;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HP on 2016/10/16.
 */

public class NewsContent extends Activity {

    WebView story ;
    TextView story_title;
    public static final int SHOW_RESPONSE = 0;
    public String urlString = "http://open.twtstudio.com/api/v1/news/";

    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    JSONObject jsonObject = null;
                    JSONObject jsonobject_news;
                    try {
                        jsonObject = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonobject_news = jsonObject.getJSONObject("data");
                        String content = jsonobject_news.getString("content");
                        String title = jsonobject_news.getString("subject");
                        content = content.replace("100%","80%");
                        story.loadDataWithBaseURL(null, content,"text/html","utf-8",null);
                        story_title.setText(title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        story = (WebView) findViewById(R.id.story);
        story_title = (TextView)findViewById(R.id.story_title);

        Bundle bundle=getIntent().getExtras();
        String index = bundle.getString("index");
        urlString += index ;

        sendRequestWithHttpURLConnection();

    }
    public void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;

                    message.obj = response.toString();
                    handler.sendMessage(message);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
