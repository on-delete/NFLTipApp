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
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.rest.RequestInterface;
import com.andre.nfltipapp.loginregistryview.model.NameExistRequest;
import com.andre.nfltipapp.loginregistryview.model.NameExistResponse;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginResponse;
import com.andre.nfltipapp.loginregistryview.model.User;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment implements View.OnClickListener{

    private TextInputEditText et_email,et_password,et_name;
    private ProgressBar progress;
    private RequestInterface requestInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);
        initViews(view);
        initRequestInterface();
        return view;
    }

    private void initViews(View view){
        AppCompatButton btn_register = (AppCompatButton) view.findViewById(R.id.btn_register);
        TextView tv_login = (TextView) view.findViewById(R.id.tv_login);
        et_email = (TextInputEditText)view.findViewById(R.id.et_email);
        et_password = (TextInputEditText)view.findViewById(R.id.et_password);
        et_name = (TextInputEditText)view.findViewById(R.id.et_name);

        progress = (ProgressBar)view.findViewById(R.id.progress);

        btn_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    private void initRequestInterface(){
        OkHttpClient httpClient = Utils.getHttpClient(getActivity());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        requestInterface = retrofit.create(RequestInterface.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login:
                goToLogin();
                break;

            case R.id.btn_register:

                String name = et_name.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                User newUser = new User(name, email, password);

                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    progress.setVisibility(View.VISIBLE);
                    nameExistingProcess(newUser);
                } else {
                    Snackbar.make(getView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void nameExistingProcess(final User newUser) {
        NameExistRequest request = new NameExistRequest();
        request.setName(newUser.getName());
        Call<NameExistResponse> response = requestInterface.nameExist(request);

        response.enqueue(new Callback<NameExistResponse>() {
            @Override
            public void onResponse(Call<NameExistResponse> call, retrofit2.Response<NameExistResponse> response) {
                NameExistResponse resp = response.body();
                progress.setVisibility(View.INVISIBLE);
                if(resp.getResult().equals(Constants.SUCCESS)){
                    if(resp.getMessage().equals(Constants.USERNAME_FREE)) {
                        registerProcess(newUser);
                    }
                    else{
                        Snackbar.make(getView(),"Username already in use!", Snackbar.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.d(Constants.FAILURE, resp.getMessage());
                    Snackbar.make(getView(),"Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NameExistResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void registerProcess(User newUser){
        RegisterLoginRequest request = new RegisterLoginRequest();
        request.setUser(newUser);
        Call<RegisterLoginResponse> response = requestInterface.registerUser(request);

        response.enqueue(new Callback<RegisterLoginResponse>() {
            @Override
            public void onResponse(Call<RegisterLoginResponse> call, retrofit2.Response<RegisterLoginResponse> response) {
                RegisterLoginResponse resp = response.body();
                progress.setVisibility(View.INVISIBLE);
                if(resp.getResult().equals(Constants.SUCCESS)){
                    Snackbar.make(getView(),"Registered!", Snackbar.LENGTH_LONG).show();
                    goToLogin();
                }
                else{
                    Snackbar.make(getView(),"Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin(){
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,login);
        ft.commit();
    }
}
