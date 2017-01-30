package com.andre.nfltipapp.tabview.fragments.statisticssection;

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
import com.andre.nfltipapp.model.Prediction;

import java.util.List;

/**
 * Created by Andre on 30.01.2017.
 */

public class StatisticsSectionFragment extends Fragment {

    private int lastExpandedPosition = -1;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;

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
        View rootView = inflater.inflate(R.layout.fragment_section_statistics, container, false);

        expandableListView = (ExpandableListView) rootView.findViewById(R.id.statisticsListView);

        Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
        List<Prediction> predictionList = data.getPredictions();

        expandableListAdapter = new StatisticsListViewAdapter(activity, predictionList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        return rootView;
    }
}
