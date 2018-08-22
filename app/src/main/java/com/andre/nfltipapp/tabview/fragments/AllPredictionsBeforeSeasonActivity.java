package com.andre.nfltipapp.tabview.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.PredictionsBeforeSeasonStatistic;

import java.util.ArrayList;

public class AllPredictionsBeforeSeasonActivity extends AppCompatActivity {

    private String teamName;
    private LinearLayout llAllPredictionsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_predictions_before_season);

        ArrayList<PredictionsBeforeSeasonStatistic> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONS_BEFORE_SEASON);
        this.teamName = getIntent().getStringExtra(Constants.TEAMNAME);
        String userId = getIntent().getStringExtra(Constants.USERID);
        String predictionTypeParcel = getIntent().getStringExtra(Constants.PREDICTION_TYPE_STRING);
        Constants.PREDICTION_TYPE predictionType = Constants.PREDICTION_TYPE.valueOf(predictionTypeParcel);

        TextView tvPredictionType = (TextView) findViewById(R.id.text_prediction_type);
        TextView tvTeamName = (TextView) findViewById(R.id.text_team_name);
        this.llAllPredictionsTable = (LinearLayout) findViewById(R.id.linear_predictions_before_season_table);

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

        if(teamName.equals("")){
            tvTeamName.setText("-");
        }
        else {
            tvTeamName.setText(Constants.TEAM_INFO_MAP.get(teamName).getTeamName());
        }

        for(PredictionsBeforeSeasonStatistic prediction : predictionList){
            initView(prediction, userId);
        }
    }

    private void initView (PredictionsBeforeSeasonStatistic prediction, String userId){
        @SuppressLint("InflateParams") View rowView = getLayoutInflater().inflate(R.layout.statistic_prediction_before_season_table_row, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 0, 0, 10);
        rowView.setLayoutParams(layoutParams);
        if(userId.equals(prediction.getUserid())) {
            rowView.setBackgroundResource(R.drawable.back_dark_grey_with_left_bottom);
        }

        TextView tvName = (TextView) rowView.findViewById(R.id.text_player_name);
        TextView tvPlayerSelection = (TextView) rowView.findViewById(R.id.text_player_selection);
        tvName.setText(prediction.getUsername());
        tvPlayerSelection.setText(prediction.getTeamprefix().equals("") ? "-" : Constants.TEAM_INFO_MAP.get(prediction.getTeamprefix()).getTeamName());

        if(!teamName.equals("")){
            if(teamName.equals(prediction.getTeamprefix())){
                tvPlayerSelection.setTextColor(Color.parseColor("#013369"));
            }
        }

        llAllPredictionsTable.addView(rowView);
    }
}
