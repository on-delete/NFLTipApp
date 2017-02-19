package com.andre.nfltipapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.andre.nfltipapp.tabview.TabSectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userName = getIntent().getStringExtra(Constants.NAME);
        String uuid = getIntent().getStringExtra(Constants.UUID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Ranking");
        }

        TabSectionsPagerAdapter tabSectionsPagerAdapter = new TabSectionsPagerAdapter(getSupportFragmentManager(), uuid);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(tabSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ranking_tab_icon_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.prognosen_tab_icon_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.statistic_tab_icon_white);
        tabLayout.getTabAt(3).setIcon(R.drawable.tabelle_tab_icon_white);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position <= Constants.TAB_NAME_LIST.length){
                    getSupportActionBar().setTitle(Constants.TAB_NAME_LIST[position]);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
