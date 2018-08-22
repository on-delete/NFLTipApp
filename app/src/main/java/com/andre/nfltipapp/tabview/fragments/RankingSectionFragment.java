package com.andre.nfltipapp.tabview.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.DataUpdatedListener;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.model.Ranking;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class RankingSectionFragment extends Fragment {

    private LayoutInflater inflater;
    private ViewGroup container;
    private Activity activity;

    private DataService dataService;
    private DataUpdatedListener dataUpdatedListener;

    private GridLayout glPlayerContainer;

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

        View rootView = inflater.inflate(R.layout.fragment_section_ranking, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.ranking_swipe_container);

        Bundle bundle = this.getArguments();
        dataService = bundle.getParcelable("dataService");
        userId = dataService.getUserId();

        glPlayerContainer = (GridLayout) rootView.findViewById(R.id.ranking_player_cards_container);

        initRankingView(dataService.getData().getRanking());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataService.dataUpdate(activity.getApplicationContext());
            }
        });

        DataUpdatedListener dataUpdatedListener = new DataUpdatedListener() {
            @Override
            public void onDataUpdated(Data data) {
                glPlayerContainer.removeAllViews();
                initRankingView(data.getRanking());

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

    private void initRankingView(List<Ranking> rankingList) {
        for(Ranking rankingEntry : rankingList){
            View playerCardView = inflater.inflate(R.layout.ranking_player_card, container, false);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = WRAP_CONTENT;
            params.setGravity(Gravity.FILL);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            playerCardView.setLayoutParams(params);

            TextView tvName = (TextView) playerCardView.findViewById(R.id.player_card_name);
            tvName.setText(rankingEntry.getName());
            if(userId != null && userId.equals(rankingEntry.getUserid())){
                playerCardView.setBackgroundResource(R.drawable.back_dark_grey_with_border_bottom);
            }
            TextView tvPoints = (TextView) playerCardView.findViewById(R.id.player_card_points) ;
            tvPoints.setText(rankingEntry.getPoints() + " P.");

            glPlayerContainer.addView(playerCardView, glPlayerContainer.getChildCount());
        }
    }
}
