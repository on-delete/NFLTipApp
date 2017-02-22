package com.andre.nfltipapp.tabview.fragments.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andre on 16.01.2017.
 */
public class GamePredictionStatistic implements Parcelable{

    private int predicted;
    private int hometeampredicted;
    private String userid;
    private String username;

    public GamePredictionStatistic(Parcel parcel){
        userid = parcel.readString();
        username = parcel.readString();
        predicted = parcel.readInt();
        hometeampredicted = parcel.readInt();
    }

    public int getPredicted() {
        return predicted;
    }

    public int getHometeampredicted() {
        return hometeampredicted;
    }

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(username);
        dest.writeInt(predicted);
        dest.writeInt(hometeampredicted);
    }

    public static final Parcelable.Creator<GamePredictionStatistic> CREATOR =
            new Parcelable.Creator<GamePredictionStatistic>(){

                @Override
                public GamePredictionStatistic createFromParcel(Parcel source) {
                    return new GamePredictionStatistic(source);
                }

                @Override
                public GamePredictionStatistic[] newArray(int size) {
                    return new GamePredictionStatistic[size];
                }
            };
}
