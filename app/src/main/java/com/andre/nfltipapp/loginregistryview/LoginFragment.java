package com.andre.nfltipapp.loginregistryview;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.andre.nfltipapp.Constants.SHARED_PREF_FILENAME;

public class LoginFragment extends Fragment {

    private TextInputEditText etName, etPassword;
    private AppCompatButton btnLogin;
    private TextView tvRegisterLink;
    private TextView tvPasswordLostLink;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        apiInterface = Api.getInstance(getActivity()).getApiInterface();

        this.pref = getActivity().getApplicationContext().getSharedPreferences(SHARED_PREF_FILENAME, 0);

        initView(view);
        return view;
    }

    private void initView(View view){
        btnLogin = (AppCompatButton) view.findViewById(R.id.button_login);
        tvRegisterLink = (TextView) view.findViewById(R.id.text_register);
        etName = (TextInputEditText)view.findViewById(R.id.text_team_name);
        etPassword = (TextInputEditText)view.findViewById(R.id.text_password);
        tvPasswordLostLink = (TextView) view.findViewById(R.id.text_reset_password);

        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().isEmpty()){
                    etName.setError("Kein Name angegeben!");
                }
                if(etPassword.getText().toString().isEmpty()){
                    etPassword.setError("Kein Password angegeben!");
                }

                if(!etName.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    loginProcess();
                }
            }
        });
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
        tvPasswordLostLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPasswordReset();
            }
        });
    }

    private void loginProcess(){
        setClickableStatus(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName(etName.getText().toString());
        loginRequest.setPassword(etPassword.getText().toString());
        Call<LoginResponse> response = apiInterface.loginUser(loginRequest);

        response.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                LoginResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Server error!", Snackbar.LENGTH_LONG).show();
                }
                else {
                    if (resp.getResult().equals(Constants.SUCCESS)) {
                        switch (resp.getMessage()) {
                            case "login_successfull":
                                pref.edit().putString("username", etName.getText().toString()).apply();
                                pref.edit().putString("password", etPassword.getText().toString()).apply();
                                getDataProcess(resp.getUserId());
                                break;
                            case "password_wrong":
                                etPassword.setError("Falsches Password!");
                                break;
                            default:
                                etName.setError("User nicht vorhanden!");
                                break;
                        }
                    }
                }
                setClickableStatus(true);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                setClickableStatus(true);
                Log.d(Constants.TAG, t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getDataProcess(final String userId){
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
                }
                else {
                    goToMainActivity(userId, resp.getData());
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG, t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToRegister(){
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, register);
        ft.commit();
    }

    private void goToPasswordReset() {
        Fragment resetPassword = new ResetPasswordFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, resetPassword);
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
        tvRegisterLink.setClickable(status);
    }
}
