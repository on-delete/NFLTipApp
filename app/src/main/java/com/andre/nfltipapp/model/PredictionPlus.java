package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andre on 04.02.2017.
 */
public class PredictionPlus implements Parcelable{

    private String superbowl;
    private String afcwinnerteam;
    private String nfcwinnerteam;
    private String bestoffenseteam;
    private String bestdefenseteam;
    private String firstgamedate;

    public PredictionPlus (Parcel parcel){
        superbowl = parcel.readString();
        afcwinnerteam = parcel.readString();
        nfcwinnerteam = parcel.readString();
        bestoffenseteam = parcel.readString();
        bestdefenseteam = parcel.readString();
        firstgamedate = parcel.readString();
    }

    public String getSuperbowl() {
        return superbowl;
    }

    public String getAfcwinnerteam() {
        return afcwinnerteam;
    }

    public String getNfcwinnerteam() {
        return nfcwinnerteam;
    }

    public String getBestoffenseteam() {
        return bestoffenseteam;
    }

    public String getBestdefenseteam() {
        return bestdefenseteam;
    }

    public String getFirstgamedate() {
        return firstgamedate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(superbowl);
        dest.writeString(afcwinnerteam);
        dest.writeString(nfcwinnerteam);
        dest.writeString(bestoffenseteam);
        dest.writeString(bestdefenseteam);
        dest.writeString(firstgamedate);
    }

    public static final Parcelable.Creator<PredictionPlus> CREATOR =
            new Parcelable.Creator<PredictionPlus>(){

                @Override
                public PredictionPlus createFromParcel(Parcel source) {
                    return new PredictionPlus(source);
                }

                @Override
                public PredictionPlus[] newArray(int size) {
                    return new PredictionPlus[size];
                }
            };
}