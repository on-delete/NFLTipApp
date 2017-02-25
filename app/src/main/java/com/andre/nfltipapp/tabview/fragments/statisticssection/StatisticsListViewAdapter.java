package com.andre.nfltipapp.tabview.fragments.statisticssection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.andre.nfltipapp.Constants.PREDICTION_TYPE;

class StatisticsListViewAdapter extends BaseExpandableListAdapter {

    private ApiInterface apiInterface;

    private Activity activity;
    private LayoutInflater layoutInflater;

    private List<String> statisticListHeaders = new ArrayList<>();
    private HashMap<String, List<?>> statisticListItems = new HashMap<>();
    private String userId;

    StatisticsListViewAdapter(Activity activity, List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList, String userId) {
        this.activity = activity;
        this.userId = userId;

        apiInterface = Api.getInstance(activity).getApiInterface();

        initStatisticListItems(predictionsForWeekList, predictionBeforeSeasonList);
    }

    private void initStatisticListItems(List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList){
        if(Utils.isPredictionTimeOver(predictionBeforeSeasonList.get(0).getFirstgamedate(), 0)){
            this.statisticListHeaders.add(Constants.PREDICTION_BEFORE_SEASON);
            statisticListItems.put(Constants.PREDICTION_BEFORE_SEASON, predictionBeforeSeasonList);
        }

        for(PredictionsForWeek predictionsForWeekItem : predictionsForWeekList){
            List<GamePrediction> tempGamesList = new ArrayList<>();

            for (GamePrediction gamePrediction : predictionsForWeekItem.getGamePredictions()){
                if(gamePrediction.isFinished()==1){
                    tempGamesList.add(gamePrediction);
                }
            }

            if(tempGamesList.size()>0){
                String title = Constants.WEEK + predictionsForWeekItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) : "");
                this.statisticListHeaders.add(title);
                this.statisticListItems.put(title, tempGamesList);
            }
        }

        Collections.reverse(statisticListHeaders);
    }

    @Override
    public Object getChild(int listPosition, int statisticListPosition) {
        List<?> genericList = this.statisticListItems.get(this.statisticListHeaders.get(listPosition));

        if(genericList.get(0) instanceof GamePrediction){
            return genericList.get(statisticListPosition);
        }
        else{
            for(int i = 0; i < genericList.size(); i++){
                PredictionBeforeSeason tempPrediction = (PredictionBeforeSeason) genericList.get(i);
                if(tempPrediction.getUser().equals("default")){
                    return tempPrediction;
                }
            }
            return null;
        }
    }

    @Override
    public long getChildId(int listPosition, int statisticListPosition) {
        return statisticListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int statisticListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) this.activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Object listItem = getChild(listPosition, statisticListPosition);
        if(listItem instanceof GamePrediction){
            return initPredictionView(parent, (GamePrediction) listItem);
        }
        else {
            return initPredictionBeforeSeasonView(parent, (PredictionBeforeSeason) listItem);
        }
    }

    private View initPredictionView(ViewGroup parent, final GamePrediction gamePrediction){
        View convertView = layoutInflater.inflate(R.layout.list_item_statistic_prediction_for_game, parent, false);

        TextView tvHomeScore = (TextView) convertView
                .findViewById(R.id.text_home_team_score);
        TextView tvAwayScore = (TextView) convertView
                .findViewById(R.id.text_away_team_score);

        LinearLayout llStatisticsListItem = (LinearLayout)  convertView.findViewById(R.id.linear_statistic_list_item);
        ImageView ivAwayTeamIcon = (ImageView) convertView.findViewById(R.id.image_away_team);
        ImageView ivHomeTeamIcon = (ImageView) convertView.findViewById(R.id.image_home_team);
        LinearLayout llAwayTeamBackground = (LinearLayout) convertView.findViewById(R.id.linear_background_team_away);
        LinearLayout llHomeTeamBackground = (LinearLayout) convertView.findViewById(R.id.linear_background_team_home);

        llStatisticsListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPredictionsRequest request = new AllPredictionsRequest();
                request.setGameid(gamePrediction.getGameid());
                getAllPredictionsForGameId(gamePrediction, request);
            }
        });

        llAwayTeamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamColor()));
        llHomeTeamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamColor()));

        ivAwayTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamIcon());
        ivHomeTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamIcon());

        tvHomeScore.setText(String.valueOf(gamePrediction.getHomepoints()));
        tvAwayScore.setText(String.valueOf(gamePrediction.getAwaypoints()));

        return convertView;
    }

    private View initPredictionBeforeSeasonView(ViewGroup parent, PredictionBeforeSeason predictionBeforeSeason) {
        View convertView = layoutInflater.inflate(R.layout.list_item_prediction_before_season, parent, false);
        LinearLayout llContainer = (LinearLayout) convertView.findViewById(R.id.linear_subitems_container);

        llContainer.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.SUPERBOWL, predictionBeforeSeason.getSuperbowlTeam()), 0);
        llContainer.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.AFC_WINNER, predictionBeforeSeason.getAfcwinnerteam()), 1);
        llContainer.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.NFC_WINNER, predictionBeforeSeason.getNfcwinnerteam()), 2);
        llContainer.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.BEST_OFFENSE, predictionBeforeSeason.getBestoffenseteam()), 3);
        llContainer.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.BEST_DEFENSE, predictionBeforeSeason.getBestdefenseteam()), 4);

        return convertView;
    }

    private View initPredictionBeforeSeasonSubView(ViewGroup parent, final PREDICTION_TYPE predictionType, final String team){
        View subView = layoutInflater.inflate(R.layout.list_subitem_statistics_prediction_before_season, parent, false);

        final LinearLayout llTeamBackground = (LinearLayout) subView.findViewById(R.id.linear_team_background);

        TextView tvPredictionType = (TextView) subView.findViewById(R.id.text_prediction_type);
        TextView tvTeamName = (TextView) subView.findViewById(R.id.text_team_name);
        ImageView ivTeamIcon = (ImageView) subView.findViewById(R.id.image_team_icon);

        setTeamInfos(team, llTeamBackground, ivTeamIcon, tvTeamName);

        setPredictionTypeText(predictionType, tvPredictionType);

        llTeamBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllPredictionsBeforeSeasonRequest request = new AllPredictionsBeforeSeasonRequest();
                request.setPredictionType(predictionType.toString());
                getAllPredictionsBeforeSeasonForPredictionType(predictionType, team, request);
            }
        });

        return subView;
    }

    private void setPredictionTypeText(PREDICTION_TYPE predictionType, TextView tvPredictionType){
        switch (predictionType) {
            case SUPERBOWL: {
                tvPredictionType.setText(R.string.superbowl);
                break;
            }
            case AFC_WINNER: {
                tvPredictionType.setText(R.string.afc_winner);
                break;
            }
            case NFC_WINNER: {
                tvPredictionType.setText(R.string.nfc_winner);
                break;
            }
            case BEST_OFFENSE: {
                tvPredictionType.setText(R.string.best_offense);
                break;
            }
            case BEST_DEFENSE: {
                tvPredictionType.setText(R.string.best_defense);
                break;
            }
            default:
                break;
        }
    }

    private void setTeamInfos(String team, LinearLayout llTeamBackground, ImageView ivTeamIcon, TextView tvTeamName){
        if(team.equals("")){
            llTeamBackground.setBackgroundColor(Color.parseColor("#BFBFBF"));
            ivTeamIcon.setImageResource(R.drawable.ic_default_icon);
            tvTeamName.setText("-");
        }
        else {
            llTeamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(team).getTeamColor()));
            ivTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(team).getTeamIcon());
            tvTeamName.setText(Constants.TEAM_INFO_MAP.get(team).getTeamName());
        }
    }

    @Override
    public int getChildrenCount(int listPosition) {
        List<?> genericList = this.statisticListItems.get(this.statisticListHeaders.get(listPosition));

        if(genericList.get(0) instanceof GamePrediction){
            return genericList.size();
        }
        else{
            return 1;
        }
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.statisticListHeaders.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.statisticListHeaders.size();
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
            convertView = layoutInflater.inflate(R.layout.list_header_view, parent, false);
        }

        TextView llStatisticListHeader = (TextView) convertView
                .findViewById(R.id.text_list_header);
        llStatisticListHeader.setText(listTitle);

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

    private void getAllPredictionsForGameId(final GamePrediction gamePrediction, AllPredictionsRequest request){
        Call<AllPredictionsResponse> response = apiInterface.allPredictions(request);

        response.enqueue(new Callback<AllPredictionsResponse>() {
            @Override
            public void onResponse(Call<AllPredictionsResponse> call, retrofit2.Response<AllPredictionsResponse> response) {
                AllPredictionsResponse resp = response.body();
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(activity.findViewById(R.id.list_view_statistics) , "Server error!", Snackbar.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(activity, AllPredictionsForGameActivity.class);
                    intent.putParcelableArrayListExtra(Constants.PREDICTIONS, resp.getGamePredictionForStatistic());
                    intent.putExtra(Constants.GAME, gamePrediction);
                    intent.putExtra(Constants.USERID, userId);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<AllPredictionsResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.list_view_statistics) , "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void getAllPredictionsBeforeSeasonForPredictionType(final PREDICTION_TYPE predictionType, final String teamName, AllPredictionsBeforeSeasonRequest request){
        Call<AllPredictionsBeforeSeasonResponse> response = apiInterface.allPredictionsPlus(request);

        response.enqueue(new Callback<AllPredictionsBeforeSeasonResponse>() {
            @Override
            public void onResponse(Call<AllPredictionsBeforeSeasonResponse> call, retrofit2.Response<AllPredictionsBeforeSeasonResponse> response) {
                AllPredictionsBeforeSeasonResponse resp = response.body();
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(activity.findViewById(R.id.list_view_statistics) , "Server error!", Snackbar.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(activity, AllPredictionsBeforeSeasonActivity.class);
                    intent.putParcelableArrayListExtra(Constants.PREDICTIONS_BEFORE_SEASON, resp.getPredictionList());
                    intent.putExtra(Constants.TEAMNAME, teamName);
                    intent.putExtra(Constants.PREDICTION_TYPE_STRING, predictionType.toString());
                    intent.putExtra(Constants.USERID, userId);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<AllPredictionsBeforeSeasonResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.list_view_statistics) , "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
