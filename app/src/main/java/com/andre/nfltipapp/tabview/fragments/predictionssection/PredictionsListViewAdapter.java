package com.andre.nfltipapp.tabview.fragments.predictionssection;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.model.AllPredictionsRequest;
import com.andre.nfltipapp.model.AllPredictionsResponse;
import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.model.PredictionPlus;
import com.andre.nfltipapp.model.UpdatePredictionRequest;
import com.andre.nfltipapp.model.UpdatePredictionResponse;
import com.andre.nfltipapp.rest.RequestInterface;
import com.andre.nfltipapp.tabview.fragments.StatisticForGameActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andre.nfltipapp.Constants.UPDATE_STATES;

public class PredictionsListViewAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private List<String> expandableListTitle = new ArrayList<>();
    private HashMap<String, Object> expandableListDetail = new HashMap<>();
    private String uuid;

    public PredictionsListViewAdapter(Activity activity, List<Prediction> predictionList, PredictionPlus predictionPlus, String uuid) {
        this.activity = activity;
        this.uuid = uuid;

        this.expandableListTitle.add("Tips vor der Saison");
        expandableListDetail.put("Tips vor der Saison", predictionPlus);

        for(Prediction predictionItem : predictionList){
            List<Game> tempGamesList = new ArrayList<>();

            for (Game game : predictionItem.getGames()){
                if(game.isFinished()==0){
                    tempGamesList.add(game);
                }
            }

            if(tempGamesList.size()>0){
                String title = "Woche " + predictionItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionItem.getType()) : "");
                this.expandableListTitle.add(title);
                this.expandableListDetail.put(title, tempGamesList);
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        Object child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
        if (child instanceof List<?>){
            List<Game> tempChild = (List<Game>) child;
            return tempChild.get(expandedListPosition);
        }
        else{
            return child;
        }
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) this.activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Object child = getChild(listPosition, expandedListPosition);
        if(child instanceof Game){
            return initPredictionView(convertView, layoutInflater, parent, (Game) child);
        }
        else {
            return initPredictionPlusView(convertView, layoutInflater, parent, (PredictionPlus) child);
        }
    }

    private View initPredictionView(View convertView, LayoutInflater layoutInflater, ViewGroup parent, final Game expandedListItem){
        convertView = layoutInflater.inflate(R.layout.predictions_list_item, parent, false);
        final LinearLayout disableOverlay = (LinearLayout) convertView.findViewById(R.id.disable_overlay);
        final CheckBox homeTeamCheckbox = (CheckBox) convertView
                .findViewById(R.id.home_team_checkbox);
        final CheckBox awayTeamCheckbox = (CheckBox) convertView
                .findViewById(R.id.away_team_checkbox);
        ImageView awayTeamIcon = (ImageView) convertView.findViewById(R.id.icon_away_team);
        ImageView homeTeamIcon = (ImageView) convertView.findViewById(R.id.icon_home_team);
        LinearLayout awayLogoBackground = (LinearLayout) convertView.findViewById(R.id.away_team_logo_background);
        LinearLayout homeLogoBackground = (LinearLayout) convertView.findViewById(R.id.home_team_logo_background);

        awayLogoBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamColor()));
        homeLogoBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamColor()));

        awayTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamIcon());
        homeTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamIcon());

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

        if(Utils.isPredictionTimeOver(expandedListItem.getGamedatetime())){
            homeTeamCheckbox.setEnabled(false);
            awayTeamCheckbox.setEnabled(false);

            disableOverlay.setBackgroundColor(Color.parseColor("#B2B2B2"));
            disableOverlay.getBackground().setAlpha(150);

            disableOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AllPredictionsRequest request = new AllPredictionsRequest();
                    request.setGameid(expandedListItem.getGameid());
                    getAllPredictionsForGameid(expandedListItem, request);
                }
            });
        }

        homeTeamCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox clickedBox = (CheckBox) buttonView;

                if(Utils.isPredictionTimeOver(expandedListItem.getGamedatetime())){
                    if(isChecked){
                        clickedBox.setChecked(false);
                    }
                    else {
                        clickedBox.setChecked(true);
                    }
                    clickedBox.setEnabled(false);
                    awayTeamCheckbox.setEnabled(false);

                    disableOverlay.setBackgroundColor(Color.parseColor("#B2B2B2"));
                    disableOverlay.getBackground().setAlpha(150);

                    disableOverlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllPredictionsRequest request = new AllPredictionsRequest();
                            request.setGameid(expandedListItem.getGameid());
                            getAllPredictionsForGameid(expandedListItem, request);
                        }
                    });
                }
                else{
                    if(isChecked) {
                        updatePrediction(clickedBox, awayTeamCheckbox, UPDATE_STATES.HOME_TEAM_SELECTED, expandedListItem, uuid);
                    }
                    else{
                        if(!awayTeamCheckbox.isChecked()) {
                            updatePrediction(clickedBox, awayTeamCheckbox, UPDATE_STATES.UNPREDICTED, expandedListItem, uuid);
                        }
                    }
                }
            }
        });

        awayTeamCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox clickedBox = (CheckBox) buttonView;

                if(Utils.isPredictionTimeOver(expandedListItem.getGamedatetime())){
                    if(isChecked){
                        clickedBox.setChecked(false);
                    }
                    else {
                        clickedBox.setChecked(true);
                    }
                    homeTeamCheckbox.setEnabled(false);
                    clickedBox.setEnabled(false);

                    disableOverlay.setBackgroundColor(Color.parseColor("#B2B2B2"));
                    disableOverlay.getBackground().setAlpha(150);

                    disableOverlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllPredictionsRequest request = new AllPredictionsRequest();
                            request.setGameid(expandedListItem.getGameid());
                            getAllPredictionsForGameid(expandedListItem, request);
                        }
                    });
                }
                else{
                    if(isChecked) {
                        updatePrediction(homeTeamCheckbox, clickedBox, UPDATE_STATES.AWAY_TEAM_SELECTED, expandedListItem, uuid);
                    }
                    else{
                        if(!homeTeamCheckbox.isChecked()) {
                            updatePrediction(homeTeamCheckbox, clickedBox, UPDATE_STATES.UNPREDICTED, expandedListItem, uuid);
                        }
                    }
                }
            }
        });

        return convertView;
    }

    private View initPredictionPlusView(View convertView, LayoutInflater layoutInflater, ViewGroup parent, PredictionPlus child) {
        ArrayList<String> teamPrefixList = new ArrayList<>();
        ArrayList<String> teamNameList = new ArrayList<>();
        teamNameList.add("");
        for(String key : Constants.TEAM_INFO_MAP.keySet()){
            teamPrefixList.add(key);
            teamNameList.add(Constants.TEAM_INFO_MAP.get(key).getTeamName());
        }

        convertView = layoutInflater.inflate(R.layout.predictions_plus_item, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.subitems_container);

        container.addView(initPredictionPlusSubView(parent, layoutInflater, Constants.PREDICTIONS_PLUS_STATES.SUPERBOWL, child.getSuperbowl(), teamPrefixList, teamNameList), 0);
        container.addView(initPredictionPlusSubView(parent, layoutInflater, Constants.PREDICTIONS_PLUS_STATES.AFC_WINNER, child.getAfcwinnerteam(), teamPrefixList, teamNameList), 1);
        container.addView(initPredictionPlusSubView(parent, layoutInflater, Constants.PREDICTIONS_PLUS_STATES.NFC_WINNER, child.getNfcwinnerteam(), teamPrefixList, teamNameList), 2);
        container.addView(initPredictionPlusSubView(parent, layoutInflater, Constants.PREDICTIONS_PLUS_STATES.BEST_OFFENSE, child.getBestoffenseteam(), teamPrefixList, teamNameList), 3);
        container.addView(initPredictionPlusSubView(parent, layoutInflater, Constants.PREDICTIONS_PLUS_STATES.BEST_DEFENSE, child.getBestdefenseteam(), teamPrefixList, teamNameList), 4);

        return convertView;
    }

    private View initPredictionPlusSubView(ViewGroup parent, LayoutInflater layoutInflater, Constants.PREDICTIONS_PLUS_STATES state, String team, final ArrayList<String> teamPrefixList, ArrayList<String> teamNameList){
        View subView = layoutInflater.inflate(R.layout.predictions_plus_subitem, parent, false);

        final LinearLayout teamBackground = (LinearLayout) subView.findViewById(R.id.team_background);

        TextView teamText = (TextView) subView.findViewById(R.id.team_text);
        switch (state) {
            case SUPERBOWL: {
                teamText.setText(R.string.superbowl);
                break;
            }
            case AFC_WINNER: {
                teamText.setText(R.string.afc_winner);
                break;
            }
            case NFC_WINNER: {
                teamText.setText(R.string.nfc_winner);
                break;
            }
            case BEST_OFFENSE: {
                teamText.setText(R.string.best_offense);
                break;
            }
            case BEST_DEFENSE: {
                teamText.setText(R.string.best_defense);
                break;
            }
        }

        Spinner teamSpinner = (Spinner) subView.findViewById(R.id.team_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.activity, R.layout.spinner_item, teamNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(adapter);

        final ImageView teamIcon = (ImageView) subView.findViewById(R.id.team_icon);

        if(!team.equals("")){
            teamSpinner.setSelection(teamPrefixList.indexOf(team) + 1);
            teamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(team).getTeamColor()));
            teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(team).getTeamIcon());
        }
        else{
            teamSpinner.setSelection(0);
            teamBackground.setBackgroundColor(Color.parseColor("#BFBFBF"));
            teamIcon.setImageResource(R.drawable.default_icon);
        }

        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    teamBackground.setBackgroundColor(Color.parseColor("#BFBFBF"));
                    teamIcon.setImageResource(R.drawable.default_icon);
                }
                else {
                    teamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position - 1)).getTeamColor()));
                    teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position - 1)).getTeamIcon());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return subView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        Object child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
        if (child instanceof List<?>){
            List<Game> tempChild = (List<Game>) child;
            return tempChild.size();
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

    private void updatePrediction(CheckBox homeTeamCheckbox, CheckBox awayTeamCheckbox, UPDATE_STATES state, Game game, String uuid){
        UpdatePredictionRequest request = new UpdatePredictionRequest();
        request.setGameid(game.getGameid());
        request.setUuid(uuid);
        switch (state){
            case HOME_TEAM_SELECTED:{
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request, homeTeamCheckbox, awayTeamCheckbox, game, state);
                break;
            }
            case AWAY_TEAM_SELECTED: {
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(false);
                sendUpdateRequest(request, homeTeamCheckbox, awayTeamCheckbox, game, state);
                break;
            }
            case UNPREDICTED: {
                request.setHasPredicted(false);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request, homeTeamCheckbox, awayTeamCheckbox, game, state);
                break;
            }
            default: break;
        }
    }

    private void sendUpdateRequest(UpdatePredictionRequest request, final CheckBox homeTeamCheckbox, final CheckBox awayTeamCheckbox, final Game game, final UPDATE_STATES state){
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
                    updateModel(homeTeamCheckbox, awayTeamCheckbox, game, state);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdatePredictionResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void updateModel(CheckBox homeTeamCheckbox, CheckBox awayTeamCheckbox, Game game, UPDATE_STATES state){
        switch (state){
            case HOME_TEAM_SELECTED:{
                game.setHaspredicted(1);
                game.setPredictedhometeam(1);
                awayTeamCheckbox.setChecked(false);
                break;
            }
            case AWAY_TEAM_SELECTED: {
                game.setHaspredicted(1);
                game.setPredictedhometeam(0);
                homeTeamCheckbox.setChecked(false);
                break;
            }
            case UNPREDICTED: {
                game.setHaspredicted(0);
                game.setPredictedhometeam(0);
                homeTeamCheckbox.setChecked(false);
                awayTeamCheckbox.setChecked(false);
                break;
            }
            default: break;
        }
    }

    private void getAllPredictionsForGameid(final Game game, AllPredictionsRequest request){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
