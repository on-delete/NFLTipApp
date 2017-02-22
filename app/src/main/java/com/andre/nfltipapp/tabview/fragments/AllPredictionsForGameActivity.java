package com.andre.nfltipapp.tabview.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.model.GamePrediction;
import com.andre.nfltipapp.tabview.fragments.model.GamePredictionStatistic;

import java.util.ArrayList;
import java.util.List;

public class AllPredictionsForGameActivity extends AppCompatActivity {

    private LinearLayout llAllPredictionsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_for_game);

        ArrayList<GamePredictionStatistic> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONS);
        GamePrediction gamePrediction = getIntent().getParcelableExtra(Constants.GAME);
        String userId = getIntent().getStringExtra(Constants.USERID);

        ImageView ivAwayTeamIcon = (ImageView) findViewById(R.id.away_team_icon_statistic);
        ImageView ivHomeTeamIcon = (ImageView) findViewById(R.id.home_team_icon_statistic);
        this.llAllPredictionsTable = (LinearLayout) findViewById(R.id.statistics_game_table_layout);

        setTitle(gamePrediction.getAwayteam() + " vs " + gamePrediction.getHometeam());

        ivAwayTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(gamePrediction.getAwayteam()).getTeamIcon());
        ivHomeTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(gamePrediction.getHometeam()).getTeamIcon());

        if(gamePrediction.getAwaypoints() > gamePrediction.getHomepoints()){
            ivAwayTeamIcon.setBackground(getDrawable(R.drawable.back_green));
            ivHomeTeamIcon.setBackground(getDrawable(R.drawable.back_red));
        }
        else if (gamePrediction.getAwaypoints() < gamePrediction.getHomepoints()){
            ivAwayTeamIcon.setBackground(getDrawable(R.drawable.back_red));
            ivHomeTeamIcon.setBackground(getDrawable(R.drawable.back_green));
        }
        else{
            ivAwayTeamIcon.setBackground(getDrawable(R.drawable.back_yellow));
            ivHomeTeamIcon.setBackground(getDrawable(R.drawable.back_yellow));
        }

        List<GamePredictionStatistic> predictionListCopy = new ArrayList<>(predictionList);

        for(int i = 0; i < predictionList.size(); i++){
            GamePredictionStatistic predictionTemp = predictionList.get(i);
            if(predictionTemp.getUserid().equals(userId)){
                View view = initView(predictionTemp);
                view.setBackgroundResource(R.drawable.bottom_border);
                TextView tvName = (TextView) view.findViewById(R.id.player_name_statistic) ;
                tvName.setTypeface(null, Typeface.BOLD);

                predictionListCopy.remove(i);
            }
        }

        for(GamePredictionStatistic prediction : predictionListCopy){
            initView(prediction);
        }
    }

    private View initView (GamePredictionStatistic prediction){
        View rowView = getLayoutInflater().inflate(R.layout.statistic_for_game_table_row, null);

        TextView tvName = (TextView) rowView.findViewById(R.id.player_name_statistic) ;
        tvName.setText(prediction.getUsername());
        CheckBox cbAwayTeam = (CheckBox) rowView.findViewById(R.id.away_team_checkbox_statistic);
        cbAwayTeam.setEnabled(false);
        CheckBox cbHomeTeam = (CheckBox) rowView.findViewById(R.id.home_team_checkbox_statistic);
        cbHomeTeam.setEnabled(false);

        if(prediction.getPredicted()==1) {
            if(prediction.getHometeampredicted() == 1){
                cbHomeTeam.setChecked(true);
            }
            else {
                cbAwayTeam.setChecked(true);
            }
        }

        this.llAllPredictionsTable.addView(rowView);

        return rowView;
    }
}
