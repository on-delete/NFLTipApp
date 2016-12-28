package com.andre.nfltipapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Ranking implements Parcelable{

    private String place;
    private String name;
    private String points;

    public Ranking(String place, String name, String points) {
        this.place = place;
        this.name = name;
        this.points = points;
    }

    public Ranking(Parcel parcel){
        this.place = parcel.readString();
        this.name = parcel.readString();
        this.points = parcel.readString();
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(place);
        dest.writeString(name);
        dest.writeString(points);
    }

    public static final Parcelable.Creator<Ranking> CREATOR =
            new Parcelable.Creator<Ranking>(){

                @Override
                public Ranking createFromParcel(Parcel source) {
                    return new Ranking(source);
                }

                @Override
                public Ranking[] newArray(int size) {
                    return new Ranking[size];
                }
            };
}
