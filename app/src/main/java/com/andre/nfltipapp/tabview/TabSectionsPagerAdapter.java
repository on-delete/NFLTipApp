package com.andre.nfltipapp.tabview;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.predictionssection.PredictionSectionFragment;
import com.andre.nfltipapp.tabview.fragments.RankingSectionFragment;
import com.andre.nfltipapp.tabview.fragments.statisticssection.StatisticsSectionFragment;
import com.andre.nfltipapp.tabview.fragments.standingssection.StandingsSectionFragment;

public class TabSectionsPagerAdapter extends FragmentPagerAdapter {
    private DataService dataService;
    private Bundle bundle = new Bundle();

    public TabSectionsPagerAdapter(FragmentManager fm, DataService dataService) {
        super(fm);
        this.dataService = dataService;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                bundle.putParcelable("dataService", this.dataService);
                StandingsSectionFragment standingsSectionFragment = new StandingsSectionFragment();
                standingsSectionFragment.setArguments(bundle);
                return standingsSectionFragment;
            case 1:
                bundle.putParcelable("dataService", this.dataService);
                RankingSectionFragment rankingSectionFragment = new RankingSectionFragment();
                rankingSectionFragment.setArguments(bundle);
                return rankingSectionFragment;
            case 2:
                bundle.putParcelable("dataService", this.dataService);
                PredictionSectionFragment predictionSectionFragment = new PredictionSectionFragment();
                predictionSectionFragment.setArguments(bundle);
                return predictionSectionFragment;
            case 3:
                bundle.putParcelable("dataService", this.dataService);
                StatisticsSectionFragment statisticsSectionFragment = new StatisticsSectionFragment();
                statisticsSectionFragment.setArguments(bundle);
                return statisticsSectionFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return Constants.TAB_NAME_LIST.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
