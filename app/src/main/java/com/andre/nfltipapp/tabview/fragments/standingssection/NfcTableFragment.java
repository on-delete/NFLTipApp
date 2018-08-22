package com.andre.nfltipapp.tabview.fragments.standingssection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.drawable.StandingsTeamBackground;
import com.andre.nfltipapp.tabview.fragments.standingssection.model.Standing;

import java.util.ArrayList;
import java.util.List;

public class NfcTableFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nfc_table,container,false);

        List<Standing> nfcStandings = getArguments().getParcelableArrayList(Constants.NFC_STANDINGS);
        if(nfcStandings==null){
            nfcStandings = new ArrayList<>();
        }

        LinearLayout llSubtableNfcEast = (LinearLayout) rootView.findViewById(R.id.linear_subtable_nfc_east);
        LinearLayout llSubtableNfcNorth = (LinearLayout) rootView.findViewById(R.id.linear_subtable_nfc_north);
        LinearLayout llSubtableNfcSouth = (LinearLayout) rootView.findViewById(R.id.linear_subtable_nfc_south);
        LinearLayout llSubtableNfcWest = (LinearLayout) rootView.findViewById(R.id.linear_subtable_nfc_west);

        for(int i=0; i< nfcStandings.size(); i++){
            Standing standing = nfcStandings.get(i);

            View rowRootView = inflater.inflate(R.layout.standings_table_row, container, false);

            LinearLayout llTableRow = (LinearLayout) rowRootView.findViewById(R.id.linear_standings_table_row);

            TextView tvTeamCityName = (TextView) rowRootView.findViewById(R.id.text_team_city);
            TextView tvTeamDivGames = (TextView) rowRootView.findViewById(R.id.text_team_divgames);
            TextView tvClinching = (TextView) rowRootView.findViewById(R.id.text_team_prefix);
            TextView tvTeamName = (TextView) rowRootView.findViewById(R.id.text_team_name);
            TextView tvTeamGames = (TextView) rowRootView.findViewById(R.id.text_team_games);
            TextView tvTeamScore = (TextView) rowRootView.findViewById(R.id.text_team_score);

//            llTableRow.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamColor()));
            llTableRow.setBackground(new StandingsTeamBackground(Color.parseColor(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamColor())));

            tvTeamCityName.setText(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamCity());
            tvTeamDivGames.setText(standing.getDivgames());
            tvClinching.setText(standing.getClinching() == null ? "" : standing.getClinching());
            tvTeamName.setText(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamName());
            tvTeamGames.setText(standing.getGames());
            tvTeamScore.setText(standing.getScore());

            int nfcEastDivider = 4;
            int nfcNorthDivider = 8;
            int nfcSouthDivider = 12;

            if(i<nfcEastDivider){
                llSubtableNfcEast.addView(rowRootView);
            }
            else if (i<nfcNorthDivider){
                llSubtableNfcNorth.addView(rowRootView);
            }
            else if (i<nfcSouthDivider){
                llSubtableNfcSouth.addView(rowRootView);
            }
            else {
                llSubtableNfcWest.addView(rowRootView);
            }
        }

        return rootView;
    }
}
