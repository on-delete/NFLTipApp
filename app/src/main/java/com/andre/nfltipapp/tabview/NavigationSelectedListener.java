package com.andre.nfltipapp.tabview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.DashboardFragment;
import com.andre.nfltipapp.tabview.fragments.RankingSectionFragment;
import com.andre.nfltipapp.tabview.fragments.predictionssection.PredictionSectionFragment;
import com.andre.nfltipapp.tabview.fragments.standingssection.StandingsSectionFragment;
import com.andre.nfltipapp.tabview.fragments.statisticssection.StatisticsSectionFragment;

public class NavigationSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Bundle bundle = new Bundle();

    private final FragmentManager fm;
    private final DataService dataService;

    public NavigationSelectedListener(FragmentManager fm, DataService dataService) {
        this.fm = fm;
        this.dataService = dataService;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment;
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                selectedFragment = fm.findFragmentByTag("dashboard");
                    if(selectedFragment == null) {
                    selectedFragment = new DashboardFragment();
                    selectedFragment.setArguments(bundle);
                }
                setFragment(selectedFragment, "dashboard");
                return true;
            case R.id.navigation_standings:
                selectedFragment = fm.findFragmentByTag("standings");
                if(selectedFragment == null) {
                    selectedFragment = new StandingsSectionFragment();
                    selectedFragment.setArguments(bundle);
                }
                setFragment(selectedFragment, "standings");
                return true;
            case R.id.navigation_ranking:
                selectedFragment = fm.findFragmentByTag("ranking");
                if(selectedFragment == null) {
                    selectedFragment = new RankingSectionFragment();
                    selectedFragment.setArguments(bundle);
                }
                setFragment(selectedFragment, "ranking");
                return true;
            case R.id.navigation_predictions:
                selectedFragment = fm.findFragmentByTag("predictions");
                if(selectedFragment == null) {
                    selectedFragment = new PredictionSectionFragment();
                    selectedFragment.setArguments(bundle);
                }
                setFragment(selectedFragment, "predictions");
                return true;
            case R.id.navigation_statistics:
                selectedFragment = fm.findFragmentByTag("statistics");
                if(selectedFragment == null) {
                    selectedFragment = new StatisticsSectionFragment();
                    selectedFragment.setArguments(bundle);
                }
                setFragment(selectedFragment, "statistics");
                return true;
        }
        return false;
    }

    private void setFragment(Fragment selectedFragment, String tag) {
        bundle.putParcelable("dataService", dataService);
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, selectedFragment, tag).commit();
    }
}
