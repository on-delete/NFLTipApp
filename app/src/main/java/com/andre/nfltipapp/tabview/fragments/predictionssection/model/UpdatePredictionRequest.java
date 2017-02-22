package com.andre.nfltipapp.tabview.fragments.predictionssection.model;

public class UpdatePredictionRequest {

    private String userId;
    private String gameId;
    private boolean hasPredicted;
    private boolean hasHomeTeamPredicted;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setHasPredicted(boolean hasPredicted) {
        this.hasPredicted = hasPredicted;
    }

    public void setHasHomeTeamPredicted(boolean hasHomeTeamPredicted) {
        this.hasHomeTeamPredicted = hasHomeTeamPredicted;
    }
}
