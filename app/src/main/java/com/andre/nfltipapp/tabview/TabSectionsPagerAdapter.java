package com.andre.nfltipapp.tabview;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.tabview.fragments.predictionssection.PredictionSectionFragment;
import com.andre.nfltipapp.tabview.fragments.RankingSectionFragment;
import com.andre.nfltipapp.tabview.fragments.statisticssection.StatisticsSectionFragment;
import com.andre.nfltipapp.tabview.fragments.standingssection.StandingsSectionFragment;

public class TabSectionsPagerAdapter extends FragmentPagerAdapter {
    private String userId;
    private Bundle bundle = new Bundle();

    public TabSectionsPagerAdapter(FragmentManager fm, String userId) {
        super(fm);
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                bundle.putString(Constants.USERID, this.userId);
                RankingSectionFragment rankingSectionFragment = new RankingSectionFragment();
                rankingSectionFragment.setArguments(bundle);
                return rankingSectionFragment;
            case 1:
                bundle.putString(Constants.USERID, this.userId);
                PredictionSectionFragment predictionSectionFragment = new PredictionSectionFragment();
                predictionSectionFragment.setArguments(bundle);
                return predictionSectionFragment;
            case 2:
                bundle.putString(Constants.USERID, this.userId);
                StatisticsSectionFragment statisticsSectionFragment = new StatisticsSectionFragment();
                statisticsSectionFragment.setArguments(bundle);
                return statisticsSectionFragment;
            case 3:
                return new StandingsSectionFragment();
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
