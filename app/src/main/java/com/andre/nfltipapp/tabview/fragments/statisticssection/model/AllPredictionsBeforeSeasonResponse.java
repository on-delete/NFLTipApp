package com.andre.nfltipapp.tabview.fragments.statisticssection.model;

import java.util.ArrayList;

public class AllPredictionsBeforeSeasonResponse {

    private String result;
    private String message;
    private ArrayList<PredictionsBeforeSeasonStatistic> predictionlist;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<PredictionsBeforeSeasonStatistic> getPredictionList() {return predictionlist; }
}
