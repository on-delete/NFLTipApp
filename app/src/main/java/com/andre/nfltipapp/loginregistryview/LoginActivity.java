package com.andre.nfltipapp.loginregistryview;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.andre.nfltipapp.R;

import static com.andre.nfltipapp.Constants.SHARED_PREF_FILENAME;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF_FILENAME, 0);
        String username = pref.getString("username", null);
        String password = pref.getString("password", null);

        if(username == null || password == null){
            initFragment(new LoginFragment());
        } else {
            initFragment(new LoggedInFragment());
        }
    }

    private void initFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_host, fragment);
        ft.commit();
    }
}
