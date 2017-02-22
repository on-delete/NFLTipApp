package com.andre.nfltipapp.tabview.fragments.predictionssection.model;

/**
 * Created by Andre on 14.02.2017.
 */

public class UpdatePredictionBeforeSeasonRequest {

    private String userId;
    private String predictionType;
    private String teamprefix;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPredictionType(String predictionType) {
        this.predictionType = predictionType;
    }

    public void setTeamprefix(String teamprefix) {
        this.teamprefix = teamprefix;
    }
}
