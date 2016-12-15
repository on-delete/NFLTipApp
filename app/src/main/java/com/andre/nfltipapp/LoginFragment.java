package com.andre.nfltipapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andre.nfltipapp.model.RegisterLoginRequest;
import com.andre.nfltipapp.model.RegisterLoginResponse;
import com.andre.nfltipapp.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andre on 15.12.2016.
 */

public class LoginFragment extends Fragment implements View.OnClickListener{
    private AppCompatButton btn_login;
    private EditText et_name,et_password;
    private TextView tv_register;
    private ProgressBar progress;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        pref = getActivity().getPreferences(0);

        btn_login = (AppCompatButton)view.findViewById(R.id.btn_login);
        tv_register = (TextView)view.findViewById(R.id.tv_register);
        et_name = (EditText)view.findViewById(R.id.et_name);
        et_password = (EditText)view.findViewById(R.id.et_password);

        progress = (ProgressBar)view.findViewById(R.id.progress);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register:
                goToRegister();
                break;
            case R.id.btn_login:
                String name = et_name.getText().toString();
                String password = et_password.getText().toString();
                if(!name.isEmpty() && !password.isEmpty()) {
                    progress.setVisibility(View.VISIBLE);
                    loginProcess(name,password);
                } else {
                    Snackbar.make(getView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void loginProcess(String name,String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setName(name);
        user.setPassword(password);
        RegisterLoginRequest request = new RegisterLoginRequest();
        request.setUser(user);
        Call<RegisterLoginResponse> response = requestInterface.registerLogin(request);

        response.enqueue(new Callback<RegisterLoginResponse>() {
            @Override
            public void onResponse(Call<RegisterLoginResponse> call, retrofit2.Response<RegisterLoginResponse> response) {
                RegisterLoginResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.NAME,resp.getUser().getName());
                    editor.putString(Constants.UUID,resp.getUser().getUuid());
                    editor.apply();
                    goToMainActivity();
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<RegisterLoginResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,t.getMessage());
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToRegister(){
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,register);
        ft.commit();
    }

    private void goToMainActivity(){
//TODO: Switch to MainActivity after success
    }

}
