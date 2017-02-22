package com.andre.nfltipapp.tabview.fragments.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andre on 04.02.2017.
 */
public class PredictionBeforeSeason implements Parcelable{

    private String user;
    private String superbowl;
    private String afcwinnerteam;
    private String nfcwinnerteam;
    private String bestoffenseteam;
    private String bestdefenseteam;
    private String firstgamedate;

    public PredictionBeforeSeason(Parcel parcel){
        user = parcel.readString();
        superbowl = parcel.readString();
        afcwinnerteam = parcel.readString();
        nfcwinnerteam = parcel.readString();
        bestoffenseteam = parcel.readString();
        bestdefenseteam = parcel.readString();
        firstgamedate = parcel.readString();
    }

    public String getUser() {
        return user;
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
        return firstgamedate + " PM";
    }

    public void setSuperbowl(String superbowl) {
        this.superbowl = superbowl;
    }

    public void setAfcwinnerteam(String afcwinnerteam) {
        this.afcwinnerteam = afcwinnerteam;
    }

    public void setBestoffenseteam(String bestoffenseteam) {
        this.bestoffenseteam = bestoffenseteam;
    }

    public void setNfcwinnerteam(String nfcwinnerteam) {
        this.nfcwinnerteam = nfcwinnerteam;
    }

    public void setBestdefenseteam(String bestdefenseteam) {
        this.bestdefenseteam = bestdefenseteam;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(superbowl);
        dest.writeString(afcwinnerteam);
        dest.writeString(nfcwinnerteam);
        dest.writeString(bestoffenseteam);
        dest.writeString(bestdefenseteam);
        dest.writeString(firstgamedate);
    }

    public static final Parcelable.Creator<PredictionBeforeSeason> CREATOR =
            new Parcelable.Creator<PredictionBeforeSeason>(){

                @Override
                public PredictionBeforeSeason createFromParcel(Parcel source) {
                    return new PredictionBeforeSeason(source);
                }

                @Override
                public PredictionBeforeSeason[] newArray(int size) {
                    return new PredictionBeforeSeason[size];
                }
            };
}
