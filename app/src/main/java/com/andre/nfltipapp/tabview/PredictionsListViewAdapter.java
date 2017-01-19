package com.andre.nfltipapp.tabview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.model.UpdatePredictionRequest;
import com.andre.nfltipapp.model.UpdatePredictionResponse;
import com.andre.nfltipapp.rest.RequestInterface;
import static com.andre.nfltipapp.Constants.UPDATE_STATES;

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
        LayoutInflater layoutInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.predictions_list_item, null);
        final CheckBox homeTeamCheckbox = (CheckBox) convertView
                .findViewById(R.id.home_team_checkbox);
        final CheckBox awayTeamCheckbox = (CheckBox) convertView
                .findViewById(R.id.away_team_checkbox);
        ImageView awayTeamIcon = (ImageView) convertView.findViewById(R.id.icon_away_team);
        ImageView homeTeamIcon = (ImageView) convertView.findViewById(R.id.icon_home_team);
        RelativeLayout awayLogoBackground = (RelativeLayout) convertView.findViewById(R.id.away_team_logo_background);
        RelativeLayout homeLogoBackground = (RelativeLayout) convertView.findViewById(R.id.home_team_logo_background);

        GradientDrawable gdAway = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamColor()), Color.parseColor(Constants.WHITE_BACKGROUND)});
        gdAway.setGradientCenter(0.5f, 0.0f);
        awayLogoBackground.setBackground(gdAway);

        GradientDrawable gdHome = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamColor()), Color.parseColor(Constants.WHITE_BACKGROUND)});
        gdHome.setGradientCenter(0.5f, 0.0f);
        homeLogoBackground.setBackground(gdHome);

        awayTeamIcon.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamColor()));
        homeTeamIcon.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamColor()));

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
}
