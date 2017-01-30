package com.andre.nfltipapp.tabview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.model.Ranking;
import com.andre.nfltipapp.model.Standing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andre on 15.01.2017.
 */

public class TabSectionsPagerAdapter extends FragmentPagerAdapter {
    private String userName;
    private String uuid;
    private Bundle bundle = new Bundle();

    public TabSectionsPagerAdapter(FragmentManager fm, String userName, String uuid) {

        super(fm);
        this.userName = userName;
        this.uuid = uuid;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                bundle.putString("username", this.userName);
                RankingSectionFragment rankingSectionFragment = new RankingSectionFragment();
                rankingSectionFragment.setArguments(bundle);
                return rankingSectionFragment;
            case 1:
                bundle.putString("uuid", this.uuid);
                PredictionSectionFragment predictionSectionFragment = new PredictionSectionFragment();
                predictionSectionFragment.setArguments(bundle);
                return predictionSectionFragment;
            case 2:
                return new StatisticsSectionFragment();
            default:
                return new StandingsSectionFragment();
        }
    }

    @Override
    public int getCount() {
        return Constants.TAB_NAME_LIST.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Constants.TAB_NAME_LIST[position];
    }

    public static class RankingSectionFragment extends Fragment {

        TableLayout table;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_ranking, container, false);

            Bundle bundle = this.getArguments();
            String userName = "";
            if (bundle != null) {
                userName = bundle.getString("username");
            }

            table = (TableLayout) rootView.findViewById(R.id.rankingTable);
            table.removeViews(1, table.getChildCount() - 1);

            Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
            List<Ranking> rankingList = data.getRanking();

            for(Ranking rankingEntry : rankingList){
                View rowView = inflater.inflate(R.layout.table_row, container, false);

                TextView textViewName = (TextView) rowView.findViewById(R.id.table_text_name) ;
                textViewName.setText(rankingEntry.getName());
                TextView textViewPoints = (TextView) rowView.findViewById(R.id.table_text_points) ;
                textViewPoints.setText(rankingEntry.getPoints());

                table.addView(rowView);
            }

            return rootView;
        }
    }

    public static class PredictionSectionFragment extends Fragment {

        int lastExpandedPosition = -1;
        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;

        Activity activity;

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
            String uuid = "";
            if (bundle != null) {
                uuid = bundle.getString("uuid");
            }

            expandableListView = (ExpandableListView) rootView.findViewById(R.id.predictionsListView);

            Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
            List<Prediction> predictionList = data.getPredictions();

            expandableListAdapter = new PredictionsListViewAdapter(getActivity().getApplicationContext(), predictionList, uuid);
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

    public static class StatisticsSectionFragment extends Fragment {

        int lastExpandedPosition = -1;
        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;

        Activity activity;

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

    public static class StandingsSectionFragment extends Fragment{

        Activity activity;

        Button afcButton;
        Button nfcButton;

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
            View rootView = inflater.inflate(R.layout.fragment_section_standings, container, false);

            Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
            final ArrayList<Standing> standingsList = data.getStandings();

            afcButton = (Button) rootView.findViewById(R.id.afc_button);
            afcButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTableToAFC(new ArrayList<>(standingsList.subList(0, 16)));
                }
            });
            nfcButton = (Button) rootView.findViewById(R.id.nfc_button);
            nfcButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTableToNFC(new ArrayList<>(standingsList.subList(16,32)));
                }
            });

            changeTableToAFC(new ArrayList<>(standingsList.subList(0,16)));

            return rootView;
        }

        private void changeTableToAFC(ArrayList<Standing> afcStanding){
            afcButton.setBackgroundColor(Color.parseColor("#B50023"));
            afcButton.setTextColor(Color.parseColor("#FAFAFA"));
            nfcButton.setBackgroundColor(Color.parseColor("#E6E6E6"));
            nfcButton.setTextColor(Color.parseColor("#151515"));

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.AFC_STANDINGS, afcStanding);
            AfcTableFragment afcTableFragment = new AfcTableFragment();
            afcTableFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().replace(R.id.table_fragment, afcTableFragment).commit();
        }

        private void changeTableToNFC(ArrayList<Standing> nfcStanding){
            afcButton.setBackgroundColor(Color.parseColor("#E6E6E6"));
            afcButton.setTextColor(Color.parseColor("#151515"));
            nfcButton.setBackgroundColor(Color.parseColor("#004079"));
            nfcButton.setTextColor(Color.parseColor("#FAFAFA"));

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.NFC_STANDINGS, nfcStanding);
            NfcTableFragment nfcTableFragment = new NfcTableFragment();
            nfcTableFragment.setArguments(bundle);

            getFragmentManager().beginTransaction().replace(R.id.table_fragment, nfcTableFragment).commit();
        }
    }
}
