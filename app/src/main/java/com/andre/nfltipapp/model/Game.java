package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable{

    private String hometeam;
    private String awayteam;
    private int homepoints;
    private int awaypoints;
    private boolean isfinished;
    private boolean haspredicted;
    private boolean predictedhometeam;

    public Game(Parcel parcel){
        hometeam = parcel.readString();
        awayteam = parcel.readString();
        homepoints = parcel.readInt();
        awaypoints = parcel.readInt();
        isfinished = (Boolean) parcel.readValue( null );
        haspredicted = (Boolean) parcel.readValue( null );
        predictedhometeam = (Boolean) parcel.readValue( null );
    }

    public String getHometeam() {
        return hometeam;
    }

    public String getAwayteam() {
        return awayteam;
    }

    public int getHomepoints() {
        return homepoints;
    }

    public int getAwaypoints() {
        return awaypoints;
    }

    public boolean isFinished() {
        return isfinished;
    }

    public boolean hasPredicted() {
        return haspredicted;
    }

    public boolean predictedHometeam() {
        return predictedhometeam;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hometeam);
        dest.writeString(awayteam);
        dest.writeInt(homepoints);
        dest.writeInt(awaypoints);
        dest.writeValue(isfinished);
        dest.writeValue(haspredicted);
        dest.writeValue(predictedhometeam);
    }

    public static final Parcelable.Creator<Game> CREATOR =
            new Parcelable.Creator<Game>(){

                @Override
                public Game createFromParcel(Parcel source) {
                    return new Game(source);
                }

                @Override
                public Game[] newArray(int size) {
                    return new Game[size];
                }
            };
}
