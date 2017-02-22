package com.andre.nfltipapp.model;

public class TeamInfo {

    private String teamCity;
    private String teamName;
    private int teamIcon;
    private String teamColor;

    public TeamInfo(String teamCity, String teamName, int teamIcon, String teamColor) {
        this.teamCity = teamCity;
        this.teamName = teamName;
        this.teamIcon = teamIcon;
        this.teamColor = teamColor;
    }

    public String getTeamCity() {
        return teamCity;
    }

    public String getTeamName() { return teamName; }

    public int getTeamIcon() {
        return teamIcon;
    }

    public String getTeamColor() {
        return teamColor;
    }
}
