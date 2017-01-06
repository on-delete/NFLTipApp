package com.andre.nfltipapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.model.UpdatePredictionRequest;
import com.andre.nfltipapp.model.UpdatePredictionResponse;
import com.andre.nfltipapp.rest.RequestInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PredictionsListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle = new ArrayList<>();
    private HashMap<String, List<Game>> expandableListDetail = new HashMap<>();
    private String uuid;

    public PredictionsListViewAdapter(Context context, List<Prediction> predictionList, String uuid) {
        this.context = context;
        this.uuid = uuid;

        for(Prediction predictionItem : predictionList){
            List<Game> tempGamesList = new ArrayList<>();

            for (Game game : predictionItem.getGames()){
                if(game.isFinished()==0 && Utils.getActualGameTimeInMilliSeconds(game.getGamedatetime()) >= System.currentTimeMillis()){
                    tempGamesList.add(game);
                }
            }

            if(tempGamesList.size()>0){
                String title = "Woche " + predictionItem.getWeek() + " in " + predictionItem.getType();
                this.expandableListTitle.add(title);
                this.expandableListDetail.put(title, tempGamesList);
            }
        }
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
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.predictions_list_item, null);
        }
        final CheckBox homeTeamCheckbox = (CheckBox) convertView
                .findViewById(R.id.home_team_checkbox);
        final CheckBox awayTeamCheckbox = (CheckBox) convertView
                .findViewById(R.id.away_team_checkbox);
        TextView homePrefixTextView = (TextView) convertView
                .findViewById(R.id.home_team_prefix_text);
        TextView awayPrefixTextView = (TextView) convertView
                .findViewById(R.id.away_team_prefix_text);

        homePrefixTextView.setText(expandedListItem.getHometeam());
        awayPrefixTextView.setText(expandedListItem.getAwayteam());

        homeTeamCheckbox.setChecked(false);
        awayTeamCheckbox.setChecked(false);

        if(expandedListItem.hasPredicted() == 1){
            if(expandedListItem.predictedHometeam() == 1){
                homeTeamCheckbox.setChecked(true);
            }
            else{
                awayTeamCheckbox.setChecked(true);
            }
        }

        homeTeamCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Utils.getActualGameTimeInMilliSeconds(expandedListItem.getGamedatetime()) < System.currentTimeMillis()){
                    if(isChecked){
                        homeTeamCheckbox.setChecked(false);
                    }
                    else {
                        homeTeamCheckbox.setChecked(true);
                    }
                    homeTeamCheckbox.setEnabled(false);
                    awayTeamCheckbox.setEnabled(false);
                }
                else{
                    if(isChecked) {
                        awayTeamCheckbox.setChecked(false);
                        updatePrediction(0, expandedListItem.getGameid(), uuid);
                        Log.d(Constants.TAG, "home checked!");
                    }
                    else{
                        if(!homeTeamCheckbox.isChecked()) {
                            updatePrediction(2, expandedListItem.getGameid(), uuid);
                            Log.d(Constants.TAG, "home unchecked!");
                        }
                    }
                }
            }
        });

        awayTeamCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Utils.getActualGameTimeInMilliSeconds(expandedListItem.getGamedatetime()) < System.currentTimeMillis()){
                    if(isChecked){
                        awayTeamCheckbox.setChecked(false);
                    }
                    else {
                        awayTeamCheckbox.setChecked(true);
                    }
                    homeTeamCheckbox.setEnabled(false);
                    awayTeamCheckbox.setEnabled(false);
                }
                else{
                    if(isChecked) {
                        homeTeamCheckbox.setChecked(false);
                        updatePrediction(1, expandedListItem.getGameid(), uuid);
                        Log.d(Constants.TAG, "away checked!");
                    }
                    else{
                        if(!awayTeamCheckbox.isChecked()) {
                            updatePrediction(2, expandedListItem.getGameid(), uuid);
                            Log.d(Constants.TAG, "away unchecked!");
                        }
                    }
                }
            }
        });

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
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_view_group, null);
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

    private void updatePrediction(int prediction, String gameId, String uuid){
        UpdatePredictionRequest request = new UpdatePredictionRequest();
        request.setGameid(gameId);
        request.setUuid(uuid);
        switch (prediction){
            case 0:{
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request);
                break;
            }
            case 1: {
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(false);
                sendUpdateRequest(request);
                break;
            }
            case 2: {
                request.setHasPredicted(false);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request);
                break;
            }
            default: break;
        }
    }

    private void sendUpdateRequest(UpdatePredictionRequest request){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        Call<UpdatePredictionResponse> response = requestInterface.updatePrediction(request);

        response.enqueue(new Callback<UpdatePredictionResponse>() {
            @Override
            public void onResponse(Call<UpdatePredictionResponse> call, retrofit2.Response<UpdatePredictionResponse> response) {
                UpdatePredictionResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Log.d(Constants.TAG, "prediction updated!");
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdatePredictionResponse> call, Throwable t) {
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
