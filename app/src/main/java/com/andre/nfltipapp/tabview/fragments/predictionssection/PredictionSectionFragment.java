package com.andre.nfltipapp.tabview.fragments.predictionssection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        final View rootView = inflater.inflate(R.layout.fragment_section_prediction, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.prediction_swipe_container);

        Bundle bundle = this.getArguments();
        final DataService dataService = bundle.getParcelable("dataService");

        elvPredictions = (ExpandableListView) rootView.findViewById(R.id.list_view_predictions);

        List<PredictionsForWeek> predictionsForWeekList = dataService.getData().getPredictionsForWeeks();
        List<PredictionBeforeSeason> predictionBeforeSeasonList = dataService.getData().getPredictionBeforeSeason();

        final PredictionsListViewAdapter elvPredictionsAdapter = new PredictionsListViewAdapter(activity, predictionsForWeekList, predictionBeforeSeasonList, dataService.getUserId());
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
                dataService.dataUpdate(activity.getApplicationContext());
            }
        });

        dataService.addDataUpdateListener(new DataUpdatedListener() {
            @Override
            public void onDataUpdated(Data data) {
                elvPredictionsAdapter.updateLists(data.getPredictionsForWeeks(), data.getPredictionBeforeSeason());
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
}
