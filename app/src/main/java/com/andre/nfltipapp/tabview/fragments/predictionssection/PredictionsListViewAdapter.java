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
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsRequest;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsResponse;
import com.andre.nfltipapp.tabview.fragments.model.Game;
import com.andre.nfltipapp.tabview.fragments.model.Prediction;
import com.andre.nfltipapp.tabview.fragments.model.PredictionPlus;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdatePredictionPlusRequest;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdatePredictionRequest;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdateResponse;
import com.andre.nfltipapp.rest.RequestInterface;
import com.andre.nfltipapp.tabview.fragments.StatisticForGameActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andre.nfltipapp.Constants.UPDATE_STATES;

public class PredictionsListViewAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private List<String> expandableListTitle = new ArrayList<>();
    private HashMap<String, List<?>> expandableListDetail = new HashMap<>();
    private String uuid;
    private OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).build();
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
    private RequestInterface requestInterface = retrofit.create(RequestInterface.class);
    private List<?> child;
    private LayoutInflater layoutInflater;
    private Object childView;
    private ArrayList<String> teamPrefixList = new ArrayList<>();
    private ArrayList<String> teamNameList = new ArrayList<>();

    private int lastSuperbowlSpinnerPosition = 0;
    private int lastAFCSpinnerPosition = 0;
    private int lastNFCSpinnerPosition = 0;
    private int lastOffenseSpinnerPosition = 0;
    private int lastDefenseSpinnerPosition = 0;
    private int offsetPredictionTime = -30;
    private int offsetPredictionPlusTime = 0;
    private boolean userInteraction = true;

    public PredictionsListViewAdapter(Activity activity, List<Prediction> predictionList, List<PredictionPlus> predictionPlus, String uuid) {
        this.activity = activity;
        this.uuid = uuid;

        if(!Utils.isPredictionTimeOver(predictionPlus.get(0).getFirstgamedate(), 0)){
            this.expandableListTitle.add("Tips vor der Saison");
            expandableListDetail.put("Tips vor der Saison", predictionPlus);
        }

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
        child = this.expandableListDetail.get(this.expandableListTitle.get(listPosition));

        if(child.get(0) instanceof Game){
            return child.get(expandedListPosition);
        }
        else{
            PredictionPlus returnPredictionPlus = null;

            for(int i = 0; i < child.size(); i++){
                PredictionPlus tempPrediction = (PredictionPlus) child.get(i);
                if(tempPrediction.getUser().equals("user")){
                    returnPredictionPlus = tempPrediction;
                    break;
                }
            }
            return returnPredictionPlus;
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

    private View initPredictionPlusView(View convertView, ViewGroup parent, PredictionPlus child) {
        if(teamNameList.isEmpty() && teamPrefixList.isEmpty()) {
            teamNameList.add("");
            for (String key : Constants.TEAM_INFO_MAP.keySet()) {
                teamPrefixList.add(key);
                teamNameList.add(Constants.TEAM_INFO_MAP.get(key).getTeamName());
            }
        }

        convertView = layoutInflater.inflate(R.layout.predictions_plus_item, parent, false);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.subitems_container);

        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.SUPERBOWL, child.getSuperbowl(), child), 0);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.AFC_WINNER, child.getAfcwinnerteam(), child), 1);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.NFC_WINNER, child.getNfcwinnerteam(), child), 2);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.BEST_OFFENSE, child.getBestoffenseteam(), child), 3);
        container.addView(initPredictionPlusSubView(parent, Constants.PREDICTIONS_PLUS_STATES.BEST_DEFENSE, child.getBestdefenseteam(), child), 4);

        return convertView;
    }

    private View initPredictionPlusSubView(ViewGroup parent, final Constants.PREDICTIONS_PLUS_STATES state, String team, final PredictionPlus predictionPlus){
        View subView = layoutInflater.inflate(R.layout.predictions_plus_subitem, parent, false);

        final LinearLayout teamBackground = (LinearLayout) subView.findViewById(R.id.team_background);

        TextView teamText = (TextView) subView.findViewById(R.id.team_text);
        Spinner teamSpinner = (Spinner) subView.findViewById(R.id.team_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.activity, R.layout.spinner_item, teamNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(adapter);

        final ImageView teamIcon = (ImageView) subView.findViewById(R.id.team_icon);

        if(!team.equals("")){
            teamSpinner.setSelection(teamPrefixList.indexOf(team) + 1, false);
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
                if(Utils.isPredictionTimeOver(predictionPlus.getFirstgamedate(), offsetPredictionPlusTime)){
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
                    sendUpdateRequest(state, position, (Spinner) parent, teamBackground, teamIcon, predictionPlus);
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
            teamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position - 1)).getTeamColor()));
            teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(teamPrefixList.get(position - 1)).getTeamIcon());
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
        Call<UpdateResponse> response = this.requestInterface.updatePrediction(request);

        response.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {
                UpdateResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    updateModel(homeTeamCheckbox, awayTeamCheckbox, game, state);
                }
                else{
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Snackbar.make(activity.findViewById(R.id.predictionsListView) ,"Server not available...", Snackbar.LENGTH_LONG).show();
                Log.d(Constants.TAG, t.getMessage());
            }
        });
    }

    private void sendUpdateRequest(final Constants.PREDICTIONS_PLUS_STATES state, final int position, final Spinner teamSpinner, final LinearLayout teamBackground, final ImageView teamIcon, final PredictionPlus predictionPlus){
        final String teamPredicted = position == 0 ? "" : teamPrefixList.get(position - 1);
        UpdatePredictionPlusRequest updatePredictionPlusRequest = new UpdatePredictionPlusRequest();
        updatePredictionPlusRequest.setTeamprefix(teamPredicted);
        updatePredictionPlusRequest.setState(state.toString().toLowerCase());
        updatePredictionPlusRequest.setUuid(this.uuid);

        Call<UpdateResponse> response = this.requestInterface.updatePredictionPlus(updatePredictionPlusRequest);

        response.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {
                UpdateResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    updateModel(state, teamPredicted, teamSpinner, predictionPlus);
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

    private void updateModel(Constants.PREDICTIONS_PLUS_STATES state, String teamPredicted, Spinner teamSpinner, PredictionPlus predictionPlus){
        switch (state) {
            case SUPERBOWL: {
                lastSuperbowlSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionPlus.setSuperbowl(teamPredicted);
                break;
            }
            case AFC_WINNER: {
                lastAFCSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionPlus.setAfcwinnerteam(teamPredicted);
                break;
            }
            case NFC_WINNER: {
                lastNFCSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionPlus.setNfcwinnerteam(teamPredicted);
                break;
            }
            case BEST_OFFENSE: {
                lastOffenseSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionPlus.setBestoffenseteam(teamPredicted);
                break;
            }
            case BEST_DEFENSE: {
                lastDefenseSpinnerPosition = teamSpinner.getSelectedItemPosition();
                predictionPlus.setBestdefenseteam(teamPredicted);
                break;
            }
            default:
                break;
        }
    }

    private void setSpinnerToLastValue(Constants.PREDICTIONS_PLUS_STATES state, Spinner teamSpinner, LinearLayout teamBackground, ImageView teamIcon){
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

    private void getAllPredictionsForGameid(final Game game, AllPredictionsRequest request){
        Call<AllPredictionsResponse> response = this.requestInterface.allPredictions(request);

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
