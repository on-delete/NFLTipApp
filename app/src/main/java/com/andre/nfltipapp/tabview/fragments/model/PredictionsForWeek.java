package com.andre.nfltipapp.tabview.fragments.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PredictionsForWeek implements Parcelable{

    private String week;
    private String type;
    private List<GamePrediction> gamePredictions;

    private PredictionsForWeek(Parcel parcel){
        week = parcel.readString();
        type = parcel.readString();
        gamePredictions = new ArrayList<>();
        parcel.readTypedList(gamePredictions, GamePrediction.CREATOR);
    }

    public String getWeek() {
        return week;
    }

    public String getType() {
        return type;
    }

    public List<GamePrediction> getGamePredictions() {
        return gamePredictions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(week);
        dest.writeString(type);
        dest.writeTypedList(gamePredictions);
    }

    public static final Parcelable.Creator<PredictionsForWeek> CREATOR =
            new Parcelable.Creator<PredictionsForWeek>(){

                @Override
                public PredictionsForWeek createFromParcel(Parcel source) {
                    return new PredictionsForWeek(source);
                }

                @Override
                public PredictionsForWeek[] newArray(int size) {
                    return new PredictionsForWeek[size];
                }
            };
}
