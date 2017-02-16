package com.andre.nfltipapp.rest;

import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.NameExistResponse;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginResponse;
import com.andre.nfltipapp.model.AllPredictionsPlusRequest;
import com.andre.nfltipapp.model.AllPredictionsPlusResponse;
import com.andre.nfltipapp.model.AllPredictionsRequest;
import com.andre.nfltipapp.model.AllPredictionsResponse;
import com.andre.nfltipapp.model.DataRequest;
import com.andre.nfltipapp.model.DataResponse;
import com.andre.nfltipapp.model.UpdatePredictionPlusRequest;
import com.andre.nfltipapp.model.UpdatePredictionRequest;
import com.andre.nfltipapp.model.UpdateResponse;

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
