package com.andre.nfltipapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.GamePredictions;

import java.util.ArrayList;

public class StatisticForGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_for_game);

        ArrayList<GamePredictions> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONLIST);
        Game game = getIntent().getParcelableExtra(Constants.GAME);

        ImageView awayTeamIcon = (ImageView) findViewById(R.id.away_team_icon_statistic);
        ImageView homeTeamIcon = (ImageView) findViewById(R.id.home_team_icon_statistic);
        TableLayout statisticGameTable = (TableLayout) findViewById(R.id.statistics_game_table_layout);

        setTitle(game.getAwayteam() + " vs " + game.getHometeam());

        awayTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(game.getAwayteam()).getTeamIcon());
        homeTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(game.getHometeam()).getTeamIcon());

        if(game.getAwaypoints() > game.getHomepoints()){
            awayTeamIcon.setBackground(getDrawable(R.drawable.back_green));
            homeTeamIcon.setBackground(getDrawable(R.drawable.back_red));
        }
        else if (game.getAwaypoints() < game.getHomepoints()){
            awayTeamIcon.setBackground(getDrawable(R.drawable.back_red));
            homeTeamIcon.setBackground(getDrawable(R.drawable.back_green));
        }
        else{
            awayTeamIcon.setBackground(getDrawable(R.drawable.back_yellow));
            homeTeamIcon.setBackground(getDrawable(R.drawable.back_yellow));
        }

        for(GamePredictions prediction : predictionList){
            View rowView = getLayoutInflater().inflate(R.layout.statistic_for_game_table_row, null);

            TextView textViewName = (TextView) rowView.findViewById(R.id.player_name_statistic) ;
            textViewName.setText(prediction.getUsername());
            CheckBox awayTeamCheckbox = (CheckBox) rowView.findViewById(R.id.away_team_checkbox_statistic);
            awayTeamCheckbox.setEnabled(false);
            CheckBox homeTeamCheckbox = (CheckBox) rowView.findViewById(R.id.home_team_checkbox_statistic);
            homeTeamCheckbox.setEnabled(false);

            if(prediction.getPredicted()==1) {
                if (game.getAwaypoints() > game.getHomepoints() && prediction.getHometeampredicted() == 0) {
                    awayTeamCheckbox.setChecked(true);
                } else if (game.getAwaypoints() < game.getHomepoints() && prediction.getHometeampredicted() == 1) {
                    homeTeamCheckbox.setChecked(true);
                }
            }

            statisticGameTable.addView(rowView);
        }
    }
}
