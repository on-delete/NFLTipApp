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
import com.andre.nfltipapp.tabview.fragments.model.Game;
import com.andre.nfltipapp.tabview.fragments.model.GamePredictions;

import java.util.ArrayList;
import java.util.List;

public class StatisticForGameActivity extends AppCompatActivity {

    private LinearLayout statisticGameTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_for_game);

        ArrayList<GamePredictions> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONLIST);
        Game game = getIntent().getParcelableExtra(Constants.GAME);
        String userId = getIntent().getStringExtra(Constants.UUID);

        ImageView awayTeamIcon = (ImageView) findViewById(R.id.away_team_icon_statistic);
        ImageView homeTeamIcon = (ImageView) findViewById(R.id.home_team_icon_statistic);
        this.statisticGameTable = (LinearLayout) findViewById(R.id.statistics_game_table_layout);

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

        List<GamePredictions> predictionListCopy = new ArrayList<>(predictionList);

        for(int i = 0; i < predictionList.size(); i++){
            GamePredictions predictionTemp = predictionList.get(i);
            if(predictionTemp.getUserid().equals(userId)){
                View view = initView(predictionTemp);
                view.setBackgroundResource(R.drawable.bottom_border);
                TextView textViewName = (TextView) view.findViewById(R.id.player_name_statistic) ;
                textViewName.setTypeface(null, Typeface.BOLD);

                predictionListCopy.remove(i);
            }
        }

        for(GamePredictions prediction : predictionListCopy){
            initView(prediction);
        }
    }

    private View initView (GamePredictions prediction){
        View rowView = getLayoutInflater().inflate(R.layout.statistic_for_game_table_row, null);

        TextView textViewName = (TextView) rowView.findViewById(R.id.player_name_statistic) ;
        textViewName.setText(prediction.getUsername());
        CheckBox awayTeamCheckbox = (CheckBox) rowView.findViewById(R.id.away_team_checkbox_statistic);
        awayTeamCheckbox.setEnabled(false);
        CheckBox homeTeamCheckbox = (CheckBox) rowView.findViewById(R.id.home_team_checkbox_statistic);
        homeTeamCheckbox.setEnabled(false);

        if(prediction.getPredicted()==1) {
            if(prediction.getHometeampredicted() == 1){
                homeTeamCheckbox.setChecked(true);
            }
            else {
                awayTeamCheckbox.setChecked(true);
            }
        }

        this.statisticGameTable.addView(rowView);

        return rowView;
    }
}
