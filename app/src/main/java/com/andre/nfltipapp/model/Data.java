package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.andre.nfltipapp.tabview.fragments.model.PredictionsForWeek;
import com.andre.nfltipapp.tabview.fragments.model.PredictionBeforeSeason;
import com.andre.nfltipapp.tabview.fragments.model.Ranking;
import com.andre.nfltipapp.tabview.fragments.standingssection.model.Standing;

import java.util.ArrayList;
import java.util.List;

public class Data implements Parcelable{

    private List<Ranking> ranking;
    private List<PredictionsForWeek> predictionsForWeeks;
    private List<PredictionBeforeSeason> predictionBeforeSeason;
    private ArrayList<Standing> standings;

    public Data(Parcel parcel){
        ranking = new ArrayList<>();
        parcel.readTypedList(ranking, Ranking.CREATOR);

        predictionsForWeeks = new ArrayList<>();
        parcel.readTypedList(predictionsForWeeks, PredictionsForWeek.CREATOR);

        predictionBeforeSeason = new ArrayList<>();
        parcel.readTypedList(predictionBeforeSeason, PredictionBeforeSeason.CREATOR);

        standings = new ArrayList<>();
        parcel.readTypedList(standings, Standing.CREATOR);
    }

    public List<Ranking> getRanking() {
        return ranking;
    }

    public List<PredictionsForWeek> getPredictionsForWeeks() {
        return predictionsForWeeks;
    }

    public List<PredictionBeforeSeason> getPredictionBeforeSeason() {
        return predictionBeforeSeason;
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
        dest.writeTypedList(predictionsForWeeks);
        dest.writeTypedList(predictionBeforeSeason);
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
