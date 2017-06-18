package com.andre.nfltipapp.tabview.fragments.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GamePrediction implements Parcelable{

    private String gameid;
    private String gamedatetime;
    private String hometeam;
    private String awayteam;
    private int homepoints;
    private int awaypoints;
    private int isfinished;
    private int haspredicted;
    private int predictedhometeam;

    private GamePrediction(Parcel parcel){
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

    public String getGamedatetime() {
        String am = " AM";
        String pm = " PM";

        if(this.gamedatetime.contains("09:30")){
            return gamedatetime + am;
        }

        return gamedatetime + pm;
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

    public int isFinished() {
        return isfinished;
    }

    public int hasPredicted() {
        return haspredicted;
    }

    public int predictedHometeam() {
        return predictedhometeam;
    }

    public void setHaspredicted(int haspredicted) {
        this.haspredicted = haspredicted;
    }

    public void setPredictedhometeam(int predictedhometeam) {
        this.predictedhometeam = predictedhometeam;
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

    public static final Parcelable.Creator<GamePrediction> CREATOR =
            new Parcelable.Creator<GamePrediction>(){

                @Override
                public GamePrediction createFromParcel(Parcel source) {
                    return new GamePrediction(source);
                }

                @Override
                public GamePrediction[] newArray(int size) {
                    return new GamePrediction[size];
                }
            };
}
