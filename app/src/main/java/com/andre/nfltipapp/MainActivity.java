package com.andre.nfltipapp;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.model.Prediction;
import com.andre.nfltipapp.model.Ranking;

import java.util.List;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String userName = intent.getStringExtra(Constants.NAME) == null ? "admin" : intent.getStringExtra(Constants.NAME);
        String uuid = intent.getStringExtra(Constants.UUID) == null ? "10" : intent.getStringExtra(Constants.UUID);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), userName, uuid);

        final ActionBar actionBar = getActionBar();

        actionBar.setHomeButtonEnabled(false);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        private String userName;
        private String uuid;
        private Bundle bundle = new Bundle();

        public AppSectionsPagerAdapter(FragmentManager fm, String userName, String uuid) {

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
                default:
                    return new StatisticsSectionFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "Ranking";
            }
            else if(position==1){
                return "Prognosen";
            }
            else{
                return "Statistik";
            }
        }
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

                TextView textViewRanking = (TextView) rowView.findViewById(R.id.table_text_ranking) ;
                textViewRanking.setText(String.valueOf(rankingList.indexOf(rankingEntry) + 1));
                TextView textViewName = (TextView) rowView.findViewById(R.id.table_text_name) ;
                textViewName.setText(rankingEntry.getName());
                TextView textViewPoints = (TextView) rowView.findViewById(R.id.table_text_points) ;
                textViewPoints.setText(rankingEntry.getPoints());

                if(rankingEntry.getName().equals(userName)){
                    textViewRanking.setTypeface(textViewRanking.getTypeface(), Typeface.BOLD);
                    textViewRanking.setBackgroundResource(R.drawable.back_grey);
                    textViewName.setTypeface(textViewRanking.getTypeface(), Typeface.BOLD);
                    textViewName.setBackgroundResource(R.drawable.back_grey);
                    textViewPoints.setTypeface(textViewRanking.getTypeface(), Typeface.BOLD);
                    textViewPoints.setBackgroundResource(R.drawable.back_grey);
                }

                table.addView(rowView);
            }

            return rootView;
        }
    }

    public static class PredictionSectionFragment extends Fragment {

        int lastExpandedPosition = -1;
        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_statistics, container, false);

            expandableListView = (ExpandableListView) rootView.findViewById(R.id.statisticsListView);

            Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
            List<Prediction> predictionList = data.getPredictions();

            expandableListAdapter = new StatisticsListViewAdapter(getActivity().getApplicationContext(), predictionList);
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
}
