package com.andre.nfltipapp.loginregistryview;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andre.nfltipapp.Constants;
import com.andre.nfltipapp.MainActivity;
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.loginregistryview.model.LoginRequest;
import com.andre.nfltipapp.loginregistryview.model.LoginResponse;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.model.DataRequest;
import com.andre.nfltipapp.model.DataResponse;
import com.andre.nfltipapp.rest.Api;
import com.andre.nfltipapp.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginFragment extends Fragment {

    private TextInputEditText etName, etPassword;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        apiInterface = Api.getInstance(getActivity()).getApiInterface();

        initView(view);
        return view;
    }

    private void initView(View view){
        AppCompatButton btnLogin = (AppCompatButton) view.findViewById(R.id.btn_login);
        TextView tvRegisterLink = (TextView) view.findViewById(R.id.tv_register);
        etName = (TextInputEditText)view.findViewById(R.id.et_name);
        etPassword = (TextInputEditText)view.findViewById(R.id.et_password);
        etName.setText("test2");
        etPassword.setText("hallo1");

        progressBar = (ProgressBar)view.findViewById(R.id.progress);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    loginProcess();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
    }

    private void loginProcess(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName(etName.getText().toString());
        loginRequest.setPassword(etPassword.getText().toString());
        Call<LoginResponse> response = apiInterface.loginUser(loginRequest);

        response.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                LoginResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    if(resp.getMessage().equals(Constants.LOGIN_SUCCESSFULL)){
                        getDataProcess(resp.getUserId());
                    } else {
                        Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Login Failed!", Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getDataProcess(final String userId){
        DataRequest request = new DataRequest();
        request.setUserId(userId);
        Call<DataResponse> response = apiInterface.getData(request);

        response.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, retrofit2.Response<DataResponse> response) {
                DataResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(resp.getResult().equals(Constants.SUCCESS)){
                    if(resp.getMessage().equals(Constants.GET_DATA_SUCCESSFULL)){
                        goToMainActivity(userId, resp.getData());
                    } else {
                        Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Login Failed!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToRegister(){
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, register);
        ft.commit();
    }

    private void goToMainActivity(String userId, Data data){
        Log.d(Constants.TAG, "Login successfull!");
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        intent.putExtra(Constants.USERID, userId);
        intent.putExtra(Constants.DATA, data);
        this.getActivity().finish();
        startActivity(intent);
    }
}
