package com.andre.nfltipapp.tabview.fragments.statisticssection.model;

import java.util.ArrayList;

/**
 * Created by Andre on 16.02.2017.
 */

public class AllPredictionsPlusResponse {

    private String result;
    private String message;
    private ArrayList<PredictionsPlusStatistic> predictionlist;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<PredictionsPlusStatistic> getPredictionList() {return predictionlist; }
}
