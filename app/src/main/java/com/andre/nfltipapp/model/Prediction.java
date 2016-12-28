package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Prediction implements Parcelable{

    private String week;
    private String type;
    private List<Game> games;

    public Prediction(Parcel parcel){
        week = parcel.readString();
        type = parcel.readString();
        games = new ArrayList<>();
        parcel.readTypedList(games, Game.CREATOR);
    }

    public String getWeek() {
        return week;
    }

    public String getType() {
        return type;
    }

    public List<Game> getGames() {
        return games;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(week);
        dest.writeString(type);
        dest.writeTypedList(games);
    }

    public static final Parcelable.Creator<Prediction> CREATOR =
            new Parcelable.Creator<Prediction>(){

                @Override
                public Prediction createFromParcel(Parcel source) {
                    return new Prediction(source);
                }

                @Override
                public Prediction[] newArray(int size) {
                    return new Prediction[size];
                }
            };
}
