package com.andre.nfltipapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.andre.nfltipapp.tabview.TabSectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout tblMain;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getIntent().getStringExtra(Constants.USERID);

        Toolbar tbMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tbMain);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(Constants.TAB_NAME_LIST[0]);
        }

        TabSectionsPagerAdapter tabSectionsPagerAdapter = new TabSectionsPagerAdapter(getSupportFragmentManager(), userId);

        ViewPager vpMain = (ViewPager) findViewById(R.id.view_pager);
        vpMain.setAdapter(tabSectionsPagerAdapter);

        tblMain = (TabLayout) findViewById(R.id.tab_host);
        tblMain.setupWithViewPager(vpMain);

        initTabIcons();

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void initTabIcons(){
        tblMain.getTabAt(0).setIcon(R.drawable.ic_tab_ranking_white);
        tblMain.getTabAt(1).setIcon(R.drawable.ic_tab_prognosen_white);
        tblMain.getTabAt(2).setIcon(R.drawable.ic_tab_statistic_white);
        tblMain.getTabAt(3).setIcon(R.drawable.ic_tab_tabelle_white);
    }
}
