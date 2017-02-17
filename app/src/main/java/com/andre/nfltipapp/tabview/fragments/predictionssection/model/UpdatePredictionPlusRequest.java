package com.andre.nfltipapp.tabview.fragments.predictionssection.model;

/**
 * Created by Andre on 14.02.2017.
 */

public class UpdatePredictionPlusRequest {

    private String uuid;
    private String state;
    private String teamprefix;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTeamprefix(String teamprefix) {
        this.teamprefix = teamprefix;
    }
}
