package com.andre.nfltipapp.tabview.fragments.model;

import java.util.ArrayList;

public class AllPredictionsResponse {

    private String result;
    private String message;
    private ArrayList<GamePredictionStatistic> predictionlist;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<GamePredictionStatistic> getGamePredictionForStatistic() {return predictionlist; }
}
