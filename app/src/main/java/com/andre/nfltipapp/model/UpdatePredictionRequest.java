package com.andre.nfltipapp.model;

/**
 * Created by Andre on 06.01.2017.
 */

public class UpdatePredictionRequest {

    String uuid;
    String gameid;
    boolean hasPredicted;
    boolean hasHomeTeamPredicted;

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
