package com.andre.nfltipapp.tabview.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.DataUpdatedListener;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.model.GamePrediction;
import com.andre.nfltipapp.tabview.fragments.model.PredictionsForWeek;
import com.andre.nfltipapp.tabview.fragments.model.Ranking;
import com.andre.nfltipapp.tabview.fragments.predictionssection.PredictionSectionFragment;

import java.util.List;

public class DashboardFragment extends Fragment {

    private LayoutInflater inflater;
    private ViewGroup container;
    private View rootView;
    private Activity activity;

    private DataService dataService;
    private DataUpdatedListener dataUpdatedListener;

    private String userId;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if (context instanceof Activity){
            activity=(Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;

        rootView = inflater.inflate(R.layout.fragment_section_dashboard, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.dashboard_swipe_container);

        Bundle bundle = this.getArguments();
        dataService = bundle.getParcelable("dataService");
        userId = dataService.getUserId();

        initDashboardView(dataService.getData());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataService.dataUpdate(activity.getApplicationContext());
            }
        });

        DataUpdatedListener dataUpdatedListener = new DataUpdatedListener() {
            @Override
            public void onDataUpdated(Data data) {
                initDashboardView(dataService.getData());

                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String error) {
                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(swipeRefreshLayout , error, Snackbar.LENGTH_LONG).show();
                    Log.d(Constants.TAG, error);
                }
            }
        };
        dataService.addDataUpdateListener(dataUpdatedListener);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(dataService != null && dataUpdatedListener != null) {
            dataService.removeDataUpdateListener(dataUpdatedListener);
        }
    }

    private void initDashboardView(Data data) {
        String week = initWeekSection(data.getPredictionsForWeeks());
        initRankingSection(data.getRanking());
        initPredictedSection(data.getPredictionsForWeeks(), week);
    }

    private String initWeekSection(List<PredictionsForWeek> predictions) {
        String week = "";

        for(PredictionsForWeek prediction : predictions) {
            boolean finished = true;

            for(GamePrediction game : prediction.getGamePredictions()) {
                if(game.isFinished() == 0) {
                    finished = false;
                }
            }

            if(!finished) {
                week = prediction.getWeek();
                break;
            }
            if(prediction.getWeek().equals("21")){
                week = "21";
                break;
            }
        }

        TextView tvWeek = rootView.findViewById(R.id.text_week);
        tvWeek.setText("Woche " + week);

        //TODO: simple solution because we don't have the superbowl date yet, should be coming from server or whatever
        long millisSuperBowl = 1549152000000L;

        long currentMillis = System.currentTimeMillis();

        long diff = millisSuperBowl - currentMillis;

        long days = 0;
        if (diff >= 0) {
            days = diff / 1000 / 60 / 60 / 24;
        }

        TextView tvSuperBowlDays = rootView.findViewById(R.id.text_days_to_superbowl);
        tvSuperBowlDays.setText(days + " Tage bis zum Superbowl");

        return week;
    }

    private void initRankingSection(List<Ranking> ranking) {
        TextView tvRanking = rootView.findViewById(R.id.text_own_ranking);
        TextView tvOwnPoints = rootView.findViewById(R.id.text_own_points);
        TextView tvLeadPoints = rootView.findViewById(R.id.text_lead_points);
        TextView tvMissingPoints = rootView.findViewById(R.id.text_missing_points);
        LinearLayout llGoToRanking = rootView.findViewById(R.id.button_go_to_ranking);

        for(int i = 0; i<ranking.size(); i++) {
            Ranking rankingTemp = ranking.get(i);
            if(userId.equals(rankingTemp.getUserid())) {
                tvRanking.setText((i + 1) + ". Platz");
                tvOwnPoints.setText(rankingTemp.getPoints() + " Punkte");
                if(!(i + 1 == ranking.size())){
                    tvLeadPoints.setVisibility(View.VISIBLE);
                    int lead = Integer.parseInt(rankingTemp.getPoints()) - Integer.parseInt(ranking.get(i+1).getPoints());
                    tvLeadPoints.setText(lead + " Punkte Vorsprung");
                } else {
                    tvLeadPoints.setVisibility(View.INVISIBLE);
                }
                if(!(i==0)) {
                    tvMissingPoints.setVisibility(View.VISIBLE);
                    int missing = Integer.parseInt(ranking.get(i-1).getPoints()) - Integer.parseInt(rankingTemp.getPoints());
                    tvMissingPoints.setText(missing + " Punkte bis zum nächsten Platz");
                } else {
                    tvMissingPoints.setVisibility(View.INVISIBLE);
                }
                break;
            }
        }

        llGoToRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = getFragmentManager().findFragmentByTag("ranking");
                if(selectedFragment == null) {
                    selectedFragment = new RankingSectionFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("dataService", dataService);
                selectedFragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, selectedFragment, "ranking").commit();
            }
        });
    }

    private void initPredictedSection(List<PredictionsForWeek> predictions, String week) {
        TextView tvPredicted = rootView.findViewById(R.id.text_predicted);
        LinearLayout llBackgroundPRedicted = rootView.findViewById(R.id.background_predicted);
        ImageView ivPredicted = rootView.findViewById(R.id.image_predicted);
        Button btGoToPrediction = rootView.findViewById(R.id.button_go_to_prediction);
        boolean allPredicted = true;

        for(PredictionsForWeek prediction : predictions) {
            if(prediction.getWeek().equals(week)) {
                for(GamePrediction games : prediction.getGamePredictions()) {
                    if(games.hasPredicted() == 0) {
                        allPredicted = false;
                        break;
                    }
                }
            }
        }

        if(allPredicted) {
            tvPredicted.setText("Du hast bereits alles getippt");
            tvPredicted.setTextColor(Color.parseColor("#f0f0f0"));
            llBackgroundPRedicted.setBackgroundColor(Color.parseColor("#d50a0a"));
            ivPredicted.setBackgroundResource(R.drawable.haken);
        } else {
            tvPredicted.setText("Du hast noch nicht alles getippt");
            tvPredicted.setTextColor(Color.parseColor("#b2b2b2"));
            llBackgroundPRedicted.setBackgroundColor(Color.parseColor("#3c3c3c"));
            ivPredicted.setBackgroundResource(R.drawable.fragezeichen);
        }

        btGoToPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = getFragmentManager().findFragmentByTag("predictions");
                if(selectedFragment == null) {
                    selectedFragment = new PredictionSectionFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putParcelable("dataService", dataService);
                selectedFragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, selectedFragment, "predictions").commit();
            }
        });
    }
}
