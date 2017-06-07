package com.andre.nfltipapp.tabview.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.DataService;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.fragments.model.Ranking;

import java.util.List;

public class RankingSectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_ranking, container, false);

        Bundle bundle = this.getArguments();
        DataService dataService = null;
        if (bundle != null) {
            dataService = bundle.getParcelable("dataService");
        }

        LinearLayout llTable = (LinearLayout) rootView.findViewById(R.id.linear_ranking_table);

        List<Ranking> rankingList = dataService.getData().getRanking();

        for(Ranking rankingEntry : rankingList){
            View rowView = inflater.inflate(R.layout.ranking_table_row, container, false);

            TextView tvName = (TextView) rowView.findViewById(R.id.text_player_name);
            tvName.setText(rankingEntry.getName());
            if(dataService.getUserId() != null && dataService.getUserId().equals(rankingEntry.getUserid())){
                tvName.setTypeface(null, Typeface.BOLD);
            }
            TextView tvPoints = (TextView) rowView.findViewById(R.id.text_player_points) ;
            tvPoints.setText(rankingEntry.getPoints());

            llTable.addView(rowView);
        }

        return rootView;
    }
}
