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
import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.ResetPasswordResponse;
import com.andre.nfltipapp.rest.Api;
import com.andre.nfltipapp.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;

public class ResetPasswordFragment extends Fragment{

    private TextView tvLoginLink;
    private TextInputEditText etName;
    private AppCompatButton btnResetPassword;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password_user_input,container,false);

        apiInterface = Api.getInstance(getActivity()).getApiInterface();

        initView(view);
        return view;
    }

    private void initView(View view){
        tvLoginLink = (TextView) view.findViewById(R.id.text_login);
        etName = (TextInputEditText)view.findViewById(R.id.text_user_name);
        btnResetPassword = (AppCompatButton)view.findViewById((R.id.button_password_reset));
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().isEmpty()){
                    etName.setError("Kein Name angegeben!");
                }
                if(!etName.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPasswordProcess(etName.getText().toString());
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

    private void resetPasswordProcess(String username){
        setClickableStatus(false);

        NameExistRequest resetRequest = new NameExistRequest();
        if(username != null) {
            resetRequest.setName(username);
        }
        Call<ResetPasswordResponse> response = apiInterface.resetPassword(resetRequest);

        response.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, retrofit2.Response<ResetPasswordResponse> response) {
                ResetPasswordResponse resp = response.body();
                progressBar.setVisibility(View.INVISIBLE);
                if(response.code()==500){
                    Log.d(Constants.TAG, resp.getMessage());
                    Snackbar.make(getActivity().findViewById(R.id.fragment_host),"Server error!", Snackbar.LENGTH_LONG).show();
                }
                else {
                    if (resp.getResult().equals(Constants.SUCCESS)) {
                        switch (resp.getMessage()) {
                            case "user_not_found":
                                etName.setError("User nicht vorhanden!");
                                break;
                            case "request_created":
                                goToSuccess(resp.getEmail());
                                break;
                            default:
                                break;
                        }
                    }
                }
                setClickableStatus(true);
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                setClickableStatus(true);
                Log.d(Constants.TAG, t.getMessage());
                Snackbar.make(getActivity().findViewById(R.id.fragment_host), "Server nicht erreichbar!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin(){
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, login);
        ft.commit();
    }

    private void goToSuccess(String email){
        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        Fragment success = new ResetPasswordSuccessFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        success.setArguments(bundle);
        ft.replace(R.id.fragment_host, success);
        ft.commit();
    }

    private void setClickableStatus(boolean status){
        btnResetPassword.setClickable(status);
    }
}
