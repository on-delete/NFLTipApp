package com.andre.nfltipapp.tabview.fragments.standingssection.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andre on 22.01.2017.
 */
public class Standing implements Parcelable {

    private String teamprefix;
    private String clinching;
    private String games;
    private String score;
    private String divgames;

    public Standing(Parcel parcel){
        teamprefix = parcel.readString();
        clinching = parcel.readString();
        games = parcel.readString();
        score = parcel.readString();
        divgames = parcel.readString();
    }

    public String getTeamprefix() {
        return teamprefix;
    }

    public String getClinching() {
        return clinching;
    }

    public String getGames() {
        return games;
    }

    public String getScore() {
        return score;
    }

    public String getDivgames() {
        return divgames;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(teamprefix);
        dest.writeString(clinching);
        dest.writeString(games);
        dest.writeString(score);
        dest.writeString(divgames);
    }

    public static final Parcelable.Creator<Standing> CREATOR =
            new Parcelable.Creator<Standing>(){

                @Override
                public Standing createFromParcel(Parcel source) {
                    return new Standing(source);
                }

                @Override
                public Standing[] newArray(int size) {
                    return new Standing[size];
                }
            };

}
