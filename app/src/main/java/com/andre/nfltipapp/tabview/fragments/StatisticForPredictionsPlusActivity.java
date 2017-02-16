package com.andre.nfltipapp.tabview.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.PredictionsPlusStatistic;

import java.util.ArrayList;

/**
 * Created by Andre on 16.02.2017.
 */

public class StatisticForPredictionsPlusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_for_prediction_plus);

        ArrayList<PredictionsPlusStatistic> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONSPLUSLIST);
        String teamName = getIntent().getStringExtra(Constants.TEAMNAME);
        String stateParcel = getIntent().getStringExtra(Constants.STATE);
        Constants.PREDICTIONS_PLUS_STATES state = Constants.PREDICTIONS_PLUS_STATES.valueOf(stateParcel);

        TextView stateText = (TextView) findViewById(R.id.state_text);
        ImageView teamIconDefault = (ImageView) findViewById(R.id.team_icon_statistic);
        LinearLayout statisticsPlusTable = (LinearLayout) findViewById(R.id.statistics_plus_table_layout);

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

        if(teamName.equals("")){
            teamIconDefault.setImageResource(R.drawable.default_icon);
        }
        else {
            teamIconDefault.setImageResource(Constants.TEAM_INFO_MAP.get(teamName).getTeamIcon());
        }

        for(PredictionsPlusStatistic prediction : predictionList){
            View rowView = getLayoutInflater().inflate(R.layout.statistic_for_plus_table_row, null);

            TextView textViewName = (TextView) rowView.findViewById(R.id.player_name_statistic) ;
            textViewName.setText(prediction.getUsername());

            ImageView teamIconPredicted = (ImageView) rowView.findViewById(R.id.player_pred_team_icon);

            if(!teamName.equals("")){
                if(teamName.equals(prediction.getTeamprefix())){
                    teamIconPredicted.setBackground(getDrawable(R.drawable.back_green));
                }
                else{
                    teamIconPredicted.setBackground(getDrawable(R.drawable.back_red));
                }
            }

            if(prediction.getTeamprefix().equals("")){
                teamIconPredicted.setImageResource(R.drawable.default_icon);
            }
            else {
                teamIconPredicted.setImageResource(Constants.TEAM_INFO_MAP.get(prediction.getTeamprefix()).getTeamIcon());
            }

            statisticsPlusTable.addView(rowView);
        }
    }
}
