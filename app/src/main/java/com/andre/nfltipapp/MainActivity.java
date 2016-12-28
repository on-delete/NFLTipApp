package com.andre.nfltipapp;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
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
        String name = intent.getStringExtra(Constants.NAME) == null ? "admin" : intent.getStringExtra(Constants.NAME);
        String uuid = intent.getStringExtra(Constants.UUID) == null ? "10" : intent.getStringExtra(Constants.UUID);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

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

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new RankingSectionFragment();

                default:
                    return new PredictionSectionFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "Ranking";
            }
            else{
                return "Prognose";
            }
        }
    }

    public static class RankingSectionFragment extends Fragment {

        TableLayout table;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_ranking, container, false);

            table = (TableLayout) rootView.findViewById(R.id.rankingTable);
            table.removeViews(1, table.getChildCount() - 1);

            Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
            List<Ranking> rankingList = data.getRanking();

            for(Ranking rankingEntry : rankingList){
                View row = inflater.inflate(R.layout.table_row, container, false);

                TextView textViewRanking = (TextView) row.findViewById(R.id.table_text_ranking) ;
                textViewRanking.setText(rankingEntry.getPlace());
                TextView textViewName = (TextView) row.findViewById(R.id.table_text_name) ;
                textViewName.setText(rankingEntry.getName());
                TextView textViewPoints = (TextView) row.findViewById(R.id.table_text_points) ;
                textViewPoints.setText(rankingEntry.getPoints());

                table.addView(row);
            }

            return rootView;
        }
    }

    public static class PredictionSectionFragment extends Fragment {

        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_prediction, container, false);

            expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);

            Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
            List<Prediction> predictionList = data.getPredictions();

            expandableListAdapter = new CustomExpandableListAdapter(getActivity().getApplicationContext(), predictionList);
            expandableListView.setAdapter(expandableListAdapter);

            return rootView;
        }
    }
}
