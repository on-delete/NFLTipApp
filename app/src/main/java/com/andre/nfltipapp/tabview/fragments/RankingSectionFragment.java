package com.andre.nfltipapp.tabview.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.DataUpdatedListener;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.model.Ranking;

import java.util.List;

public class RankingSectionFragment extends Fragment {

    private LayoutInflater inflater;
    private ViewGroup container;
    private Activity activity;

    private LinearLayout llTable;

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
        final DataService dataService = bundle.getParcelable("dataService");
        userId = dataService.getUserId();

        llTable = (LinearLayout) rootView.findViewById(R.id.linear_ranking_table);

        initRankingView(dataService.getData().getRanking());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataService.dataUpdate(activity.getApplicationContext());
            }
        });

        dataService.addDataUpdateListener(new DataUpdatedListener() {
            @Override
            public void onDataUpdated(Data data) {
                llTable.removeAllViews();
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
        });


        return rootView;
    }

    private void initRankingView(List<Ranking> rankingList) {
        for(Ranking rankingEntry : rankingList){
            View rowView = inflater.inflate(R.layout.ranking_table_row, container, false);

            TextView tvName = (TextView) rowView.findViewById(R.id.text_player_name);
            tvName.setText(rankingEntry.getName());
            if(userId != null && userId.equals(rankingEntry.getUserid())){
                tvName.setTypeface(null, Typeface.BOLD);
            }
            TextView tvPoints = (TextView) rowView.findViewById(R.id.text_player_points) ;
            tvPoints.setText(rankingEntry.getPoints());

            llTable.addView(rowView);
        }
    }
}
