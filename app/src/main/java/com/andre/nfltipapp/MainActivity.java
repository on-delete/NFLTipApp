package com.andre.nfltipapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.andre.nfltipapp.loginregistryview.LoginActivity;
import com.andre.nfltipapp.model.Data;
import com.andre.nfltipapp.tabview.NavigationSelectedListener;
import com.andre.nfltipapp.tabview.fragments.DashboardFragment;

import static com.andre.nfltipapp.Constants.SHARED_PREF_FILENAME;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private Bundle bundle = new Bundle();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_FILENAME, 0);

        String userId = getIntent().getStringExtra(Constants.USERID);
        Data data = getIntent().getParcelableExtra(Constants.DATA);

        DataService dataService = new DataService();
        dataService.setData(data);
        dataService.setUserId(userId);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        NavigationSelectedListener navigationSelectedListener = new NavigationSelectedListener(fm, dataService);
        navigation.setOnNavigationItemSelectedListener(navigationSelectedListener);
        navigation.setBackgroundResource(R.drawable.back_dark_grey);

        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#f0f0f0"));
        navigation.setItemTextColor(colorStateList);

        bundle.putParcelable("dataService", dataService);
        Fragment selectedFragment = fm.findFragmentByTag("dashboard");
        if(selectedFragment == null) {
            selectedFragment = new DashboardFragment();
            selectedFragment.setArguments(bundle);
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, selectedFragment, "dashboard").commit();
    }

    private void logout() {
        pref.edit().putString("username", null).apply();
        pref.edit().putString("password", null).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        this.finish();
        startActivity(intent);
    }
}
