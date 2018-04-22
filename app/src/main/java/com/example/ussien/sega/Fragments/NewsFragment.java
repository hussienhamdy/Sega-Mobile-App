package com.example.ussien.sega.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ussien.sega.Adapters.NewsListAdapter;
import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.News;
import com.example.ussien.sega.R;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {
    NewsListAdapter newsListAdapter;
    private boolean loadingFlag = true;
    private boolean firstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        final ListView listView = view.findViewById(R.id.new_listview);
        final View empty = view.findViewById(R.id.empty_layout);
        final View loading = view.findViewById(R.id.loading_layout);
        final TextView textView = view.findViewById(R.id.no_connection_txt);
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!loadingFlag)
                    loadNews(listView,empty,loading,textView,swipeRefreshLayout);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            // to solve the issue of scrolling with swipeRefreshLayout
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                if (listView.getChildAt(0) != null)
                    swipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
            }
        });
        loadNews(listView,empty,loading,textView,swipeRefreshLayout);
        return view;
    }

    private void loadNews(final ListView listView, final View empty, final View loading, final TextView textView,
                          final SwipeRefreshLayout swipeRefreshLayout)
    {
        loadingFlag = true;
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        int gameID = getArguments().getInt("gameID");
        Call<ArrayList<News>> call = apiInterface.loadNews(gameID);
        call.enqueue(new Callback<ArrayList<News>>() {
            @Override
            public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                loadingFlag = false;
                textView.setVisibility(View.GONE);
                ArrayList<News> newsArrayList = response.body();
                newsListAdapter = new NewsListAdapter(getActivity(),newsArrayList);
                listView.setAdapter(newsListAdapter);
                listView.scheduleLayoutAnimation();
                loading.setVisibility(View.GONE);
                if(newsArrayList.size()==0)
                {
                    empty.setVisibility(View.VISIBLE);
                }
                else
                {
                    empty.setVisibility(View.GONE);
                }
                if(firstLoad)
                {
                    updateData();
                    firstLoad=false;
                }
            }
            @Override
            public void onFailure(Call<ArrayList<News>> call, Throwable t) {
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                loadingFlag = false;
                textView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateData()
    {
        int gameID = getArguments().getInt("gameID");
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("67900da86aad75ba2530", options);
        Channel channel = pusher.subscribe("game"+Integer.toString(gameID)+"_channel");
        channel.bind("news_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            String image = jsonObject.getString("image");
                            String content = jsonObject.getString("content");
                            int newsID = jsonObject.getInt("newsID");
                            News news = new News(image,content,newsID);
                            newsListAdapter.addNews(news);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        pusher.connect();
    }
}
