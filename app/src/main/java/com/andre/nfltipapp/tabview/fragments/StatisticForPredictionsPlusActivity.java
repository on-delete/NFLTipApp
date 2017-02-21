package com.andre.nfltipapp.tabview.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.PredictionsPlusStatistic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andre on 16.02.2017.
 */

public class StatisticForPredictionsPlusActivity extends AppCompatActivity {

    private String teamName;
    private LinearLayout statisticsPlusTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_for_prediction_plus);

        ArrayList<PredictionsPlusStatistic> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONSPLUSLIST);
        this.teamName = getIntent().getStringExtra(Constants.TEAMNAME);
        String userId = getIntent().getStringExtra(Constants.USERID);
        String stateParcel = getIntent().getStringExtra(Constants.STATE);
        Constants.PREDICTIONS_PLUS_STATES state = Constants.PREDICTIONS_PLUS_STATES.valueOf(stateParcel);

        TextView stateText = (TextView) findViewById(R.id.state_text);
        ImageView teamIconDefault = (ImageView) findViewById(R.id.team_icon_statistic);
        this.statisticsPlusTable = (LinearLayout) findViewById(R.id.statistics_plus_table_layout);

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

        List<PredictionsPlusStatistic> predictionListCopy = new ArrayList<>(predictionList);

        for(int i = 0; i < predictionList.size(); i++){
            PredictionsPlusStatistic predictionTemp = predictionList.get(i);
            if(predictionTemp.getUserid().equals(userId)){
                View view = initView(predictionTemp);
                view.setBackgroundResource(R.drawable.bottom_border);
                TextView textViewName = (TextView) view.findViewById(R.id.player_name_statistic);
                textViewName.setTypeface(null, Typeface.BOLD);

                predictionListCopy.remove(i);
            }
        }

        for(PredictionsPlusStatistic prediction : predictionListCopy){
            initView(prediction);
        }
    }

    private View initView (PredictionsPlusStatistic prediction){
        View rowView = getLayoutInflater().inflate(R.layout.statistic_for_plus_table_row, null);

        TextView textViewName = (TextView) rowView.findViewById(R.id.player_name_statistic);
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

        return rowView;
    }
}
