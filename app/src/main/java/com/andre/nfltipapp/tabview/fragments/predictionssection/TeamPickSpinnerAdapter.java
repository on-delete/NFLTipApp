package com.andre.nfltipapp.tabview.fragments.predictionssection;

import android.content.Context;
import android.graphics.Color;
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

/**
 * Created by Andre on 20.02.2017.
 */

public class TeamPickSpinnerAdapter extends ArrayAdapter<TeamInfoSpinnerObject> {

    private ArrayList<TeamInfoSpinnerObject> objects;

    public TeamPickSpinnerAdapter(Context context, int resource, ArrayList<TeamInfoSpinnerObject> objects) {
        super(context, resource, objects);

        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View subView = layoutInflater.inflate(R.layout.spinner_item, parent, false);

        TextView textView = (TextView) subView.findViewById(R.id.text_name);
        textView.setText(objects.get(position).getTeamName());

        return subView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View subView = layoutInflater.inflate(R.layout.custom_spinner_dropwdown_view, parent, false);

        LinearLayout background = (LinearLayout) subView.findViewById(R.id.team_background);
        TextView teamText = (TextView) subView.findViewById(R.id.team_name);
        ImageView teamIcon = (ImageView) subView.findViewById(R.id.team_icon);

        if(position!=0 && position < objects.size()){
            background.setBackgroundColor(Color.parseColor(Constants.TEAM_INFO_MAP.get(objects.get(position).getTeamPrefix()).getTeamColor()));
            teamIcon.setImageResource(Constants.TEAM_INFO_MAP.get(objects.get(position).getTeamPrefix()).getTeamIcon());
            teamText.setText(objects.get(position).getTeamName());
        }

        return subView;
    }
}
