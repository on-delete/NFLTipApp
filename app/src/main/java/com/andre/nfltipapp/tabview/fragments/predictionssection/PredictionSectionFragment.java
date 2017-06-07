package com.andre.nfltipapp.tabview.fragments.predictionssection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.DataUpdatedListener;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.model.PredictionsForWeek;
import com.andre.nfltipapp.tabview.fragments.model.PredictionBeforeSeason;

import java.util.List;

public class PredictionSectionFragment extends Fragment {

    private int lastExpandedPosition = -1;

    private ExpandableListView elvPredictions;

    private Activity activity;

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
        View rootView = inflater.inflate(R.layout.fragment_section_prediction, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.prediction_swipe_container);

        Bundle bundle = this.getArguments();
        final DataService dataService = bundle.getParcelable("dataService");

        elvPredictions = (ExpandableListView) rootView.findViewById(R.id.list_view_predictions);

        List<PredictionsForWeek> predictionsForWeekList = dataService.getData().getPredictionsForWeeks();
        List<PredictionBeforeSeason> predictionBeforeSeasonList = dataService.getData().getPredictionBeforeSeason();

        ExpandableListAdapter elvPredictionsAdapter = new PredictionsListViewAdapter(activity, predictionsForWeekList, predictionBeforeSeasonList, dataService.getUserId());
        elvPredictions.setAdapter(elvPredictionsAdapter);

        elvPredictions.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    elvPredictions.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataService.dataUpdated(null);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        dataService.addDataUpdateListener(new DataUpdatedListener() {
            @Override
            public void onDataUpdated(Data data) {
                System.out.println("update data prediction");
            }
        });

        return rootView;
    }
}
