package com.andre.nfltipapp.tabview.fragments.predictionssection.model;

import android.support.annotation.NonNull;

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
    public int compareTo(@NonNull TeamInfoSpinnerObject o) {
        return teamName.compareTo(o.teamName);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof TeamInfoSpinnerObject && getTeamPrefix().equals(((TeamInfoSpinnerObject) o).getTeamPrefix()));
    }
}
