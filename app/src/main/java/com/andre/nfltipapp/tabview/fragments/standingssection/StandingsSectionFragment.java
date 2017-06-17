package com.andre.nfltipapp.tabview.fragments.standingssection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.DataUpdatedListener;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.standingssection.model.Standing;

import java.util.ArrayList;

public class StandingsSectionFragment extends Fragment {

    private Button btAfc;
    private Button btNfc;

    private  AfcTableFragment afcTableFragment;
    private  NfcTableFragment nfcTableFragment;

    private Bundle bundle = new Bundle();
    private Activity activity;

    private int selectedFragment = 0;
    private final int BOTTOM_AFC_RANGE = 0;
    private final int UPPER_AFC_RANGE = 16;
    private final int UPPER_NFC_RANGE = 32;

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
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.standings_swipe_container);

        Bundle bundle = this.getArguments();
        final DataService dataService = bundle.getParcelable("dataService");

        ArrayList<Standing> standingsList = dataService.getData().getStandings();

        initFragments(new ArrayList<>(standingsList.subList(BOTTOM_AFC_RANGE, UPPER_AFC_RANGE)), new ArrayList<>(standingsList.subList(UPPER_AFC_RANGE, UPPER_NFC_RANGE)));

        btAfc = (Button) rootView.findViewById(R.id.button_afc);
        btAfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTableToAfc();
            }
        });
        btNfc = (Button) rootView.findViewById(R.id.button_nfc);
        btNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTableToNfc();
            }
        });

        changeTableToAfc();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataService.dataUpdate(activity.getApplicationContext());
            }
        });

        dataService.addDataUpdateListener(new DataUpdatedListener() {
            @Override
            public void onDataUpdated(Data data) {
                ArrayList<Standing> standingsList = data.getStandings();
                initFragments(new ArrayList<>(standingsList.subList(BOTTOM_AFC_RANGE, UPPER_AFC_RANGE)), new ArrayList<>(standingsList.subList(UPPER_AFC_RANGE, UPPER_NFC_RANGE)));
                if(selectedFragment == 0){
                    changeTableToAfc();
                }
                else{
                    changeTableToNfc();
                }

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

    private void changeTableToAfc(){
        selectedFragment = 0;

        btAfc.setBackgroundColor(Color.parseColor("#B50023"));
        btAfc.setTextColor(Color.parseColor(Constants.TABLE_SELECTED_TEXT_COLOR));
        btNfc.setBackgroundColor(Color.parseColor(Constants.DEFAULT_BACKGROUND_COLOR));
        btNfc.setTextColor(Color.parseColor(Constants.TABLE_DEFAULT_TEXT_COLOR));

        getFragmentManager().beginTransaction().replace(R.id.frame_table, afcTableFragment).commit();
    }

    private void changeTableToNfc(){
        selectedFragment = 1;

        btAfc.setBackgroundColor(Color.parseColor(Constants.DEFAULT_BACKGROUND_COLOR));
        btAfc.setTextColor(Color.parseColor(Constants.TABLE_DEFAULT_TEXT_COLOR));
        btNfc.setBackgroundColor(Color.parseColor("#004079"));
        btNfc.setTextColor(Color.parseColor(Constants.TABLE_SELECTED_TEXT_COLOR));

        getFragmentManager().beginTransaction().replace(R.id.frame_table, nfcTableFragment).commit();
    }

    private void initFragments(ArrayList<Standing> afcStanding, ArrayList<Standing> nfcStanding){
        bundle.putParcelableArrayList(Constants.AFC_STANDINGS, afcStanding);
        afcTableFragment = new AfcTableFragment();
        afcTableFragment.setArguments(bundle);

        bundle.putParcelableArrayList(Constants.NFC_STANDINGS, nfcStanding);
        nfcTableFragment = new NfcTableFragment();
        nfcTableFragment.setArguments(bundle);
    }
}
