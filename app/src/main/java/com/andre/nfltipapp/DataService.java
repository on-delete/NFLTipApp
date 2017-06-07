package com.andre.nfltipapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.andre.nfltipapp.model.Data;

import java.util.ArrayList;
import java.util.List;

public class DataService implements Parcelable{

    private Data data;
    private String userId;

    private List<DataUpdatedListener> dataUpdatedListenerList = new ArrayList<>();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    DataService() {
    }

    public DataService(Parcel parcel){
        this.data = parcel.readParcelable(Data.class.getClassLoader());

        this.userId = parcel.readString();

        dataUpdatedListenerList = new ArrayList<>();
        parcel.readArrayList(DataUpdatedListener.class.getClassLoader());
    }

    public void addDataUpdateListener(DataUpdatedListener listener) {
        this.dataUpdatedListenerList.add(listener);
    }

    public void removeDataUpdateListener(DataUpdatedListener listener) {
        this.dataUpdatedListenerList.remove(listener);
    }

    public void dataUpdated(Data newData) {
        for (DataUpdatedListener dataUpdatedListener : this.dataUpdatedListenerList) {
            dataUpdatedListener.onDataUpdated(newData);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.userId);
        dest.writeList(dataUpdatedListenerList);
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public DataService createFromParcel(Parcel in) {
                    return new DataService(in);
                }

                public DataService[] newArray(int size) {
                    return new DataService[size];
                }
            };
}
