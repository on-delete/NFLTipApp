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
import android.widget.TableLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.drawable.PredictionsTeamBackground;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;

import static com.andre.nfltipapp.Constants.UPDATE_TYPE;
import static com.andre.nfltipapp.Constants.PREDICTION_TYPE;

class PredictionsListViewAdapter extends BaseExpandableListAdapter {

    private ApiInterface apiInterface;

    private Activity activity;
    private LayoutInflater layoutInflater;

    private List<String> predictionListHeaders = new ArrayList<>();
    private HashMap<String, Map<String, List<GamePrediction>>> predictionListItems = new HashMap<>();
    private ArrayList<TeamInfoSpinnerObject> teamInfoList = new ArrayList<>();
    private ArrayList<String> teamPrefixList = new ArrayList<>();
    private ArrayList<TeamInfoSpinnerObject> teamInfoAFCList = new ArrayList<>();
    private ArrayList<String> teamPrefixAFCList = new ArrayList<>();
    private ArrayList<TeamInfoSpinnerObject> teamInfoNFCList = new ArrayList<>();
    private ArrayList<String> teamPrefixNFCList = new ArrayList<>();

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
    private PredictionBeforeSeason predictionBeforeSeason = null;

    PredictionsListViewAdapter(Activity activity, List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList, String userId) {
        this.activity = activity;
        this.userId = userId;

        apiInterface = Api.getInstance(activity).getApiInterface();

        initPredictionListItems(predictionsForWeekList, predictionBeforeSeasonList);
    }

    public void updateLists(List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList){
        this.predictionListHeaders = new ArrayList<>();
        this.predictionBeforeSeason = null;
        this.predictionListItems = new HashMap<>();

        initPredictionListItems(predictionsForWeekList, predictionBeforeSeasonList);
        notifyDataSetChanged();
    }

    private void initPredictionListItems(List<PredictionsForWeek> predictionsForWeekList, List<PredictionBeforeSeason> predictionBeforeSeasonList){
        if(!Utils.isPredictionTimeOver(predictionBeforeSeasonList.get(0).getFirstgamedate(), 0)){
            this.predictionListHeaders.add(Constants.PREDICTION_BEFORE_SEASON);
            for(int i = 0; i < predictionBeforeSeasonList.size(); i++){
                PredictionBeforeSeason tempPrediction = predictionBeforeSeasonList.get(i);
                if(tempPrediction.getUser().equals("user")){
                    this.predictionBeforeSeason = tempPrediction;
                }
            }
        }

        for(PredictionsForWeek predictionsForWeekItem : predictionsForWeekList){
            Map<String, List<GamePrediction>> tempGamesPerDayList = new LinkedHashMap<>();

            for (GamePrediction gamePrediction : predictionsForWeekItem.getGamePredictions()){
                if(gamePrediction.isFinished()==0){
                    String gameDay = Utils.getGameDay(gamePrediction.getGamedatetime());

                    if(tempGamesPerDayList.containsKey(gameDay)){
                        tempGamesPerDayList.get(gameDay).add(gamePrediction);
                    } else {
                        List<GamePrediction> subList = new ArrayList<>();
                        subList.add(gamePrediction);
                        tempGamesPerDayList.put(gameDay, subList);
                    }
                }
            }

            if(tempGamesPerDayList.size()>0){
                String title = Constants.WEEK + predictionsForWeekItem.getWeek() + " | " + (Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionsForWeekItem.getType()) : "");
                this.predictionListHeaders.add(title);
                this.predictionListItems.put(title, tempGamesPerDayList);
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int predictionListPosition) {
        if(predictionBeforeSeason != null && listPosition == 0){
            return predictionBeforeSeason;
        }

        return this.predictionListItems.get(this.predictionListHeaders.get(listPosition));
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
        if(listItem instanceof Map){
            Map<String, List<GamePrediction>> tempPredicitons = (Map<String, List<GamePrediction>>) listItem;
            Set keys = tempPredicitons.keySet();
            int i = 0;
            for(Object key : keys){
                String keyTemp = (String) key;
                if(i == predictionListPosition){
                    return initGameDayPredicitonsView(parent, keyTemp, tempPredicitons.get(keyTemp));
                }
                i++;
            }
            return null;
        }
        else {
            return initPredictionBeforeSeasonView(parent, (PredictionBeforeSeason) listItem);
        }
    }

    private View initGameDayPredicitonsView(ViewGroup parent, String gameDay, final List<GamePrediction> gamePrediction){
        View convertView = layoutInflater.inflate(R.layout.list_item_prediction_per_day_list, parent, false);

        TextView tvGameDay = (TextView) convertView.findViewById(R.id.text_game_day);
        TableLayout tvPredictionList = (TableLayout) convertView.findViewById(R.id.table_predictions_for_game_root);

        tvGameDay.setText(gameDay);

        for(int i = 0; i<gamePrediction.size(); i++){
            View predicitonView = initPredictionView(parent, gamePrediction.get(i));
            tvPredictionList.addView(predicitonView);
        }

        return convertView;
    }

    private View initPredictionView(ViewGroup parent, GamePrediction gamePrediction){
        View convertView = layoutInflater.inflate(R.layout.list_item_prediction_for_game, parent, false);

        final LinearLayout llDisableOverlay = (LinearLayout) convertView.findViewById(R.id.linear_disable_overlay);
        final CheckBox cbHomeTeam = (CheckBox) convertView
                .findViewById(R.id.checkbox_home_team);
        final CheckBox cbAwayTeam = (CheckBox) convertView
                .findViewById(R.id.checkbox_away_team);
        LinearLayout llAwayTeamBackground = (LinearLayout) convertView.findViewById(R.id.linear_background_team_away);
        LinearLayout llHomeTeamBackground = (LinearLayout) convertView.findViewById(R.id.linear_background_team_home);
        TextView tvGameTime = (TextView) convertView.findViewById(R.id.text_prediction_game_time);
        TextView tvAwayTeamCityName = (TextView) convertView.findViewById(R.id.text_team_city_away);
        TextView tvAwayTeamName = (TextView) convertView.findViewById(R.id.text_team_name_away);
        TextView tvHomeTeamCityName = (TextView) convertView.findViewById(R.id.text_team_city_home);
        TextView tvHomeTeamName = (TextView) convertView.findViewById(R.id.text_team_name_home);

        tvGameTime.setText(Utils.getGameTime(gamePrediction.getGamedatetime()));
        tvAwayTeamCityName.setText(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamCity());
        tvAwayTeamName.setText(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamName());
        tvHomeTeamCityName.setText(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamCity());
        tvHomeTeamName.setText(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamName());

        llAwayTeamBackground.setBackground(new PredictionsTeamBackground(Color.parseColor(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamColor()), true));
        llHomeTeamBackground.setBackground(new PredictionsTeamBackground(Color.parseColor(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamColor()), false));

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
                    if(clickedBox.getId() == R.id.checkbox_home_team) {
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
        initSpinnerLists();

        View convertView = layoutInflater.inflate(R.layout.list_item_prediction_before_season, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.linear_subitems_container);

        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.SUPERBOWL, predictionBeforeSeason.getSuperbowlTeam(), predictionBeforeSeason, teamInfoList, teamPrefixList), 0);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.AFC_WINNER, predictionBeforeSeason.getAfcwinnerteam(), predictionBeforeSeason, teamInfoAFCList, teamPrefixAFCList), 1);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.NFC_WINNER, predictionBeforeSeason.getNfcwinnerteam(), predictionBeforeSeason, teamInfoNFCList, teamPrefixNFCList), 2);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.BEST_OFFENSE, predictionBeforeSeason.getBestoffenseteam(), predictionBeforeSeason, teamInfoList, teamPrefixList), 3);
        container.addView(initPredictionBeforeSeasonSubView(parent, PREDICTION_TYPE.BEST_DEFENSE, predictionBeforeSeason.getBestdefenseteam(), predictionBeforeSeason, teamInfoList, teamPrefixList), 4);

        return convertView;
    }

    private void initSpinnerLists(){
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

        if(teamInfoAFCList.isEmpty() && teamPrefixAFCList.isEmpty()){
            for (String key : Constants.TEAM_INFO_MAP.keySet()) {
                if(Constants.TEAM_INFO_MAP.get(key).getDivision() == Constants.DIVISION.AFC) {
                    teamInfoAFCList.add(new TeamInfoSpinnerObject(Constants.TEAM_INFO_MAP.get(key).getTeamName(), key));
                }
            }

            Collections.sort(teamInfoAFCList);

            teamInfoAFCList.add(0, new TeamInfoSpinnerObject("", ""));

            for(TeamInfoSpinnerObject object : teamInfoAFCList){
                teamPrefixAFCList.add(object.getTeamPrefix());
            }
        }

        if(teamInfoNFCList.isEmpty() && teamPrefixNFCList.isEmpty()){
            for (String key : Constants.TEAM_INFO_MAP.keySet()) {
                if(Constants.TEAM_INFO_MAP.get(key).getDivision() == Constants.DIVISION.NFC) {
                    teamInfoNFCList.add(new TeamInfoSpinnerObject(Constants.TEAM_INFO_MAP.get(key).getTeamName(), key));
                }
            }

            Collections.sort(teamInfoNFCList);

            teamInfoNFCList.add(0, new TeamInfoSpinnerObject("", ""));

            for(TeamInfoSpinnerObject object : teamInfoNFCList){
                teamPrefixNFCList.add(object.getTeamPrefix());
            }
        }
    }

    private View initPredictionBeforeSeasonSubView(ViewGroup parent, final PREDICTION_TYPE predictionType, String team, final PredictionBeforeSeason predictionBeforeSeason, final ArrayList<TeamInfoSpinnerObject> teamInfoList, final ArrayList<String> teamPrefixList){
        View subView = layoutInflater.inflate(R.layout.list_subitem_prediction_before_season, parent, false);

        final LinearLayout llTeamBackground = (LinearLayout) subView.findViewById(R.id.linear_team_background);

        TextView tvTeamName = (TextView) subView.findViewById(R.id.text_team_name);
        Spinner spTeamChoice = (Spinner) subView.findViewById(R.id.spinner_team_choice);
        final ImageView ivPredicitonType = (ImageView) subView.findViewById(R.id.image_prediction_type);

        TeamPickSpinnerAdapter adapter = new TeamPickSpinnerAdapter(this.activity, R.layout.costum_spinner_view, teamInfoList);
        spTeamChoice.setAdapter(adapter);

        if(!team.equals("")){
            spTeamChoice.setSelection(teamPrefixList.indexOf(team), false);
        }
        else{
            spTeamChoice.setSelection(0, false);
        }

        setTeamInfos(spTeamChoice.getSelectedItemPosition(), teamPrefixList, llTeamBackground);

        initViewsToModel(predictionType, tvTeamName, spTeamChoice, ivPredicitonType);

        spTeamChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!userInteraction){
                    userInteraction = true;
                    return;
                }
                /*if(Utils.isPredictionTimeOver(predictionBeforeSeason.getFirstgamedate(), offsetPredictionPlusTime)){
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
                    Snackbar.make(activity.findViewById(R.id.list_view_predictions) ,"Zusatztips sind jetzt gesperrt!", Snackbar.LENGTH_LONG).show();
                }
                else {*/
                    sendUpdateRequest(predictionType, position, (Spinner) parent, llTeamBackground, predictionBeforeSeason, teamInfoList, teamPrefixList);
                //}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return subView;
    }

    private void setTeamInfos(int position, ArrayList<String> teamPrefixList, LinearLayout teamBackground){
        if(position == 0){
            teamBackground.setBackgroundColor(Color.parseColor(Constants.DEFAULT_TEAM_BACKGROUND_COLOR));
        }
        else {
            teamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position)).getTeamColor()));
        }
    }

    private void initViewsToModel(PREDICTION_TYPE predictionType, TextView tvTeamName, Spinner spTeamChoice, ImageView ivPredicitonType){
        switch (predictionType) {
            case SUPERBOWL: {
                tvTeamName.setText(R.string.superbowl);
                ivPredicitonType.setBackgroundResource(R.drawable.pokal);
                lastSuperbowlSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case AFC_WINNER: {
                tvTeamName.setText(R.string.afc_winner);
                ivPredicitonType.setBackgroundResource(R.drawable.pokal);
                lastAFCSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case NFC_WINNER: {
                tvTeamName.setText(R.string.nfc_winner);
                ivPredicitonType.setBackgroundResource(R.drawable.pokal);
                lastNFCSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case BEST_OFFENSE: {
                tvTeamName.setText(R.string.best_offense);
                ivPredicitonType.setBackgroundResource(R.drawable.schwert);
                lastOffenseSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            case BEST_DEFENSE: {
                tvTeamName.setText(R.string.best_defense);
                ivPredicitonType.setBackgroundResource(R.drawable.schild);
                lastDefenseSpinnerPosition = spTeamChoice.getSelectedItemPosition();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int getChildrenCount(int listPosition) {
        if(predictionBeforeSeason != null && listPosition == 0){
            return 1;
        }

        return this.predictionListItems.get(this.predictionListHeaders.get(listPosition)).size();
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
            convertView = layoutInflater.inflate(R.layout.list_header_view, parent, false);
        }

        TextView llPredictionListHeader = (TextView) convertView.findViewById(R.id.text_list_header);
        ImageView ivListHeaderImage = (ImageView) convertView.findViewById(R.id.image_list_header);

        if(listTitle.equals(Constants.PREDICTION_BEFORE_SEASON)) {
            convertView.setBackgroundColor(Color.parseColor("#013369"));
            llPredictionListHeader.setTextColor(Color.parseColor("#f0f0f0"));
            ivListHeaderImage.setBackgroundResource(R.drawable.stern);
        } else {
            convertView.setBackgroundResource(R.drawable.back_dark_grey);
            llPredictionListHeader.setTextColor(Color.parseColor("#3c3c3c"));
            ivListHeaderImage.setBackgroundResource(R.drawable.kalender_dark);
        }

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
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(activity.findViewById(R.id.list_view_predictions) , "Server error!", Snackbar.LENGTH_LONG).show();
                    setCheckboxesToLastValue(cbHomeTeam, cbAwayTeam, updateType);
                }
                else{
                    updateModel(cbHomeTeam, cbAwayTeam, gamePrediction, updateType);
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                setCheckboxesToLastValue(cbHomeTeam, cbAwayTeam, updateType);
                Snackbar.make(activity.findViewById(R.id.list_view_predictions) , "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void sendUpdateRequest(final PREDICTION_TYPE predictionType, final int position, final Spinner spTeamChoice, final LinearLayout llTeamBackground, final PredictionBeforeSeason predictionBeforeSeason, final ArrayList<TeamInfoSpinnerObject> teamInfoList, final ArrayList<String> teamPrefixList){
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
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(activity.findViewById(R.id.list_view_predictions) , "Server error!", Snackbar.LENGTH_LONG).show();
                    setSpinnerToLastValue(predictionType, teamPrefixList, spTeamChoice, llTeamBackground);
                }
                else{
                    updateModel(predictionType, teamPredicted, spTeamChoice, predictionBeforeSeason);
                    setTeamInfos(spTeamChoice.getSelectedItemPosition(), teamPrefixList, llTeamBackground);
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                setSpinnerToLastValue(predictionType, teamPrefixList, spTeamChoice, llTeamBackground);
                Snackbar.make(activity.findViewById(R.id.list_view_predictions) , "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
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

    private void setSpinnerToLastValue(PREDICTION_TYPE predictionType, ArrayList<String> teamPrefixList, Spinner spTeamChoice, LinearLayout llTeamBackground){
        userInteraction = false;
        switch (predictionType) {
            case SUPERBOWL: {
                spTeamChoice.setSelection(lastSuperbowlSpinnerPosition, false);
                setTeamInfos(lastSuperbowlSpinnerPosition, teamPrefixList, llTeamBackground);
                break;
            }
            case AFC_WINNER: {
                spTeamChoice.setSelection(lastAFCSpinnerPosition, false);
                setTeamInfos(lastAFCSpinnerPosition, teamPrefixList, llTeamBackground);
                break;
            }
            case NFC_WINNER: {
                spTeamChoice.setSelection(lastNFCSpinnerPosition, false);
                setTeamInfos(lastNFCSpinnerPosition, teamPrefixList, llTeamBackground);
                break;
            }
            case BEST_OFFENSE: {
                spTeamChoice.setSelection(lastOffenseSpinnerPosition, false);
                setTeamInfos(lastOffenseSpinnerPosition, teamPrefixList, llTeamBackground);
                break;
            }
            case BEST_DEFENSE: {
                spTeamChoice.setSelection(lastDefenseSpinnerPosition, false);
                setTeamInfos(lastDefenseSpinnerPosition, teamPrefixList, llTeamBackground);
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
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(activity.findViewById(R.id.list_view_predictions) , "Server error!", Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(activity.findViewById(R.id.list_view_predictions) , "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }
}
