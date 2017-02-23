package com.example.hp.swiperefreshlayouttest01;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public String[] indexList = new String[100];

    public final static String url = "http://open.twtstudio.com/api/v1/news/1/page/1";

    private ListView mListView;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.main);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        new NewsAsyncTask().execute(url);
        mSwipeLayout.setOnRefreshListener(mSwipeRefreshLayoutOnRefreshListener);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_green_light);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                Bundle bundle = new Bundle();
                bundle.putString("index", indexList[arg2]);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, NewsContent.class);
                startActivity(intent);

            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener mSwipeRefreshLayoutOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSwipeLayout.setRefreshing(true);
                    new NewsAsyncTask().execute(url);
                }
            };

    private List<NewsBean> getJsonData(String url) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jsonString = readStream(new URL(url).openStream());
            JSONObject jsonObject;
            NewsBean newsBean;
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                newsBean = new NewsBean();
                Gson gson = new Gson();
           //     newsBean.newsIconUrl = gson.fromJson("pic",String.class);
             //   newsBean.newsTitle = gson.fromJson("subject",String.class);
                newsBean.newsIconUrl = jsonObject.getString("pic");
                newsBean.newsTitle = jsonObject.getString("subject");
                newsBean.newsContent = jsonObject.getString("summary");
                newsBean.newContentUrl = jsonObject.getString("index");
                indexList[i] = String.valueOf(jsonObject.getLong("index"));
                newsBeanList.add(newsBean);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }

    private String readStream(InputStream is) {
        InputStreamReader isr;
        String res = "";
        try {
            String line;
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                res += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, newsBeans);
            mListView.setAdapter(newsAdapter);
            mSwipeLayout.setRefreshing(false);
        }
    }
}