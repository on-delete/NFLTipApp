package com.andre.nfltipapp.model;

import com.andre.nfltipapp.Constants;

public class TeamInfo {

    private String teamCity;
    private String teamName;
    private Constants.DIVISION division;
    private int teamIcon;
    private String teamColor;

    public TeamInfo(String teamCity, String teamName, Constants.DIVISION division, int teamIcon, String teamColor) {
        this.teamCity = teamCity;
        this.teamName = teamName;
        this.division = division;
        this.teamIcon = teamIcon;
        this.teamColor = teamColor;
    }

    public String getTeamCity() {
        return teamCity;
    }

    public String getTeamName() { return teamName; }

    public Constants.DIVISION getDivision() {
        return division;
    }

    public int getTeamIcon() {
        return teamIcon;
    }

    public String getTeamColor() {
        return teamColor;
    }
}
