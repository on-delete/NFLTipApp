package com.andre.nfltipapp.tabview.fragments.predictionssection.model;

/**
 * Created by Andre on 06.01.2017.
 */

public class UpdatePredictionRequest {

    private String uuid;
    private String gameid;
    private boolean hasPredicted;
    private boolean hasHomeTeamPredicted;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public void setHasPredicted(boolean hasPredicted) {
        this.hasPredicted = hasPredicted;
    }

    public void setHasHomeTeamPredicted(boolean hasHomeTeamPredicted) {
        this.hasHomeTeamPredicted = hasHomeTeamPredicted;
    }
}
