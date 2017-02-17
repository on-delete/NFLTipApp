package com.andre.nfltipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.andre.nfltipapp.tabview.SlidingTabLayout;
import com.andre.nfltipapp.tabview.TabSectionsPagerAdapter;

public class MainActivity extends FragmentActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String userName = intent.getStringExtra(Constants.NAME) == null ? "admin" : intent.getStringExtra(Constants.NAME);
        String uuid = intent.getStringExtra(Constants.UUID) == null ? "10" : intent.getStringExtra(Constants.UUID);

        TabSectionsPagerAdapter tabSectionsPagerAdapter = new TabSectionsPagerAdapter(getSupportFragmentManager(), userName, uuid);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(tabSectionsPagerAdapter);
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setViewPager(pager);
    }
}
