package com.andre.nfltipapp.tabview.fragments.predictionssection;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.TeamInfoSpinnerObject;

import java.util.ArrayList;

class TeamPickSpinnerAdapter extends ArrayAdapter<TeamInfoSpinnerObject> {

    private ArrayList<TeamInfoSpinnerObject> objects;

    TeamPickSpinnerAdapter(Context context, int resource, ArrayList<TeamInfoSpinnerObject> objects) {
        super(context, resource, objects);

        this.objects = objects;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.spinner_item, parent, false);
        }

        TextView tvTeamName = (TextView) convertView.findViewById(R.id.text_name);
        tvTeamName.setText(objects.get(position).getTeamName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_spinner_dropwdown_view, parent, false);
        }

        LinearLayout llTeamBackground = (LinearLayout) convertView.findViewById(R.id.team_background);
        TextView tvTeamName = (TextView) convertView.findViewById(R.id.team_name);
        ImageView ivTeamIcon = (ImageView) convertView.findViewById(R.id.team_icon);

        if(position!=0 && position < objects.size()){
            llTeamBackground.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(objects.get(position).getTeamPrefix()).getTeamColor()));
            ivTeamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(objects.get(position).getTeamPrefix()).getTeamIcon());
            tvTeamName.setText(objects.get(position).getTeamName());
        }

        return convertView;
    }
}
