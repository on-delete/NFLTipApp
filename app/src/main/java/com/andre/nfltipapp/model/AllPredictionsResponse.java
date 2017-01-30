package com.andre.nfltipapp.model;

import java.util.ArrayList;

/**
 * Created by Andre on 16.01.2017.
 */

public class AllPredictionsResponse {

    private String result;
    private String message;
    private ArrayList<GamePredictions> predictionlist;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<GamePredictions> getPredictionList() {return predictionlist; }
}