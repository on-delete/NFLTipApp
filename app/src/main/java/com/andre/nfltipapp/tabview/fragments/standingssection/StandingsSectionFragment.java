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

    private Button btAfc;
    private Button btNfc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int bottomAfcRange = 0;
        final int upperAfcRange = 16;
        final int upperNfcRange = 32;

        View rootView = inflater.inflate(R.layout.fragment_section_standings, container, false);

        Data data = getActivity().getIntent().getParcelableExtra(Constants.DATA);
        final ArrayList<Standing> standingsList = data.getStandings();

        btAfc = (Button) rootView.findViewById(R.id.afc_button);
        btAfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTableToAfc(new ArrayList<>(standingsList.subList(bottomAfcRange, upperAfcRange)));
            }
        });
        btNfc = (Button) rootView.findViewById(R.id.nfc_button);
        btNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTableToNfc(new ArrayList<>(standingsList.subList(upperAfcRange, upperNfcRange)));
            }
        });

        changeTableToAfc(new ArrayList<>(standingsList.subList(0,16)));

        return rootView;
    }

    private void changeTableToAfc(ArrayList<Standing> afcStanding){
        btAfc.setBackgroundColor(Color.parseColor(Constants.AFC_BACKGROUND_COLOR));
        btAfc.setTextColor(Color.parseColor(Constants.TABLE_SELECTED_TEXT_COLOR));
        btNfc.setBackgroundColor(Color.parseColor(Constants.DEFAULT_BACKGROUND_COLOR));
        btNfc.setTextColor(Color.parseColor(Constants.TABLE_DEFAULT_TEXT_COLOR));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.AFC_STANDINGS, afcStanding);
        AfcTableFragment afcTableFragment = new AfcTableFragment();
        afcTableFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.table_fragment, afcTableFragment).commit();
    }

    private void changeTableToNfc(ArrayList<Standing> nfcStanding){
        btAfc.setBackgroundColor(Color.parseColor(Constants.DEFAULT_BACKGROUND_COLOR));
        btAfc.setTextColor(Color.parseColor(Constants.TABLE_DEFAULT_TEXT_COLOR));
        btNfc.setBackgroundColor(Color.parseColor(Constants.NFC_BACKGROUND_COLOR));
        btNfc.setTextColor(Color.parseColor(Constants.TABLE_SELECTED_TEXT_COLOR));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.NFC_STANDINGS, nfcStanding);
        NfcTableFragment nfcTableFragment = new NfcTableFragment();
        nfcTableFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.table_fragment, nfcTableFragment).commit();
    }
}
