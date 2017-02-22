package com.andre.nfltipapp.tabview.fragments.predictionssection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.model.PredictionsForWeek;
import com.andre.nfltipapp.tabview.fragments.model.PredictionBeforeSeason;

import java.util.List;

/**
 * Created by Andre on 30.01.2017.
 */

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

        Bundle bundle = this.getArguments();
        String userId = "";
        if (bundle != null) {
            userId = bundle.getString(Constants.USERID);
        }

        elvPredictions = (ExpandableListView) rootView.findViewById(R.id.predictionsListView);

        Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
        List<PredictionsForWeek> predictionsForWeekList = data.getPredictionsForWeeks();
        List<PredictionBeforeSeason> predictionBeforeSeasonList = data.getPredictionBeforeSeason();

        ExpandableListAdapter elvPredictionsAdapter = new PredictionsListViewAdapter(activity, predictionsForWeekList, predictionBeforeSeasonList, userId);
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

        return rootView;
    }
}
