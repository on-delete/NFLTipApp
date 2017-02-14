package com.andre.nfltipapp.model;

/**
 * Created by Andre on 14.02.2017.
 */

public class UpdatePredictionPlusRequest {

    String uuid;
    String state;
    String teamprefix;

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
