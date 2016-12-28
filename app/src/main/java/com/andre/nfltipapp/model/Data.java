package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Data implements Parcelable{

    private List<Ranking> ranking;
    private List<Prediction> predictions;

    public Data(Parcel parcel){
        ranking = new ArrayList<>();
        parcel.readTypedList(ranking, Ranking.CREATOR);

        predictions = new ArrayList<>();
        parcel.readTypedList(predictions, Prediction.CREATOR);
    }

    public List<Ranking> getRanking() {
        return ranking;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(ranking);
        dest.writeTypedList(predictions);
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
