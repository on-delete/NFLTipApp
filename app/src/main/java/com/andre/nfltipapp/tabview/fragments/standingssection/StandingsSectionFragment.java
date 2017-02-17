package com.andre.nfltipapp.tabview.fragments.standingssection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.standingssection.model.Standing;

import java.util.ArrayList;

/**
 * Created by Andre on 30.01.2017.
 */

public class StandingsSectionFragment extends Fragment {

    private Button afcButton;
    private Button nfcButton;

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
