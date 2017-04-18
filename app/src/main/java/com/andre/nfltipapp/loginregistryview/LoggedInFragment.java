package com.andre.nfltipapp.loginregistryview;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import static com.andre.nfltipapp.Constants.SHARED_PREF_FILENAME;

public class LoggedInFragment extends Fragment{

    private AppCompatButton btnLogin;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;
    private SharedPreferences pref;
    private String username;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logged_in,container,false);

        apiInterface = Api.getInstance(getActivity()).getApiInterface();

        this.pref = getActivity().getApplicationContext().getSharedPreferences(SHARED_PREF_FILENAME, 0);
        this.username = pref.getString("username", null);
        this.password = pref.getString("password", null);

        initView(view);
        return view;
    }

    private void initView(View view){
        btnLogin = (AppCompatButton) view.findViewById(R.id.button_login);

        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

        loginProcess();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setVisibility(View.INVISIBLE);
                loginProcess();
            }
        });
    }

    private void loginProcess(){
        progressBar.setVisibility(View.VISIBLE);
        LoginRequest loginRequest = new LoginRequest();
        if(username != null) {
            loginRequest.setName(username);
        }
        if(password != null) {
            loginRequest.setPassword(password);
        }
        Call<LoginResponse> response = apiInterface.loginUser(loginRequest);

        response.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                LoginResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Server error!", Snackbar.LENGTH_LONG).show();
                    btnLogin.setVisibility(View.VISIBLE);
                }
                else {
                    if (resp.getResult().equals(Constants.SUCCESS)) {
                        switch (resp.getMessage()) {
                            case "login_successfull":
                                getDataProcess(resp.getUserId());
                                break;
                            case "password_wrong":
                                pref.edit().putString("password", null).apply();
                                goToLogin();
                                break;
                            default:
                                pref.edit().putString("username", null).apply();
                                goToLogin();
                                break;
                        }
                    }
                }
                setClickableStatus(true);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG, t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void getDataProcess(final String userId){
        progressBar.setVisibility(View.VISIBLE);
        DataRequest request = new DataRequest();
        request.setUserId(userId);
        Call<DataResponse> response = apiInterface.getData(request);

        response.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, retrofit2.Response<DataResponse> response) {
                DataResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Server error!", Snackbar.LENGTH_LONG).show();
                    btnLogin.setVisibility(View.VISIBLE);
                }
                else {
                    goToMainActivity(userId, resp.getData());
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                btnLogin.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG, t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin(){
        Fragment register = new LoginFragment();
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

    private void setClickableStatus(boolean status){
        btnLogin.setClickable(status);
    }
}
