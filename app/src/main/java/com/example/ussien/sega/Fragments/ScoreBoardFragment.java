package com.example.ussien.sega.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ussien.sega.Adapters.ScoreBoardAdapter;
import com.example.ussien.sega.Model.ApiClient;
import com.example.ussien.sega.Model.ApiInterface;
import com.example.ussien.sega.Model.News;
import com.example.ussien.sega.Model.ScoreBoard;
import com.example.ussien.sega.R;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.SwipeToRefreshListener;
import de.codecrafters.tableview.listeners.TableHeaderClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreBoardFragment extends Fragment {
    private static final int[] TABLE_HEADERS = { R.string.table_image, R.string.table_name,
                                                 R.string.table_progress, R.string.table_points };
    private ArrayList<ScoreBoard> scoreBoardArrayList = new ArrayList<>();
    private ScoreBoardAdapter scoreBoardAdapter;
    private boolean loadingFlag = true;
    private boolean firstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score_board, container, false);
        final TextView textView = view.findViewById(R.id.no_connection_txt);

        final View loading = view.findViewById(R.id.loading_layout);

        final SortableTableView sortableTableView = view.findViewById(R.id.tableView);
        sortableTableView.setSwipeToRefreshEnabled(true);
        sortableTableView.setSwipeToRefreshListener(new SwipeToRefreshListener() {
            @Override
            public void onRefresh(RefreshIndicator refreshIndicator) {
                if(!loadingFlag)
                    loadScoreBoard(sortableTableView,textView,loading,refreshIndicator);
            }
        });

        //to hide the header titles
        sortableTableView.getChildAt(0).setVisibility(View.GONE);

        SwipeToRefreshListener.RefreshIndicator refreshIndicator=null;

        loadScoreBoard(sortableTableView,textView,loading,refreshIndicator);

        return view;
    }

    private static class ScoreBoardComparator implements Comparator<ScoreBoard> {
        @Override
        public int compare(ScoreBoard scoreBoard1, ScoreBoard scoreBoard2) {
            return scoreBoard2.getPoints() < scoreBoard1.getPoints()? -1 :
                    scoreBoard1.getPoints() == scoreBoard2.getPoints() ? 0 : 1;
        }
    }
    private void loadScoreBoard(final SortableTableView sortableTableView, final TextView textView,
                                final View loading, final SwipeToRefreshListener.RefreshIndicator refreshIndicator)
    {
        loadingFlag = true;
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        int gameID = getArguments().getInt("gameID");
        Call<ArrayList<ScoreBoard>> call = apiInterface.loadscoreBoard(gameID);
        call.enqueue(new Callback<ArrayList<ScoreBoard>>() {
            @Override
            public void onResponse(Call<ArrayList<ScoreBoard>> call, Response<ArrayList<ScoreBoard>> response) {
                if(refreshIndicator!=null)
                    refreshIndicator.hide();
                loadingFlag = false;
                textView.setVisibility(View.GONE);
                scoreBoardArrayList = response.body();
                scoreBoardAdapter = new ScoreBoardAdapter(getActivity(), scoreBoardArrayList);
                sortableTableView.setDataAdapter(scoreBoardAdapter);
                loading.setVisibility(View.GONE);
                if(firstLoad)
                {
                    updateData();
                    firstLoad = false;
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ScoreBoard>> call, Throwable t) {
                if(refreshIndicator!=null)
                    refreshIndicator.hide();
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
        channel.bind("scoreboard_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                                JSONObject object = new JSONObject(data);
                                int progress = object.getInt("progress");
                                int points = object.getInt("points");
                                int studentID = object.getInt("studentID");
                                scoreBoardAdapter.update(studentID,progress,points);
                                scoreBoardAdapter.sort(new ScoreBoardComparator());
                                scoreBoardAdapter.notifyDataSetChanged();
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

