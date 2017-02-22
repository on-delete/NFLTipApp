package com.andre.nfltipapp.tabview.fragments.statisticssection.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PredictionsBeforeSeasonStatistic implements Parcelable{

    private String username;
    private String userid;
    private String teamprefix;

    private PredictionsBeforeSeasonStatistic(Parcel parcel){
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

    public static final Parcelable.Creator<PredictionsBeforeSeasonStatistic> CREATOR =
            new Parcelable.Creator<PredictionsBeforeSeasonStatistic>(){

                @Override
                public PredictionsBeforeSeasonStatistic createFromParcel(Parcel source) {
                    return new PredictionsBeforeSeasonStatistic(source);
                }

                @Override
                public PredictionsBeforeSeasonStatistic[] newArray(int size) {
                    return new PredictionsBeforeSeasonStatistic[size];
                }
            };
}
