package com.andre.nfltipapp;

import com.andre.nfltipapp.model.NameExistRequest;
import com.andre.nfltipapp.model.NameExistResponse;
import com.andre.nfltipapp.model.RegisterLoginRequest;
import com.andre.nfltipapp.model.RegisterLoginResponse;

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
