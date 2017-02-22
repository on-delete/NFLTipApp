package com.andre.nfltipapp.loginregistryview;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import com.andre.nfltipapp.R;
import com.andre.nfltipapp.rest.Api;
import com.andre.nfltipapp.rest.ApiInterface;
import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.NameExistResponse;
import com.andre.nfltipapp.loginregistryview.model.RegisterRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterResponse;
import com.andre.nfltipapp.loginregistryview.model.User;

import retrofit2.Call;
import retrofit2.Callback;

public class RegisterFragment extends Fragment {

    private TextInputEditText etEmail, etPassword, etName;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);

        apiInterface = Api.getInstance(getActivity()).getApiInterface();

        initViews(view);
        return view;
    }

    private void initViews(View view){
        AppCompatButton btnRegister = (AppCompatButton) view.findViewById(R.id.btn_register);
        TextView tvLoginLink = (TextView) view.findViewById(R.id.tv_login);
        etEmail = (TextInputEditText) view.findViewById(R.id.et_email);
        etPassword = (TextInputEditText) view.findViewById(R.id.et_password);
        etName = (TextInputEditText) view.findViewById(R.id.et_name);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                User newUser = new User(name, email, password);

                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    nameExistingProcess(newUser);
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
    }

    private void nameExistingProcess(final User newUser) {
        NameExistRequest request = new NameExistRequest();
        request.setName(newUser.getName());
        Call<NameExistResponse> response = apiInterface.nameExist(request);

        response.enqueue(new Callback<NameExistResponse>() {
            @Override
            public void onResponse(Call<NameExistResponse> call, retrofit2.Response<NameExistResponse> response) {
                NameExistResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(resp.getResult().equals(Constants.SUCCESS)){
                    if(resp.getMessage().equals("username unused")) {
                        registerProcess(newUser);
                    }
                    else{
                        Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Username already in use!", Snackbar.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.d(Constants.FAILURE, resp.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NameExistResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void registerProcess(User newUser){
        RegisterRequest request = new RegisterRequest();
        request.setUser(newUser);
        Call<RegisterResponse> response = apiInterface.registerUser(request);

        response.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                RegisterResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Registered!", Snackbar.LENGTH_LONG).show();
                    goToLogin();
                }
                else{
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Internal Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin(){
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, login);
        ft.commit();
    }
}
