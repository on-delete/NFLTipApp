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
import com.andre.nfltipapp.rest.Api;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsRequest;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsResponse;
import com.andre.nfltipapp.tabview.fragments.model.GamePrediction;
import com.andre.nfltipapp.tabview.fragments.model.PredictionsForWeek;
import com.andre.nfltipapp.tabview.fragments.model.PredictionBeforeSeason;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.TeamInfoSpinnerObject;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdatePredictionBeforeSeasonRequest;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdatePredictionRequest;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdateResponse;
import com.andre.nfltipapp.rest.ApiInterface;
import com.andre.nfltipapp.tabview.fragments.AllPredictionsForGameActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.andre.nfltipapp.Constants.UPDATE_STATES;

public class PredictionsListViewAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private List<String> expandableListTitle = new ArrayList<>();
    private HashMap<String, List<?>> expandableListDetail = new HashMap<>();
    private String userId;
    private ApiInterface apiInterface;
    private List<?> child;
    private LayoutInflater layoutInflater;
    private ArrayList<TeamInfoSpinnerObject> teamInfoList = new ArrayList<>();
    private ArrayList<String> teamPrefixList = new ArrayList<>();

    private int lastSuperbowlSpinnerPosition = 0;
    private int lastAFCSpinnerPosition = 0;
    private int lastNFCSpinnerPosition = 0;
    private int lastOffenseSpinnerPosition = 0;
    private int lastDefenseSpinnerPosition = 0;
    private boolean lastHomeTeamCheckboxStatus = false;
    private boolean lastAwayTeamCheckboxStatus = false;
    private int offsetPredictionTime = -30;
    private int offsetPredictionPlusTime = 0;
    private boolean userInteraction = true;
    private boolean userInteractionHomeCheckbox = true;
    private boolean userInteractionAwayCheckbox = true;

    public PredictionsListViewAdapter(Activity activity, List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionPlus, String userId) {
        this.activity = activity;
        this.userId = userId;

        if(!Utils.isPredictionTimeOver(predictionPlus.get(0).getFirstgamedate(), 0)){
            this.expandableListTitle.add("Tips vor der Saison");
            expandableListDetail.put("Tips vor der Saison", predictionPlus);
        }

        for(PredictionsForWeek predictionsForWeekItem : predictionsForWeekList){
            List<GamePrediction> tempGamesList = new ArrayList<>();

            for (GamePrediction gamePrediction : predictionsForWeekItem.getGamePredictions()){
                if(gamePrediction.isFinished()==0){
                    tempGamesList.add(gamePrediction);
                }
            }

            if(tempGamesList.size()>0){
                String title = "Woche " + predictionsForWeekItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) : "");
                this.expandableListTitle.add(title);
                this.expandableListDetail.put(title, tempGamesList);
            }
        }

        apiInterface = Api.getInstance(activity).getApiInterface();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));

        if(child.get(0) instanceof GamePrediction){
            return child.get(expandedListPosition);
        }
        else{
            PredictionBeforeSeason returnPredictionBeforeSeason = null;

            for(int i = 0; i < child.size(); i++){
                PredictionBeforeSeason tempPrediction = (PredictionBeforeSeason) child.get(i);
                if(tempPrediction.getUser().equals("user")){
                    returnPredictionBeforeSeason = tempPrediction;
                    break;
                }
            }
            return returnPredictionBeforeSeason;
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

        lastHomeTeamCheckboxStatus = homeTeamCheckbox.isChecked();
        lastAwayTeamCheckboxStatus = awayTeamCheckbox.isChecked();

        if(Utils.isPredictionTimeOver(expandedListItem.getGamedatetime(), offsetPredictionTime)){
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

                if(Utils.isPredictionTimeOver(expandedListItem.getGamedatetime(), offsetPredictionTime)){
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
                    if(userInteractionHomeCheckbox){
                        userInteractionAwayCheckbox = false;
                        if(isChecked) {
                            awayTeamCheckbox.setChecked(false);
                            updatePrediction(clickedBox, awayTeamCheckbox, UPDATE_STATES.HOME_TEAM_SELECTED, expandedListItem, userId);
                        }
                        else{
                            updatePrediction(clickedBox, awayTeamCheckbox, UPDATE_STATES.UNPREDICTED, expandedListItem, userId);
                        }
                    }
                    userInteractionAwayCheckbox = true;
                }
            }
        });

        awayTeamCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox clickedBox = (CheckBox) buttonView;

                if(Utils.isPredictionTimeOver(expandedListItem.getGamedatetime(), offsetPredictionTime)){
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
                    if(userInteractionAwayCheckbox){
                        userInteractionHomeCheckbox = false;
                        if (isChecked) {
                            homeTeamCheckbox.setChecked(false);
                            updatePrediction(homeTeamCheckbox, clickedBox, UPDATE_STATES.AWAY_TEAM_SELECTED, expandedListItem, userId);
                        } else {
                            updatePrediction(homeTeamCheckbox, clickedBox, UPDATE_STATES.UNPREDICTED, expandedListItem, userId);
                        }
                    }
                    userInteractionHomeCheckbox = true;
                }
            }
        });

        return convertView;
    }

    private View initPredictionPlusView(View convertView, ViewGroup parent, PredictionBeforeSeason child) {
        if(teamInfoList.isEmpty() && teamPrefixList.isEmpty()){
            for (String key : Constants.TEAM_INFO_MAP.keySet()) {
                teamInfoList.add(new TeamInfoSpinnerObject(Constants.TEAM_INFO_MAP.get(key).getTeamName(), key));
            }

            Collections.sort(teamInfoList);

            teamInfoList.add(0, new TeamInfoSpinnerObject("", ""));

            for(TeamInfoSpinnerObject object : teamInfoList){
                teamPrefixList.add(object.getTeamPrefix());
            }
        }

        convertView = layoutInflater.inflate(R.layout.predictions_plus_item, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.subitems_container);

        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.SUPERBOWL, child.getSuperbowl(), child), 0);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.AFC_WINNER, child.getAfcwinnerteam(), child), 1);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.NFC_WINNER, child.getNfcwinnerteam(), child), 2);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.BEST_OFFENSE, child.getBestoffenseteam(), child), 3);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTION_TYPE.BEST_DEFENSE, child.getBestdefenseteam(), child), 4);

        return convertView;
    }

    private View initPredictionPlusSubView(ViewGroup parent, final Constants.PREDICTION_TYPE state, String team, final PredictionBeforeSeason predictionBeforeSeason){
        View subView = layoutInflater.inflate(R.layout.predictions_plus_subitem, parent, false);

        final LinearLayout teamBackground = (LinearLayout) subView.findViewById(R.id.team_background);

        TextView teamText = (TextView) subView.findViewById(R.id.team_text);
        Spinner teamSpinner = (Spinner) subView.findViewById(R.id.team_spinner);

        TeamPickSpinnerAdapter adapter = new TeamPickSpinnerAdapter(this.activity, R.layout.spinner_item, teamInfoList);
        teamSpinner.setAdapter(adapter);

        final ImageView teamIcon = (ImageView) subView.findViewById(R.id.team_icon);

        if(!team.equals("")){
            teamSpinner.setSelection(teamPrefixList.indexOf(team), false);
        }
        else{
            teamSpinner.setSelection(0, false);
        }
        setTeamInfos(teamSpinner.getSelectedItemPosition(), teamBackground, teamIcon);

        switch (state) {
            case SUPERBOWL: {
                teamText.setText(R.string.superbowl);
                lastSuperbowlSpinnerPosition = teamSpinner.getSelectedItemPosition();
                break;
            }
            case AFC_WINNER: {
                teamText.setText(R.string.afc_winner);
                lastAFCSpinnerPosition = teamSpinner.getSelectedItemPosition();
                break;
            }
            case NFC_WINNER: {
                teamText.setText(R.string.nfc_winner);
                lastNFCSpinnerPosition = teamSpinner.getSelectedItemPosition();
                break;
            }
            case BEST_OFFENSE: {
                teamText.setText(R.string.best_offense);
                lastOffenseSpinnerPosition = teamSpinner.getSelectedItemPosition();
                break;
            }
            case BEST_DEFENSE: {
                teamText.setText(R.string.best_defense);
                lastDefenseSpinnerPosition = teamSpinner.getSelectedItemPosition();
                break;
            }
            default:
                break;
        }

        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!userInteraction){
                    userInteraction = true;
                    return;
                }
                if(Utils.isPredictionTimeOver(predictionBeforeSeason.getFirstgamedate(), offsetPredictionPlusTime)){
                    Spinner spinner = (Spinner) parent;
                    switch (state) {
                        case SUPERBOWL: {
                            spinner.setSelection(lastSuperbowlSpinnerPosition);
                            break;
                        }
                        case AFC_WINNER: {
                            spinner.setSelection(lastAFCSpinnerPosition);
                            break;
                        }
                        case NFC_WINNER: {
                            spinner.setSelection(lastNFCSpinnerPosition);
                            break;
                        }
                        case BEST_OFFENSE: {
                            spinner.setSelection(lastOffenseSpinnerPosition);
                            break;
                        }
                        case BEST_DEFENSE: {
                            spinner.setSelection(lastDefenseSpinnerPosition);
                            break;
                        }
                        default:
                            break;
                    }
                    Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Zusatztips sind jetzt gesperrt!", Snackbar.LENGTH_LONG).show();
                }
                else {
                    sendUpdateRequest(state, position, (Spinner) parent, teamBackground, teamIcon, predictionBeforeSeason);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return subView;
    }

    private void setTeamInfos(int position, LinearLayout teamBackground, ImageView teamIcon){
        if(position == 0){
            teamBackground.setBackgroundColor(Color.parseColor("#BFBFBF"));
            teamIcon.setImageResource(R.drawable.default_icon);
        }
        else {
            teamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position)).getTeamColor()));
            teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position)).getTeamIcon());
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

    private void updatePrediction(CheckBox homeTeamCheckbox, CheckBox awayTeamCheckbox, UPDATE_STATES state, GamePrediction gamePrediction, String uuid){
        UpdatePredictionRequest request = new UpdatePredictionRequest();
        request.setGameId(gamePrediction.getGameid());
        request.setUserId(uuid);
        switch (state){
            case HOME_TEAM_SELECTED:{
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request, homeTeamCheckbox, awayTeamCheckbox, gamePrediction, state);
                break;
            }
            case AWAY_TEAM_SELECTED: {
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(false);
                sendUpdateRequest(request, homeTeamCheckbox, awayTeamCheckbox, gamePrediction, state);
                break;
            }
            case UNPREDICTED: {
                request.setHasPredicted(false);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request, homeTeamCheckbox, awayTeamCheckbox, gamePrediction, state);
                break;
            }
            default: break;
        }
    }

    private void sendUpdateRequest(UpdatePredictionRequest request, final CheckBox homeTeamCheckbox, final CheckBox awayTeamCheckbox, final GamePrediction gamePrediction, final UPDATE_STATES state){
        Call<UpdateResponse> response = this.apiInterface.updatePrediction(request);

        response.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {
                UpdateResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    updateModel(homeTeamCheckbox, awayTeamCheckbox, gamePrediction, state);
                }
                else{
                    setCheckboxesToLastValue(homeTeamCheckbox, awayTeamCheckbox, state);
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                setCheckboxesToLastValue(homeTeamCheckbox, awayTeamCheckbox, state);
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void sendUpdateRequest(final Constants.PREDICTION_TYPE state, final int position, final Spinner teamSpinner, final LinearLayout teamBackground, final ImageView teamIcon, final PredictionBeforeSeason predictionBeforeSeason){
        final String teamPredicted = position == 0 ? "" : teamInfoList.get(position).getTeamPrefix();
        UpdatePredictionBeforeSeasonRequest updatePredictionBeforeSeasonRequest = new UpdatePredictionBeforeSeasonRequest();
        updatePredictionBeforeSeasonRequest.setTeamprefix(teamPredicted);
        updatePredictionBeforeSeasonRequest.setPredictionType(state.toString().toLowerCase());
        updatePredictionBeforeSeasonRequest.setUserId(this.userId);

        Call<UpdateResponse> response = this.apiInterface.updatePredictionPlus(updatePredictionBeforeSeasonRequest);

        response.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {
                UpdateResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    updateModel(state, teamPredicted, teamSpinner, predictionBeforeSeason);
                    setTeamInfos(teamSpinner.getSelectedItemPosition(), teamBackground, teamIcon);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                    setSpinnerToLastValue(state, teamSpinner, teamBackground, teamIcon);
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                setSpinnerToLastValue(state, teamSpinner, teamBackground, teamIcon);
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void updateModel(CheckBox homeTeamCheckbox, CheckBox awayTeamCheckbox, GamePrediction gamePrediction, UPDATE_STATES state){
        switch (state){
            case HOME_TEAM_SELECTED:{
                gamePrediction.setHaspredicted(1);
                gamePrediction.setPredictedhometeam(1);
                break;
            }
            case AWAY_TEAM_SELECTED: {
                gamePrediction.setHaspredicted(1);
                gamePrediction.setPredictedhometeam(0);
                break;
            }
            case UNPREDICTED: {
                gamePrediction.setHaspredicted(0);
                gamePrediction.setPredictedhometeam(0);
                break;
            }
            default: break;
        }
        lastHomeTeamCheckboxStatus = homeTeamCheckbox.isChecked();
        lastAwayTeamCheckboxStatus = awayTeamCheckbox.isChecked();
    }

    private void updateModel(Constants.PREDICTION_TYPE state, String teamPredicted, Spinner teamSpinner, PredictionBeforeSeason predictionBeforeSeason){
        switch (state) {
            case SUPERBOWL: {
                lastSuperbowlSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionBeforeSeason.setSuperbowl(teamPredicted);
                break;
            }
            case AFC_WINNER: {
                lastAFCSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionBeforeSeason.setAfcwinnerteam(teamPredicted);
                break;
            }
            case NFC_WINNER: {
                lastNFCSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionBeforeSeason.setNfcwinnerteam(teamPredicted);
                break;
            }
            case BEST_OFFENSE: {
                lastOffenseSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionBeforeSeason.setBestoffenseteam(teamPredicted);
                break;
            }
            case BEST_DEFENSE: {
                lastDefenseSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionBeforeSeason.setBestdefenseteam(teamPredicted);
                break;
            }
            default:
                break;
        }
    }

    private void setCheckboxesToLastValue(CheckBox homeTeamCheckbox, CheckBox awayTeamCheckbox, UPDATE_STATES state){
        switch (state){
            case HOME_TEAM_SELECTED:{
                userInteractionHomeCheckbox = false;
                homeTeamCheckbox.setChecked(false);
                if(lastAwayTeamCheckboxStatus){
                    userInteractionAwayCheckbox = false;
                    awayTeamCheckbox.setChecked(true);
                }
                break;
            }
            case AWAY_TEAM_SELECTED: {
                userInteractionAwayCheckbox = false;
                awayTeamCheckbox.setChecked(false);
                if(lastHomeTeamCheckboxStatus){
                    userInteractionHomeCheckbox = false;
                    homeTeamCheckbox.setChecked(true);
                }
                break;
            }
            case UNPREDICTED: {
                if(lastHomeTeamCheckboxStatus){
                    userInteractionHomeCheckbox = false;
                    homeTeamCheckbox.setChecked(true);
                }
                if(lastAwayTeamCheckboxStatus){
                    userInteractionAwayCheckbox = false;
                    awayTeamCheckbox.setChecked(true);
                }
                break;
            }
            default: break;
        }
    }

    private void setSpinnerToLastValue(Constants.PREDICTION_TYPE state, Spinner teamSpinner, LinearLayout teamBackground, ImageView teamIcon){
        userInteraction = false;
        switch (state) {
            case SUPERBOWL: {
                teamSpinner.setSelection(lastSuperbowlSpinnerPosition, false);
                setTeamInfos(lastSuperbowlSpinnerPosition, teamBackground, teamIcon);
                break;
            }
            case AFC_WINNER: {
                teamSpinner.setSelection(lastAFCSpinnerPosition, false);
                setTeamInfos(lastAFCSpinnerPosition, teamBackground, teamIcon);
                break;
            }
            case NFC_WINNER: {
                teamSpinner.setSelection(lastNFCSpinnerPosition, false);
                setTeamInfos(lastNFCSpinnerPosition, teamBackground, teamIcon);
                break;
            }
            case BEST_OFFENSE: {
                teamSpinner.setSelection(lastOffenseSpinnerPosition, false);
                setTeamInfos(lastOffenseSpinnerPosition, teamBackground, teamIcon);
                break;
            }
            case BEST_DEFENSE: {
                teamSpinner.setSelection(lastDefenseSpinnerPosition, false);
                setTeamInfos(lastDefenseSpinnerPosition, teamBackground, teamIcon);
                break;
            }
            default:
                break;
        }
    }

    private void getAllPredictionsForGameid(final GamePrediction gamePrediction, AllPredictionsRequest request){
        Call<AllPredictionsResponse> response = this.apiInterface.allPredictions(request);

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
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
