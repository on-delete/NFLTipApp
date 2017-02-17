package com.andre.nfltipapp.rest;

import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.NameExistResponse;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginResponse;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.AllPredictionsPlusRequest;
import com.andre.nfltipapp.tabview.fragments.statisticssection.model.AllPredictionsPlusResponse;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsRequest;
import com.andre.nfltipapp.tabview.fragments.model.AllPredictionsResponse;
import com.andre.nfltipapp.model.DataRequest;
import com.andre.nfltipapp.model.DataResponse;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdatePredictionPlusRequest;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdatePredictionRequest;
import com.andre.nfltipapp.tabview.fragments.predictionssection.model.UpdateResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("nameExisting/")
    Call<NameExistResponse> nameExist(@Body NameExistRequest request);

    @POST("registerLogin/")
    Call<RegisterLoginResponse> loginUser(@Body RegisterLoginRequest request);

    @POST("registerUser/")
    Call<RegisterLoginResponse> registerUser(@Body RegisterLoginRequest request);

    @POST("getData/")
    Call<DataResponse> getData(@Body DataRequest request);

    @POST("updatePrediction")
    Call<UpdateResponse> updatePrediction(@Body UpdatePredictionRequest request);

    @POST("updatePredictionPlus")
    Call<UpdateResponse> updatePredictionPlus(@Body UpdatePredictionPlusRequest request);

    @POST("getAllPredictionsPlusForState")
    Call<AllPredictionsPlusResponse> allPredictionsPlus(@Body AllPredictionsPlusRequest request);

    @POST("getAllPredictionsForGame")
    Call<AllPredictionsResponse> allPredictions(@Body AllPredictionsRequest request);
}
