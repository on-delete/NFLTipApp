package com.andre.nfltipapp.tabview.fragments.predictionssection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import static com.andre.nfltipapp.Constants.UPDATE_TYPE;
import static com.andre.nfltipapp.Constants.PREDICTION_TYPE;

class PredictionsListViewAdapter extends BaseExpandableListAdapter {

    private ApiInterface apiInterface;

    private Activity activity;
    private LayoutInflater layoutInflater;

    private List<String> predictionListHeaders = new ArrayList<>();
    private HashMap<String, List<?>> predictionListItems = new HashMap<>();
    private ArrayList<TeamInfoSpinnerObject> teamInfoList = new ArrayList<>();
    private ArrayList<String> teamPrefixList = new ArrayList<>();

    private String userId;
    private int lastSuperbowlSpinnerPosition = 0;
    private int lastAFCSpinnerPosition = 0;
    private int lastNFCSpinnerPosition = 0;
    private int lastOffenseSpinnerPosition = 0;
    private int lastDefenseSpinnerPosition = 0;
    private int offsetPredictionPlusTime = 0;
    private boolean lastHomeTeamCheckboxStatus = false;
    private boolean lastAwayTeamCheckboxStatus = false;
    private boolean userInteraction = true;
    private boolean userInteractionHomeCheckbox = true;
    private boolean userInteractionAwayCheckbox = true;

    PredictionsListViewAdapter(Activity activity, List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList, String userId) {
        this.activity = activity;
        this.userId = userId;

        apiInterface = Api.getInstance(activity).getApiInterface();

        initPredictionListItems(predictionsForWeekList, predictionBeforeSeasonList);
    }

    private void initPredictionListItems(List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList){
        if(!Utils.isPredictionTimeOver(predictionBeforeSeasonList.get(0).getFirstgamedate(), 0)){
            this.predictionListHeaders.add(Constants.PREDICTION_BEFORE_SEASON);
            predictionListItems.put(Constants.PREDICTION_BEFORE_SEASON, predictionBeforeSeasonList);
        }

        for(PredictionsForWeek predictionsForWeekItem : predictionsForWeekList){
            List<GamePrediction> tempGamesList = new ArrayList<>();

            for (GamePrediction gamePrediction : predictionsForWeekItem.getGamePredictions()){
                if(gamePrediction.isFinished()==0){
                    tempGamesList.add(gamePrediction);
                }
            }

            if(tempGamesList.size()>0){
                String title = Constants.WEEK + predictionsForWeekItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) : "");
                this.predictionListHeaders.add(title);
                this.predictionListItems.put(title, tempGamesList);
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int predictionListPosition) {
        List<?> genericList = this.predictionListItems.get(this.predictionListHeaders.get(listPosition));

        if(genericList.get(0) instanceof GamePrediction){
            return genericList.get(predictionListPosition);
        }
        else{
            for(int i = 0; i < genericList.size(); i++){
                PredictionBeforeSeason tempPrediction = (PredictionBeforeSeason) genericList.get(i);
                if(tempPrediction.getUser().equals("user")){
                    return tempPrediction;
                }
            }
            return null;
        }
    }

    @Override
    public long getChildId(int listPosition, int predictionListPosition) {
        return predictionListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int predictionListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        layoutInflater = (LayoutInflater) this.activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Object listItem = getChild(listPosition, predictionListPosition);
        if(listItem instanceof GamePrediction){
            return initPredictionView(parent, (GamePrediction) listItem);
        }
        else {
            return initPredictionBeforeSeasonView(parent, (PredictionBeforeSeason) listItem);
        }
    }

    private View initPredictionView(ViewGroup parent, final GamePrediction gamePrediction){
        View convertView = layoutInflater.inflate(R.layout.predictions_list_item, parent, false);

        final LinearLayout llDisableOverlay = (LinearLayout) convertView.findViewById(R.id.disable_overlay);
        final CheckBox cbHomeTeam = (CheckBox) convertView
                .findViewById(R.id.home_team_checkbox);
        final CheckBox cbAwayTeam = (CheckBox) convertView
                .findViewById(R.id.away_team_checkbox);
        ImageView ivAwayTeamIcon = (ImageView) convertView.findViewById(R.id.icon_away_team);
        ImageView ivHomeTeamIcon = (ImageView) convertView.findViewById(R.id.icon_home_team);
        LinearLayout llAwayTeamBackground = (LinearLayout) convertView.findViewById(R.id.away_team_logo_background);
        LinearLayout llHomeTeamBackground = (LinearLayout) convertView.findViewById(R.id.home_team_logo_background);

        llAwayTeamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamColor()));
        llHomeTeamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamColor()));

        ivAwayTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamIcon());
        ivHomeTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamIcon());

        cbHomeTeam.setChecked(false);
        cbAwayTeam.setChecked(false);

        initCheckboxesToModel(gamePrediction, cbHomeTeam, cbAwayTeam);

        lastHomeTeamCheckboxStatus = cbHomeTeam.isChecked();
        lastAwayTeamCheckboxStatus = cbAwayTeam.isChecked();

        checkIfPredictionTimeIsOver(gamePrediction, cbHomeTeam, cbAwayTeam, llDisableOverlay);

        setOnCheckedChangeListener(gamePrediction, cbHomeTeam, cbAwayTeam, llDisableOverlay);
        setOnCheckedChangeListener(gamePrediction, cbAwayTeam, cbHomeTeam, llDisableOverlay);

        return convertView;
    }

    private void initCheckboxesToModel(GamePrediction gamePrediction, CheckBox cbHomeTeam, CheckBox cbAwayTeam){
        if(gamePrediction.hasPredicted() == 1){
            if(gamePrediction.predictedHometeam() == 1){
                cbHomeTeam.setChecked(true);
            }
            else{
                cbAwayTeam.setChecked(true);
            }
        }
    }

    private boolean checkIfPredictionTimeIsOver(final GamePrediction gamePrediction, CheckBox cbOne, CheckBox cbTwo, LinearLayout llDisableOverlay){
        int offsetPredictionTime = -30;
        if(Utils.isPredictionTimeOver(gamePrediction.getGamedatetime(), offsetPredictionTime)){
            cbOne.setEnabled(false);
            cbTwo.setEnabled(false);

            llDisableOverlay.setBackgroundColor(Color.parseColor("#B2B2B2"));
            llDisableOverlay.getBackground().setAlpha(150);

            llDisableOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AllPredictionsRequest request = new AllPredictionsRequest();
                    request.setGameid(gamePrediction.getGameid());
                    getAllPredictionsForGameid(gamePrediction, request);
                }
            });

            return true;
        }

        return false;
    }

    private void setOnCheckedChangeListener(final GamePrediction gamePrediction, CheckBox cbClicked, final CheckBox cbOther, final LinearLayout llDisableOverlay){
        cbClicked.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox clickedBox = (CheckBox) buttonView;

                if(checkIfPredictionTimeIsOver(gamePrediction, clickedBox, cbOther, llDisableOverlay)){
                    if(isChecked){
                        clickedBox.setChecked(false);
                    }
                    else {
                        clickedBox.setChecked(true);
                    }
                }
                else{
                    if(clickedBox.getId() == R.id.home_team_checkbox) {
                        if (userInteractionHomeCheckbox) {
                            userInteractionAwayCheckbox = false;
                            if (isChecked) {
                                cbOther.setChecked(false);
                                updatePrediction(clickedBox, cbOther, UPDATE_TYPE.HOME_TEAM_SELECTED, gamePrediction);
                            } else {
                                updatePrediction(clickedBox, cbOther, UPDATE_TYPE.UNPREDICTED, gamePrediction);
                            }
                        }
                        userInteractionAwayCheckbox = true;
                    }
                    else{
                        if(userInteractionAwayCheckbox){
                            userInteractionHomeCheckbox = false;
                            if (isChecked) {
                                cbOther.setChecked(false);
                                updatePrediction(cbOther, clickedBox, UPDATE_TYPE.AWAY_TEAM_SELECTED, gamePrediction);
                            } else {
                                updatePrediction(cbOther, clickedBox, UPDATE_TYPE.UNPREDICTED, gamePrediction);
                            }
                        }
                        userInteractionHomeCheckbox = true;
                    }
                }
            }
        });
    }

    private View initPredictionBeforeSeasonView(ViewGroup parent, PredictionBeforeSeason predictionBeforeSeason) {
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

        View convertView = layoutInflater.inflate(R.layout.predictions_plus_item, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.subitems_container);

        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.SUPERBOWL, predictionBeforeSeason.getSuperbowlTeam(), predictionBeforeSeason), 0);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.AFC_WINNER, predictionBeforeSeason.getAfcwinnerteam(), predictionBeforeSeason), 1);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.NFC_WINNER, predictionBeforeSeason.getNfcwinnerteam(), predictionBeforeSeason), 2);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.BEST_OFFENSE, predictionBeforeSeason.getBestoffenseteam(), predictionBeforeSeason), 3);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.BEST_DEFENSE, predictionBeforeSeason.getBestdefenseteam(), predictionBeforeSeason), 4);

        return convertView;
    }

    private View initPredictionBeforeSeasonSubView(ViewGroup parent, final PREDICTION_TYPE predictionType, String team, final PredictionBeforeSeason predictionBeforeSeason){
        View subView = layoutInflater.inflate(R.layout.predictions_plus_subitem, parent, false);

        final LinearLayout llTeamBackground = (LinearLayout) subView.findViewById(R.id.team_background);

        TextView tvTeamName = (TextView) subView.findViewById(R.id.team_text);
        Spinner spTeamChoice = (Spinner) subView.findViewById(R.id.team_spinner);
        final ImageView ivTeamIcon = (ImageView) subView.findViewById(R.id.team_icon);

        TeamPickSpinnerAdapter adapter = new TeamPickSpinnerAdapter(this.activity, R.layout.spinner_item, teamInfoList);
        spTeamChoice.setAdapter(adapter);

        if(!team.equals("")){
            spTeamChoice.setSelection(teamPrefixList.indexOf(team), false);
        }
        else{
            spTeamChoice.setSelection(0, false);
        }

        setTeamInfos(spTeamChoice.getSelectedItemPosition(), llTeamBackground, ivTeamIcon);

        initViewsToModel(predictionType, tvTeamName, spTeamChoice);

        spTeamChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!userInteraction){
                    userInteraction = true;
                    return;
                }
                if(Utils.isPredictionTimeOver(predictionBeforeSeason.getFirstgamedate(), offsetPredictionPlusTime)){
                    Spinner spinner = (Spinner) parent;
                    switch (predictionType) {
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
                    sendUpdateRequest(predictionType, position, (Spinner) parent, llTeamBackground, ivTeamIcon, predictionBeforeSeason);
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

    private void initViewsToModel(PREDICTION_TYPE predictionType, TextView tvTeamName, Spinner spTeamChoice){
        switch (predictionType) {
            case SUPERBOWL: {
                tvTeamName.setText(R.string.superbowl);
                lastSuperbowlSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case AFC_WINNER: {
                tvTeamName.setText(R.string.afc_winner);
                lastAFCSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case NFC_WINNER: {
                tvTeamName.setText(R.string.nfc_winner);
                lastNFCSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case BEST_OFFENSE: {
                tvTeamName.setText(R.string.best_offense);
                lastOffenseSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case BEST_DEFENSE: {
                tvTeamName.setText(R.string.best_defense);
                lastDefenseSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int getChildrenCount(int listPosition) {
        List<?> genericList = this.predictionListItems.get(this.predictionListHeaders.get(listPosition));

        if(genericList.get(0) instanceof GamePrediction){
            return genericList.size();
        }
        else{
            return 1;
        }
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.predictionListHeaders.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.predictionListHeaders.size();
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
        TextView llPredictionListHeader = (TextView) convertView.findViewById(R.id.listTitle);
        llPredictionListHeader.setText(listTitle);

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

    private void updatePrediction(CheckBox cbHomeTeam, CheckBox cbAwayTeam, UPDATE_TYPE updateType, GamePrediction gamePrediction){
        UpdatePredictionRequest request = new UpdatePredictionRequest();
        request.setGameId(gamePrediction.getGameid());
        request.setUserId(this.userId);

        switch (updateType){
            case HOME_TEAM_SELECTED:{
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request, cbHomeTeam, cbAwayTeam, gamePrediction, updateType);
                break;
            }
            case AWAY_TEAM_SELECTED: {
                request.setHasPredicted(true);
                request.setHasHomeTeamPredicted(false);
                sendUpdateRequest(request, cbHomeTeam, cbAwayTeam, gamePrediction, updateType);
                break;
            }
            case UNPREDICTED: {
                request.setHasPredicted(false);
                request.setHasHomeTeamPredicted(true);
                sendUpdateRequest(request, cbHomeTeam, cbAwayTeam, gamePrediction, updateType);
                break;
            }
            default: break;
        }
    }

    private void sendUpdateRequest(UpdatePredictionRequest request, final CheckBox cbHomeTeam, final CheckBox cbAwayTeam, final GamePrediction gamePrediction, final UPDATE_TYPE updateType){
        Call<UpdateResponse> response = this.apiInterface.updatePrediction(request);

        response.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {
                UpdateResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    updateModel(cbHomeTeam, cbAwayTeam, gamePrediction, updateType);
                }
                else{
                    setCheckboxesToLastValue(cbHomeTeam, cbAwayTeam, updateType);
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                setCheckboxesToLastValue(cbHomeTeam, cbAwayTeam, updateType);
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void sendUpdateRequest(final PREDICTION_TYPE predictionType, final int position, final Spinner spTeamChoice, final LinearLayout llTeamBackground, final ImageView ivTeamIcon, final PredictionBeforeSeason predictionBeforeSeason){
        final String teamPredicted = position == 0 ? "" : teamInfoList.get(position).getTeamPrefix();
        UpdatePredictionBeforeSeasonRequest updatePredictionBeforeSeasonRequest = new UpdatePredictionBeforeSeasonRequest();
        updatePredictionBeforeSeasonRequest.setTeamprefix(teamPredicted);
        updatePredictionBeforeSeasonRequest.setPredictionType(predictionType.toString().toLowerCase());
        updatePredictionBeforeSeasonRequest.setUserId(this.userId);

        Call<UpdateResponse> response = this.apiInterface.updatePredictionPlus(updatePredictionBeforeSeasonRequest);

        response.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {
                UpdateResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    updateModel(predictionType, teamPredicted, spTeamChoice, predictionBeforeSeason);
                    setTeamInfos(spTeamChoice.getSelectedItemPosition(), llTeamBackground, ivTeamIcon);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                    setSpinnerToLastValue(predictionType, spTeamChoice, llTeamBackground, ivTeamIcon);
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                setSpinnerToLastValue(predictionType, spTeamChoice, llTeamBackground, ivTeamIcon);
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void updateModel(CheckBox cbHomeTeam, CheckBox cbAwayTeam, GamePrediction gamePrediction, UPDATE_TYPE updateType){
        switch (updateType){
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
        lastHomeTeamCheckboxStatus = cbHomeTeam.isChecked();
        lastAwayTeamCheckboxStatus = cbAwayTeam.isChecked();
    }

    private void updateModel(PREDICTION_TYPE predictionType, String teamPredicted, Spinner spTeamChoice, PredictionBeforeSeason predictionBeforeSeason){
        switch (predictionType) {
            case SUPERBOWL: {
                lastSuperbowlSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                predictionBeforeSeason.setSuperbowl(teamPredicted);
                break;
            }
            case AFC_WINNER: {
                lastAFCSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                predictionBeforeSeason.setAfcwinnerteam(teamPredicted);
                break;
            }
            case NFC_WINNER: {
                lastNFCSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                predictionBeforeSeason.setNfcwinnerteam(teamPredicted);
                break;
            }
            case BEST_OFFENSE: {
                lastOffenseSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                predictionBeforeSeason.setBestoffenseteam(teamPredicted);
                break;
            }
            case BEST_DEFENSE: {
                lastDefenseSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                predictionBeforeSeason.setBestdefenseteam(teamPredicted);
                break;
            }
            default:
                break;
        }
    }

    private void setCheckboxesToLastValue(CheckBox cbHomeTeam, CheckBox cbAwayTeam, UPDATE_TYPE updateType){
        switch (updateType){
            case HOME_TEAM_SELECTED:{
                userInteractionHomeCheckbox = false;
                cbHomeTeam.setChecked(false);
                if(lastAwayTeamCheckboxStatus){
                    userInteractionAwayCheckbox = false;
                    cbAwayTeam.setChecked(true);
                }
                break;
            }
            case AWAY_TEAM_SELECTED: {
                userInteractionAwayCheckbox = false;
                cbAwayTeam.setChecked(false);
                if(lastHomeTeamCheckboxStatus){
                    userInteractionHomeCheckbox = false;
                    cbHomeTeam.setChecked(true);
                }
                break;
            }
            case UNPREDICTED: {
                if(lastHomeTeamCheckboxStatus){
                    userInteractionHomeCheckbox = false;
                    cbHomeTeam.setChecked(true);
                }
                if(lastAwayTeamCheckboxStatus){
                    userInteractionAwayCheckbox = false;
                    cbAwayTeam.setChecked(true);
                }
                break;
            }
            default: break;
        }
    }

    private void setSpinnerToLastValue(PREDICTION_TYPE predictionType, Spinner spTeamChoice, LinearLayout llTeamBackground, ImageView ivTeamIcon){
        userInteraction = false;
        switch (predictionType) {
            case SUPERBOWL: {
                spTeamChoice.setSelection(lastSuperbowlSpinnerPosition, false);
                setTeamInfos(lastSuperbowlSpinnerPosition, llTeamBackground, ivTeamIcon);
                break;
            }
            case AFC_WINNER: {
                spTeamChoice.setSelection(lastAFCSpinnerPosition, false);
                setTeamInfos(lastAFCSpinnerPosition, llTeamBackground, ivTeamIcon);
                break;
            }
            case NFC_WINNER: {
                spTeamChoice.setSelection(lastNFCSpinnerPosition, false);
                setTeamInfos(lastNFCSpinnerPosition, llTeamBackground, ivTeamIcon);
                break;
            }
            case BEST_OFFENSE: {
                spTeamChoice.setSelection(lastOffenseSpinnerPosition, false);
                setTeamInfos(lastOffenseSpinnerPosition, llTeamBackground, ivTeamIcon);
                break;
            }
            case BEST_DEFENSE: {
                spTeamChoice.setSelection(lastDefenseSpinnerPosition, false);
                setTeamInfos(lastDefenseSpinnerPosition, llTeamBackground, ivTeamIcon);
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
