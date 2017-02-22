package com.andre.nfltipapp.tabview.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.PredictionsBeforeSeasonStatistic;

import java.util.ArrayList;
import java.util.List;

public class AllPredictionsBeforeSeasonActivity extends AppCompatActivity {

    private String teamName;
    private LinearLayout llAllPredictionsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_for_prediction_plus);

        ArrayList<PredictionsBeforeSeasonStatistic> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONS_BEFORE_SEASON);
        this.teamName = getIntent().getStringExtra(Constants.TEAMNAME);
        String userId = getIntent().getStringExtra(Constants.USERID);
        String predictionTypeParcel = getIntent().getStringExtra(Constants.PREDICTION_TYPE_STRING);
        Constants.PREDICTION_TYPE predictionType = Constants.PREDICTION_TYPE.valueOf(predictionTypeParcel);

        TextView tvPredictionType = (TextView) findViewById(R.id.state_text);
        ImageView ivTeamIcon = (ImageView) findViewById(R.id.team_icon_statistic);
        this.llAllPredictionsTable = (LinearLayout) findViewById(R.id.statistics_plus_table_layout);

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
            ivTeamIcon.setImageResource(R.drawable.default_icon);
        }
        else {
            ivTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(teamName).getTeamIcon());
        }

        List<PredictionsBeforeSeasonStatistic> predictionListCopy = new ArrayList<>(predictionList);

        for(int i = 0; i < predictionList.size(); i++){
            PredictionsBeforeSeasonStatistic predictionTemp = predictionList.get(i);
            if(predictionTemp.getUserid().equals(userId)){
                View view = initView(predictionTemp);
                view.setBackgroundResource(R.drawable.bottom_border);
                TextView tvName = (TextView) view.findViewById(R.id.player_name_statistic);
                tvName.setTypeface(null, Typeface.BOLD);

                predictionListCopy.remove(i);
            }
        }

        for(PredictionsBeforeSeasonStatistic prediction : predictionListCopy){
            initView(prediction);
        }
    }

    private View initView (PredictionsBeforeSeasonStatistic prediction){
        @SuppressLint("InflateParams") View rowView = getLayoutInflater().inflate(R.layout.statistic_for_plus_table_row, null);

        TextView tvName = (TextView) rowView.findViewById(R.id.player_name_statistic);
        tvName.setText(prediction.getUsername());

        ImageView ivTeamIconPredicted = (ImageView) rowView.findViewById(R.id.player_pred_team_icon);

        if(!teamName.equals("")){
            if(teamName.equals(prediction.getTeamprefix())){
                ivTeamIconPredicted.setBackground(getDrawable(R.drawable.back_green));
            }
            else{
                ivTeamIconPredicted.setBackground(getDrawable(R.drawable.back_red));
            }
        }

        if(prediction.getTeamprefix().equals("")){
            ivTeamIconPredicted.setImageResource(R.drawable.default_icon);
        }
        else {
            ivTeamIconPredicted.setImageResource(Constants.TEAM_INFO_MAP.get(prediction.getTeamprefix()).getTeamIcon());
        }

        llAllPredictionsTable.addView(rowView);

        return rowView;
    }
}
