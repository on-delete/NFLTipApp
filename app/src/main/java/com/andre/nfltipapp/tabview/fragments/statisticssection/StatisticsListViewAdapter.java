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
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.rest.Api;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.AllPredictionsBeforeSeasonRequest;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.AllPredictionsBeforeSeasonResponse;
import com.andre.nfltipapp.tabview.fragments.model.PredictionBeforeSeason;
import com.andre.nfltipapp.tabview.fragments.AllPredictionsForGameActivity;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsRequest;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsResponse;
import com.andre.nfltipapp.tabview.fragments.model.GamePrediction;
import com.andre.nfltipapp.tabview.fragments.model.PredictionsForWeek;
import com.andre.nfltipapp.rest.ApiInterface;
import com.andre.nfltipapp.tabview.fragments.AllPredictionsBeforeSeasonActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class StatisticsListViewAdapter extends BaseExpandableListAdapter {

    private String userId;

    private ApiInterface apiInterface;

    private Activity activity;
    private List<String> expandableListTitle = new ArrayList<>();
    private HashMap<String, List<?>> expandableListDetail = new HashMap<>();
    private List<?> child;
    private LayoutInflater layoutInflater;

    public StatisticsListViewAdapter(Activity activity, List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionPlus, String userId) {
        this.activity = activity;

        this.userId = userId;

        if(Utils.isPredictionTimeOver(predictionPlus.get(0).getFirstgamedate(), 0)){
            this.expandableListTitle.add("Tips vor der Saison");
            expandableListDetail.put("Tips vor der Saison", predictionPlus);
        }

        for(PredictionsForWeek predictionsForWeekItem : predictionsForWeekList){
            List<GamePrediction> tempGamesList = new ArrayList<>();

            for (GamePrediction gamePrediction : predictionsForWeekItem.getGamePredictions()){
                if(gamePrediction.isFinished()==1){
                    tempGamesList.add(gamePrediction);
                }
            }

            if(tempGamesList.size()>0){
                String title = "Woche " + predictionsForWeekItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) : "");
                this.expandableListTitle.add(title);
                this.expandableListDetail.put(title, tempGamesList);
            }
        }

        Collections.reverse(expandableListTitle);

        apiInterface = Api.getInstance(activity).getApiInterface();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));

        if(child.get(0) instanceof GamePrediction){
            return child.get(expandedListPosition);
        }
        else{
            for(int i = 0; i < child.size(); i++){
                PredictionBeforeSeason tempPrediction = (PredictionBeforeSeason) child.get(i);
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

        Object childView = getChild(listPosition, expandedListPosition);
        if(childView instanceof GamePrediction){
            return initPredictionView(convertView, parent, (GamePrediction) childView);
        }
        else {
            return initPredictionPlusView(convertView, parent, (PredictionBeforeSeason) childView);
        }
    }

    private View initPredictionView(View convertView, ViewGroup parent, final GamePrediction expandedListItem){
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

    private View initPredictionPlusView(View convertView, ViewGroup parent, PredictionBeforeSeason child) {
        convertView = layoutInflater.inflate(R.layout.predictions_plus_item, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.subitems_container);

        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.SUPERBOWL, child.getSuperbowl()), 0);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.AFC_WINNER, child.getAfcwinnerteam()), 1);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.NFC_WINNER, child.getNfcwinnerteam()), 2);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.BEST_OFFENSE, child.getBestoffenseteam()), 3);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.BEST_DEFENSE, child.getBestdefenseteam()), 4);

        return convertView;
    }

    private View initPredictionPlusSubView(ViewGroup parent, final Constants.PREDICTION_TYPE state, final String team){
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
                AllPredictionsBeforeSeasonRequest request = new AllPredictionsBeforeSeasonRequest();
                request.setPredictionType(state.toString());
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

        if(child.get(0) instanceof GamePrediction){
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

    private void getAllPredictionsForGameid(final GamePrediction gamePrediction, AllPredictionsRequest request){
        Call<AllPredictionsResponse> response = apiInterface.allPredictions(request);

        response.enqueue(new Callback<AllPredictionsResponse>() {
            @Override
            public void onResponse(Call<AllPredictionsResponse> call, retrofit2.Response<AllPredictionsResponse> response) {
                AllPredictionsResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Intent intent = new Intent(activity, AllPredictionsForGameActivity.class);
                    intent.putParcelableArrayListExtra(Constants.PREDICTIONS, resp.getGamePredictionForStatistic());
                    intent.putExtra(Constants.GAME, gamePrediction);
                    intent.putExtra(Constants.USERID, userId);
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

    private void getAllPredictionsPlusForState(final Constants.PREDICTION_TYPE state, final String teamName, AllPredictionsBeforeSeasonRequest request){
        Call<AllPredictionsBeforeSeasonResponse> response = apiInterface.allPredictionsPlus(request);

        response.enqueue(new Callback<AllPredictionsBeforeSeasonResponse>() {
            @Override
            public void onResponse(Call<AllPredictionsBeforeSeasonResponse> call, retrofit2.Response<AllPredictionsBeforeSeasonResponse> response) {
                AllPredictionsBeforeSeasonResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Intent intent = new Intent(activity, AllPredictionsBeforeSeasonActivity.class);
                    intent.putParcelableArrayListExtra(Constants.PREDICTIONS_BEFORE_SEASON, resp.getPredictionList());
                    intent.putExtra(Constants.TEAMNAME, teamName);
                    intent.putExtra(Constants.PREDICTION_TYPE_STRING, state.toString());
                    intent.putExtra(Constants.USERID, userId);
                    activity.startActivity(intent);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllPredictionsBeforeSeasonResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.statisticsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
