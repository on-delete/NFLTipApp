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

public class AfcTableFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_afc_table, container, false);

        List<Standing> afcStandingsList = getArguments().getParcelableArrayList(Constants.AFC_STANDINGS);
        if(afcStandingsList==null){
            afcStandingsList = new ArrayList<>();
        }

        LinearLayout llSubtableAfcEast = (LinearLayout) rootView.findViewById(R.id.linear_subtable_afc_east);
        LinearLayout llSubtableAfcNorth = (LinearLayout) rootView.findViewById(R.id.linear_subtable_afc_north);
        LinearLayout llSubtableAfcSouth = (LinearLayout) rootView.findViewById(R.id.linear_subtable_afc_south);
        LinearLayout llSubtableAfcWest = (LinearLayout) rootView.findViewById(R.id.linear_subtable_afc_west);

        for(int i=0; i< afcStandingsList.size(); i++){
            Standing standing = afcStandingsList.get(i);

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

            int afcEastDivider = 4;
            int afcNorthDivider = 8;
            int afcSouthDivider = 12;

            if(i< afcEastDivider){
                llSubtableAfcEast.addView(rowRootView);
            }
            else if (i< afcNorthDivider){
                llSubtableAfcNorth.addView(rowRootView);
            }
            else if (i< afcSouthDivider){
                llSubtableAfcSouth.addView(rowRootView);
            }
            else {
                llSubtableAfcWest.addView(rowRootView);
            }
        }

        return rootView;
    }
}
