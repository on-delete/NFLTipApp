package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable{

    private String gameid;
    private String gamedatetime;
    private String hometeam;
    private String awayteam;
    private int homepoints;
    private int awaypoints;
    private int isfinished;
    private int haspredicted;
    private int predictedhometeam;

    public Game(Parcel parcel){
        gameid = parcel.readString();
        gamedatetime = parcel.readString();
        hometeam = parcel.readString();
        awayteam = parcel.readString();
        homepoints = parcel.readInt();
        awaypoints = parcel.readInt();
        isfinished = parcel.readInt();
        haspredicted = parcel.readInt();
        predictedhometeam = parcel.readInt();
    }

    public String getGameid() {
        return gameid;
    }

    public String getGamedatetime() { return gamedatetime + " PM"; }

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

    public int isFinished() {
        return isfinished;
    }

    public int hasPredicted() {
        return haspredicted;
    }

    public int predictedHometeam() {
        return predictedhometeam;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameid);
        dest.writeString(gamedatetime);
        dest.writeString(hometeam);
        dest.writeString(awayteam);
        dest.writeInt(homepoints);
        dest.writeInt(awaypoints);
        dest.writeInt(isfinished);
        dest.writeInt(haspredicted);
        dest.writeInt(predictedhometeam);
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
