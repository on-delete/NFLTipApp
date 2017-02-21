package com.andre.nfltipapp.rest;

import com.andre.nfltipapp.loginregistryview.model.LoginRequest;
import com.andre.nfltipapp.loginregistryview.model.LoginResponse;
import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.NameExistResponse;
import com.andre.nfltipapp.loginregistryview.model.RegisterRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterResponse;
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

public interface ApiInterface {

    @POST("nameExisting/")
    Call<NameExistResponse> nameExist(@Body NameExistRequest request);

    @POST("loginUser/")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("registerUser/")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

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
