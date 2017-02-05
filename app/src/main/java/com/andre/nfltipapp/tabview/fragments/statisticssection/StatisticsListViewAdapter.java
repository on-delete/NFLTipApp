package com.andre.nfltipapp.tabview.fragments.statisticssection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.StatisticForGameActivity;
import com.andre.nfltipapp.model.AllPredictionsRequest;
import com.andre.nfltipapp.model.AllPredictionsResponse;
import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.rest.RequestInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatisticsListViewAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private List<String> expandableListTitle = new ArrayList<>();
   private HashMap<String, List<Game>> expandableListDetail = new HashMap<>();

    public StatisticsListViewAdapter(Activity activity, List<Prediction> predictionList) {
        this.activity = activity;

        for(Prediction predictionItem : predictionList){
            List<Game> tempGamesList = new ArrayList<>();

            for (Game game : predictionItem.getGames()){
                if(game.isFinished()==1){
                    tempGamesList.add(game);
                }
            }

            if(tempGamesList.size()>0){
                String title = "Woche " + predictionItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionItem.getType()) : "");
                this.expandableListTitle.add(title);
                this.expandableListDetail.put(title, tempGamesList);
            }
        }

        Collections.reverse(expandableListTitle);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Game expandedListItem = (Game) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.statistics_list_item, parent, false);
        }

        TextView homeScoreTextView = (TextView) convertView
                .findViewById(R.id.home_team_score_text);
        TextView awayScoreTextView = (TextView) convertView
                .findViewById(R.id.away_team_score_text);

        LinearLayout statisticRow = (LinearLayout)  convertView.findViewById(R.id.statistics_row);
        ImageView awayTeamIcon = (ImageView) convertView.findViewById(R.id.icon_away_team);
        ImageView homeTeamIcon = (ImageView) convertView.findViewById(R.id.icon_home_team);
        LinearLayout awayLogoBackground = (LinearLayout) convertView.findViewById(R.id.away_team_logo_background);
        LinearLayout homeLogoBackground = (LinearLayout) convertView.findViewById(R.id.home_team_logo_background);

        statisticRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPredictionsRequest request = new AllPredictionsRequest();
                request.setGameid(expandedListItem.getGameid());
                getAllPredictionsForGameid(expandedListItem, request);
            }
        });

        awayLogoBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamColor()));
        homeLogoBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamColor()));

        awayTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamIcon());
        homeTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamIcon());

        homeScoreTextView.setText(String.valueOf(expandedListItem.getHomepoints()));
        awayScoreTextView.setText(String.valueOf(expandedListItem.getAwaypoints()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_view_group, parent, false);
        }

        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    private void getAllPredictionsForGameid(final Game game, AllPredictionsRequest request){
        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        Call<AllPredictionsResponse> response = requestInterface.allPredictions(request);

        response.enqueue(new Callback<AllPredictionsResponse>() {
            @Override
            public void onResponse(Call<AllPredictionsResponse> call, retrofit2.Response<AllPredictionsResponse> response) {
                AllPredictionsResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Intent intent = new Intent(activity, StatisticForGameActivity.class);
                    intent.putParcelableArrayListExtra(Constants.PREDICTIONLIST, resp.getPredictionList());
                    intent.putExtra(Constants.GAME, game);
                    activity.startActivity(intent);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllPredictionsResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.statisticsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
