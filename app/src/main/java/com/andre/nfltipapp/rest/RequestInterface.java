package com.andre.nfltipapp.rest;

import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.NameExistResponse;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Andre on 15.12.2016.
 */

public interface RequestInterface {

    @POST("nameExisting/")
    Call<NameExistResponse> nameExist(@Body NameExistRequest request);

    @POST("registerLogin/")
    Call<RegisterLoginResponse> loginUser(@Body RegisterLoginRequest request);

    @POST("registerUser/")
    Call<RegisterLoginResponse> registerUser(@Body RegisterLoginRequest request);
}
