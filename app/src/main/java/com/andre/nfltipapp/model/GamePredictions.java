package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andre on 16.01.2017.
 */
public class GamePredictions implements Parcelable{

    private int predicted;
    private int hometeampredicted;
    private String userid;
    private String username;

    public GamePredictions(Parcel parcel){
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

    public static final Parcelable.Creator<GamePredictions> CREATOR =
            new Parcelable.Creator<GamePredictions>(){

                @Override
                public GamePredictions createFromParcel(Parcel source) {
                    return new GamePredictions(source);
                }

                @Override
                public GamePredictions[] newArray(int size) {
                    return new GamePredictions[size];
                }
            };
}
