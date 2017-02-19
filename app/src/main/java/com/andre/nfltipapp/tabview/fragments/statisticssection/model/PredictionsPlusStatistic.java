package com.andre.nfltipapp.tabview.fragments.statisticssection.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andre on 16.02.2017.
 */

public class PredictionsPlusStatistic implements Parcelable{

    private String username;
    private String userid;
    private String teamprefix;

    public PredictionsPlusStatistic (Parcel parcel){
        username = parcel.readString();
        userid = parcel.readString();
        teamprefix = parcel.readString();
    }

    public String getUsername() {
        return username;
    }

    public String getUserid() {
        return userid;
    }

    public String getTeamprefix() {
        return teamprefix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(userid);
        dest.writeString(teamprefix);
    }

    public static final Parcelable.Creator<PredictionsPlusStatistic> CREATOR =
            new Parcelable.Creator<PredictionsPlusStatistic>(){

                @Override
                public PredictionsPlusStatistic createFromParcel(Parcel source) {
                    return new PredictionsPlusStatistic(source);
                }

                @Override
                public PredictionsPlusStatistic[] newArray(int size) {
                    return new PredictionsPlusStatistic[size];
                }
            };
}
