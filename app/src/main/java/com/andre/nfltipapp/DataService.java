package com.andre.nfltipapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.model.DataRequest;
import com.andre.nfltipapp.model.DataResponse;
import com.andre.nfltipapp.rest.Api;
import com.andre.nfltipapp.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

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

    private DataService(Parcel parcel){
        this.data = parcel.readParcelable(Data.class.getClassLoader());

        this.userId = parcel.readString();
    }

    public void addDataUpdateListener(DataUpdatedListener listener) {
        this.dataUpdatedListenerList.add(listener);
    }

    public void removeDataUpdateListener(DataUpdatedListener listener) {
        this.dataUpdatedListenerList.remove(listener);
    }

    public void dataUpdate(Context context) {
        getDataFromServer(context);
    }

    private void getDataFromServer(Context context){
        DataRequest request = new DataRequest();
        request.setUserId(getUserId());
        ApiInterface apiInterface = Api.getInstance(context).getApiInterface();
        Call<DataResponse> response = apiInterface.getData(request);

        response.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, retrofit2.Response<DataResponse> response) {
                DataResponse resp = response.body();
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    notifyFailure("Server error!");
                }
                else {
                    Log.d(Constants.TAG, resp.getData().toString());
                    notifyDataUpdate(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.d(Constants.TAG, t.getMessage());
                notifyFailure("Server nicht erreichbar!");
            }
        });
    }

    private void notifyDataUpdate(Data data){
        this.data = data;

        for (DataUpdatedListener dataUpdatedListener : this.dataUpdatedListenerList) {
            dataUpdatedListener.onDataUpdated(data);
        }
    }

    private void notifyFailure(String error){
        for (DataUpdatedListener dataUpdatedListener : this.dataUpdatedListenerList) {
            dataUpdatedListener.onFailure(error);
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
