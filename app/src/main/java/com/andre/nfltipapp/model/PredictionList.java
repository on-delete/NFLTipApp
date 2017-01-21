package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andre on 16.01.2017.
 */

public class PredictionList implements Parcelable {

    private List<GamePredictions> gamePredictions;

    public PredictionList(Parcel parcel){
        gamePredictions = new ArrayList<>();
        parcel.readTypedList(gamePredictions, GamePredictions.CREATOR);
    }

    public List<GamePredictions> getGamePredictions() {
        return gamePredictions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(gamePredictions);
    }

    public static final Parcelable.Creator<PredictionList> CREATOR =
            new Parcelable.Creator<PredictionList>(){

                @Override
                public PredictionList createFromParcel(Parcel source) {
                    return new PredictionList(source);
                }

                @Override
                public PredictionList[] newArray(int size) {
                    return new PredictionList[size];
                }
            };
}
