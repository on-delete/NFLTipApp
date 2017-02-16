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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.AllPredictionsPlusRequest;
import com.andre.nfltipapp.model.AllPredictionsPlusResponse;
import com.andre.nfltipapp.model.PredictionPlus;
import com.andre.nfltipapp.tabview.fragments.StatisticForGameActivity;
import com.andre.nfltipapp.model.AllPredictionsRequest;
import com.andre.nfltipapp.model.AllPredictionsResponse;
import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.rest.RequestInterface;
import com.andre.nfltipapp.tabview.fragments.StatisticForPredictionsPlusActivity;

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
    private HashMap<String, List<?>> expandableListDetail = new HashMap<>();
    private List<?> child;
    private LayoutInflater layoutInflater;
    private Object childView;

    public StatisticsListViewAdapter(Activity activity, List<Prediction> predictionList, List<PredictionPlus> predictionPlus) {
        this.activity = activity;

        this.expandableListTitle.add("Tips vor der Saison");
        expandableListDetail.put("Tips vor der Saison", predictionPlus);

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
        child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));

        if(child.get(0) instanceof Game){
            return child.get(expandedListPosition);
        }
        else{
            for(int i = 0; i < child.size(); i++){
                PredictionPlus tempPrediction = (PredictionPlus) child.get(i);
                if(tempPrediction.getUser().equals("default")){
                    return tempPrediction;
                }
            }
            return null;
        }
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) this.activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        childView = getChild(listPosition, expandedListPosition);
        if(childView instanceof Game){
            return initPredictionView(convertView, parent, (Game) childView);
        }
        else {
            return initPredictionPlusView(convertView, parent, (PredictionPlus) childView);
        }
    }

    private View initPredictionView(View convertView, ViewGroup parent, final Game expandedListItem){
        convertView = layoutInflater.inflate(R.layout.statistics_list_item, parent, false);

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

    private View initPredictionPlusView(View convertView, ViewGroup parent, PredictionPlus child) {
        convertView = layoutInflater.inflate(R.layout.predictions_plus_item, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.subitems_container);

        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.SUPERBOWL, child.getSuperbowl()), 0);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.AFC_WINNER, child.getAfcwinnerteam()), 1);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.NFC_WINNER, child.getNfcwinnerteam()), 2);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.BEST_OFFENSE, child.getBestoffenseteam()), 3);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.BEST_DEFENSE, child.getBestdefenseteam()), 4);

        return convertView;
    }

    private View initPredictionPlusSubView(ViewGroup parent, final Constants.PREDICTIONS_PLUS_STATES state, final String team){
        View subView = layoutInflater.inflate(R.layout.predictions_plus_statistic_subitem, parent, false);

        final LinearLayout teamBackground = (LinearLayout) subView.findViewById(R.id.team_background);

        TextView stateText = (TextView) subView.findViewById(R.id.state_text);
        TextView teamText = (TextView) subView.findViewById(R.id.team_text);
        ImageView teamIcon = (ImageView) subView.findViewById(R.id.team_icon);

        setTeamInfos(team, teamBackground, teamIcon, teamText);

        switch (state) {
            case SUPERBOWL: {
                stateText.setText(R.string.superbowl);
                break;
            }
            case AFC_WINNER: {
                stateText.setText(R.string.afc_winner);
                break;
            }
            case NFC_WINNER: {
                stateText.setText(R.string.nfc_winner);
                break;
            }
            case BEST_OFFENSE: {
                stateText.setText(R.string.best_offense);
                break;
            }
            case BEST_DEFENSE: {
                stateText.setText(R.string.best_defense);
                break;
            }
            default:
                break;
        }

        teamBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPredictionsPlusRequest request = new AllPredictionsPlusRequest();
                request.setState(state.toString());
                getAllPredictionsPlusForState(state, team, request);
            }
        });

        return subView;
    }

    private void setTeamInfos(String team, LinearLayout teamBackground, ImageView teamIcon, TextView teamText){
        if(team.equals("")){
            teamBackground.setBackgroundColor(Color.parseColor("#BFBFBF"));
            teamIcon.setImageResource(R.drawable.default_icon);
            teamText.setText("-");
        }
        else {
            teamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(team).getTeamColor()));
            teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(team).getTeamIcon());
            teamText.setText(Constants.TEAM_INFO_MAP.get(team).getTeamName());
        }
    }

    @Override
    public int getChildrenCount(int listPosition) {
        child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));

        if(child.get(0) instanceof Game){
            return child.size();
        }
        else{
            return 1;
        }
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

    private void getAllPredictionsPlusForState(final Constants.PREDICTIONS_PLUS_STATES state, final String teamName, AllPredictionsPlusRequest request){
        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        Call<AllPredictionsPlusResponse> response = requestInterface.allPredictionsPlus(request);

        response.enqueue(new Callback<AllPredictionsPlusResponse>() {
            @Override
            public void onResponse(Call<AllPredictionsPlusResponse> call, retrofit2.Response<AllPredictionsPlusResponse> response) {
                AllPredictionsPlusResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Intent intent = new Intent(activity, StatisticForPredictionsPlusActivity.class);
                    intent.putParcelableArrayListExtra(Constants.PREDICTIONSPLUSLIST, resp.getPredictionList());
                    intent.putExtra(Constants.TEAMNAME, teamName);
                    intent.putExtra(Constants.STATE, state.toString());
                    activity.startActivity(intent);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllPredictionsPlusResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.statisticsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
