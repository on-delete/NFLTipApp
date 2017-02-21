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
import com.andre.nfltipapp.Utils;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.model.DataRequest;
import com.andre.nfltipapp.model.DataResponse;
import com.andre.nfltipapp.rest.RequestInterface;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginRequest;
import com.andre.nfltipapp.loginregistryview.model.RegisterLoginResponse;
import com.andre.nfltipapp.loginregistryview.model.User;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment implements View.OnClickListener{
    private TextInputEditText et_name,et_password;
    private ProgressBar progress;
    private RequestInterface requestInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        initViews(view);
        initRequestInterface();
        return view;
    }

    private void initViews(View view){
        AppCompatButton btn_login = (AppCompatButton) view.findViewById(R.id.btn_login);
        TextView tv_register = (TextView) view.findViewById(R.id.tv_register);
        et_name = (TextInputEditText)view.findViewById(R.id.et_name);
        et_password = (TextInputEditText)view.findViewById(R.id.et_password);
        et_name.setText("test2");
        et_password.setText("hallo1");

        progress = (ProgressBar)view.findViewById(R.id.progress);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
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
            case R.id.tv_register:
                goToRegister();
                break;
            case R.id.btn_login:
                String name = et_name.getText().toString();
                String password = et_password.getText().toString();
                User loginUser = new User(name, password);
                if(!name.isEmpty() && !password.isEmpty()) {
                    progress.setVisibility(View.VISIBLE);
                    loginProcess(loginUser);
                } else {
                    Snackbar.make(getView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void loginProcess(User loginUser){
        RegisterLoginRequest request = new RegisterLoginRequest();
        request.setUser(loginUser);
        Call<RegisterLoginResponse> response = requestInterface.loginUser(request);

        response.enqueue(new Callback<RegisterLoginResponse>() {
            @Override
            public void onResponse(Call<RegisterLoginResponse> call, retrofit2.Response<RegisterLoginResponse> response) {
                RegisterLoginResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    if(resp.getMessage().equals(Constants.LOGIN_SUCCESSFULL)){
                        getDataProcess(resp.getUser().getName(), resp.getUser().getUuid());
                    } else {
                        Snackbar.make(getView(),"Login Failed!", Snackbar.LENGTH_LONG).show();
                        progress.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,t.getMessage());
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getDataProcess(final String name, final String uuid){
        DataRequest request = new DataRequest();
        request.setUuid(uuid);
        Call<DataResponse> response = requestInterface.getData(request);

        response.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, retrofit2.Response<DataResponse> response) {
                DataResponse resp = response.body();
                progress.setVisibility(View.INVISIBLE);
                if(resp.getResult().equals(Constants.SUCCESS)){
                    if(resp.getMessage().equals(Constants.GET_DATA_SUCCESSFULL)){
                        goToMainActivity(name, uuid, resp.getData());
                    } else {
                        Snackbar.make(getView(),"Login Failed!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
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

    private void goToMainActivity(String name, String uuid, Data data){
        Log.d(Constants.TAG, "Login successfull!");
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        intent.putExtra(Constants.NAME, name);
        intent.putExtra(Constants.UUID, uuid);
        intent.putExtra(Constants.DATA, data);
        this.getActivity().finish();
        startActivity(intent);
    }
}
