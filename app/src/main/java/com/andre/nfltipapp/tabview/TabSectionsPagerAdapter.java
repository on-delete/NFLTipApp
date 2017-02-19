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
