package com.andre.nfltipapp.tabview.fragments.predictionssection.model;

import static android.R.attr.y;

/**
 * Created by Andre on 20.02.2017.
 */

public class TeamInfoSpinnerObject implements Comparable<TeamInfoSpinnerObject>{

    private String teamName;
    private String teamPrefix;

    public TeamInfoSpinnerObject(String teamName, String teamPrefix) {
        this.teamName = teamName;
        this.teamPrefix = teamPrefix;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamPrefix() {
        return teamPrefix;
    }

    @Override
    public int compareTo(TeamInfoSpinnerObject o) {
        return teamName.compareTo(o.teamName);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof TeamInfoSpinnerObject && getTeamPrefix().equals(((TeamInfoSpinnerObject) o).getTeamPrefix()));
    }
}
