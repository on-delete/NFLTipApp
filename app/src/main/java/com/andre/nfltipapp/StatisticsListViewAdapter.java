package com.andre.nfltipapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.model.Game;
import com.andre.nfltipapp.model.Prediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatisticsListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle = new ArrayList<>();
   private HashMap<String, List<Game>> expandableListDetail = new HashMap<>();

    public StatisticsListViewAdapter(Context context, List<Prediction> predictionList) {
        this.context = context;

        for(Prediction predictionItem : predictionList){
            List<Game> tempGamesList = new ArrayList<>();

            for (Game game : predictionItem.getGames()){
                if(game.isFinished()==1){
                    tempGamesList.add(game);
                }
            }

            if(tempGamesList.size()>0){
                String title = "Woche " + predictionItem.getWeek() + " - " + (Constants.WEEK_TYPE_MAP.get(predictionItem.getType()) != null ? Constants.WEEK_TYPE_MAP.get(predictionItem.getType()) : "");
                this.expandableListTitle.add(title);
                this.expandableListDetail.put(title, tempGamesList);
            }
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Game expandedListItem = (Game) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.statistics_list_item, null);
        }
        LinearLayout homeBackground = (LinearLayout) convertView
                .findViewById(R.id.background_home_team);
        LinearLayout awayBackground = (LinearLayout) convertView
                .findViewById(R.id.background_away_team);
        TextView homePrefixTextView = (TextView) convertView
                .findViewById(R.id.home_team_prefix_text);
        TextView homeScoreTextView = (TextView) convertView
                .findViewById(R.id.home_team_score_text);
        TextView awayPrefixTextView = (TextView) convertView
                .findViewById(R.id.away_team_prefix_text);
        TextView awayScoreTextView = (TextView) convertView
                .findViewById(R.id.away_team_score_text);

        homePrefixTextView.setText(Constants.TEAM_INFO_MAP.get(expandedListItem.getHometeam()).getTeamName());
        homeScoreTextView.setText(String.valueOf(expandedListItem.getHomepoints()));
        awayPrefixTextView.setText(Constants.TEAM_INFO_MAP.get(expandedListItem.getAwayteam()).getTeamName());
        awayScoreTextView.setText(String.valueOf(expandedListItem.getAwaypoints()));

        homeBackground.setBackgroundResource(R.drawable.back);
        awayBackground.setBackgroundResource(R.drawable.back);

        if(expandedListItem.hasPredicted()==0){
            homeBackground.setBackgroundResource(R.drawable.back_red);
            awayBackground.setBackgroundResource(R.drawable.back_red);
        }
        else {
            if (((expandedListItem.getHomepoints() > expandedListItem.getAwaypoints()) && expandedListItem.predictedHometeam() == 1) || ((expandedListItem.getHomepoints() < expandedListItem.getAwaypoints()) && expandedListItem.predictedHometeam() == 0)) {
                homeBackground.setBackgroundResource(R.drawable.back_green);
                awayBackground.setBackgroundResource(R.drawable.back_green);
            } else if (expandedListItem.getHomepoints() == expandedListItem.getAwaypoints()){
                homeBackground.setBackgroundResource(R.drawable.back_yellow);
                awayBackground.setBackgroundResource(R.drawable.back_yellow);
            }
            else {
                homeBackground.setBackgroundResource(R.drawable.back_red);
                awayBackground.setBackgroundResource(R.drawable.back_red);
            }
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_view_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
