package com.andre.nfltipapp.tabview.fragments;

import android.graphics.Color;
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
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.tabview.fragments.model.GamePrediction;
import com.andre.nfltipapp.tabview.fragments.model.GamePredictionStatistic;

import java.util.ArrayList;
import java.util.List;

public class AllPredictionsForGameActivity extends AppCompatActivity {

    private LinearLayout llAllPredictionsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_predictions_for_game);

        ArrayList<GamePredictionStatistic> predictionList = getIntent().getParcelableArrayListExtra(Constants.PREDICTIONS);
        GamePrediction gamePrediction = getIntent().getParcelableExtra(Constants.GAME);
        String userId = getIntent().getStringExtra(Constants.USERID);

        TextView tvGametime = (TextView) findViewById(R.id.text_game_time);
        TextView tvTeamNameAway = (TextView) findViewById(R.id.text_team_name_away);
        TextView tvTeamNameHome = (TextView) findViewById(R.id.text_team_name_home);
        this.llAllPredictionsTable = (LinearLayout) findViewById(R.id.linear_predictions_for_game_table);

        setTitle(gamePrediction.getAwayteam() + " vs " + gamePrediction.getHometeam());

        tvGametime.setText(Utils.getGameDay(gamePrediction.getGamedatetime()) + " | " + Utils.getGameTime(gamePrediction.getGamedatetime()));

        tvTeamNameAway.setText(gamePrediction.getAwayteam());
        tvTeamNameHome.setText(gamePrediction.getHometeam());

        if(gamePrediction.getAwaypoints() > gamePrediction.getHomepoints()){
            tvTeamNameAway.setTextColor(Color.parseColor("#013369"));
        }
        else if (gamePrediction.getAwaypoints() < gamePrediction.getHomepoints()){
            tvTeamNameHome.setTextColor(Color.parseColor("#013369"));
        }

        for(GamePredictionStatistic prediction : predictionList){
            initView(prediction, gamePrediction, userId);
        }
    }

    private View initView (GamePredictionStatistic prediction, GamePrediction gamePrediction, String userId){
        View rowView = getLayoutInflater().inflate(R.layout.statistic_prediction_for_game_table_row, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 0, 0, 10);
        rowView.setLayoutParams(layoutParams);

        if(userId.equals(prediction.getUserid())) {
            rowView.setBackgroundResource(R.drawable.back_dark_grey_with_left_bottom);
        }

        TextView tvName = (TextView) rowView.findViewById(R.id.text_player_name) ;
        tvName.setText(prediction.getUsername());

        ImageView ivSternAway = (ImageView) rowView.findViewById(R.id.image_stern_away);
        ImageView ivSternHome = (ImageView) rowView.findViewById(R.id.image_stern_home);

        if(prediction.getPredicted()==1) {
            if (prediction.getHometeampredicted() == 1) {
                ivSternHome.setBackgroundResource(R.drawable.stern_blau);
            } else {
                ivSternAway.setBackgroundResource(R.drawable.stern_blau);
            }
        }

        this.llAllPredictionsTable.addView(rowView);

        return rowView;
    }
}
