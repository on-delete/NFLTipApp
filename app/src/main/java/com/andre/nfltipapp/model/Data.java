package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.andre.nfltipapp.tabview.fragments.model.Prediction;
import com.andre.nfltipapp.tabview.fragments.model.PredictionPlus;
import com.andre.nfltipapp.tabview.fragments.model.Ranking;
import com.andre.nfltipapp.tabview.fragments.standingssection.model.Standing;

import java.util.ArrayList;
import java.util.List;

public class Data implements Parcelable{

    private List<Ranking> ranking;
    private List<Prediction> predictions;
    private List<PredictionPlus> predictionsplus;
    private ArrayList<Standing> standings;

    public Data(Parcel parcel){
        ranking = new ArrayList<>();
        parcel.readTypedList(ranking, Ranking.CREATOR);

        predictions = new ArrayList<>();
        parcel.readTypedList(predictions, Prediction.CREATOR);

        predictionsplus = new ArrayList<>();
        parcel.readTypedList(predictionsplus, PredictionPlus.CREATOR);

        standings = new ArrayList<>();
        parcel.readTypedList(standings, Standing.CREATOR);
    }

    public List<Ranking> getRanking() {
        return ranking;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public List<PredictionPlus> getPredictionsplus() {
        return predictionsplus;
    }

    public ArrayList<Standing> getStandings() {
        return standings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(ranking);
        dest.writeTypedList(predictions);
        dest.writeTypedList(predictionsplus);
        dest.writeTypedList(standings);
    }

    public static final Parcelable.Creator<Data> CREATOR =
            new Parcelable.Creator<Data>(){

                @Override
                public Data createFromParcel(Parcel source) {
                    return new Data(source);
                }

                @Override
                public Data[] newArray(int size) {
                    return new Data[size];
                }
            };
}
