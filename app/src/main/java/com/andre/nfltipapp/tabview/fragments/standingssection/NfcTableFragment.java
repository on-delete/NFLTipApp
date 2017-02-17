package com.andre.nfltipapp.tabview.fragments.standingssection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.standingssection.model.Standing;

import java.util.List;

/**
 * Created by Andre on 22.01.2017.
 */

public class NfcTableFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_nfc,container,false);

        List<Standing> nfcStandings = getArguments().getParcelableArrayList(Constants.NFC_STANDINGS);

        LinearLayout subtableNfcEast = (LinearLayout) view.findViewById(R.id.subtable_nfc_east);
        LinearLayout subtableNfcNorth = (LinearLayout) view.findViewById(R.id.subtable_nfc_north);
        LinearLayout subtableNfcSouth = (LinearLayout) view.findViewById(R.id.subtable_nfc_south);
        LinearLayout subtableNfcWest = (LinearLayout) view.findViewById(R.id.subtable_nfc_west);

        for(int i=0; i< nfcStandings.size(); i++){
            Standing standing = nfcStandings.get(i);

            View rowView = inflater.inflate(R.layout.table_row_standings, container, false);

            LinearLayout tableRow = (LinearLayout) rowView.findViewById(R.id.standings_table_row);
            ImageView teamIcon = (ImageView) rowView.findViewById(R.id.standings_row_team_icon);

            TextView teamCity = (TextView) rowView.findViewById(R.id.standings_row_team_city);
            TextView teamDivGames = (TextView) rowView.findViewById(R.id.standings_row_team_divgames);
            TextView teamPrefix = (TextView) rowView.findViewById(R.id.standings_row_team_prefix);
            TextView teamName = (TextView) rowView.findViewById(R.id.standings_row_team_name);
            TextView teamGames = (TextView) rowView.findViewById(R.id.standings_row_team_games);
            TextView teamScore = (TextView) rowView.findViewById(R.id.standings_row_team_score);

            tableRow.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamColor()));
            teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamIcon());

            teamCity.setText(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamCity());
            teamDivGames.setText(standing.getDivgames());
            teamPrefix.setText(standing.getPrefix() == null ? "" : standing.getPrefix());
            teamName.setText(Constants.TEAM_INFO_MAP.get(standing.getTeamprefix()).getTeamName());
            teamGames.setText(standing.getGames());
            teamScore.setText(standing.getScore());

            if(i<4){
                subtableNfcEast.addView(rowView);
            }
            else if (i<8){
                subtableNfcNorth.addView(rowView);
            }
            else if (i<12){
                subtableNfcSouth.addView(rowView);
            }
            else {
                subtableNfcWest.addView(rowView);
            }
        }

        return view;
    }
}
