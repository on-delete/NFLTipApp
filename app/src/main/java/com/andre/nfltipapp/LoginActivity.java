package com.andre.nfltipapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getPreferences(0);
        initFragment();
    }

    private void initFragment(){
        //Fragment fragment;
        /*if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            //TODO: Direct to main activity
        }else {*/
        Fragment fragment = new LoginFragment();
        //}
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,fragment);
        ft.commit();
    }
}
