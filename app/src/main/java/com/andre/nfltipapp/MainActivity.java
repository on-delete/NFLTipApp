package com.andre.nfltipapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.andre.nfltipapp.loginregistryview.LoginActivity;
import com.andre.nfltipapp.tabview.TabSectionsPagerAdapter;

import static com.andre.nfltipapp.Constants.SHARED_PREF_FILENAME;

public class MainActivity extends AppCompatActivity {

    private TabLayout tblMain;
    private SharedPreferences pref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_FILENAME, 0);

        String userId = getIntent().getStringExtra(Constants.USERID);

        Toolbar tbMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tbMain);

        tbMain.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                logout();
                return true;
            }
        });

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

    private void logout() {
        pref.edit().putString("username", null).apply();
        pref.edit().putString("password", null).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        this.finish();
        startActivity(intent);
    }

    private void initTabIcons(){
        tblMain.getTabAt(0).setIcon(R.drawable.ic_tab_ranking_white);
        tblMain.getTabAt(1).setIcon(R.drawable.ic_tab_prognosen_white);
        tblMain.getTabAt(2).setIcon(R.drawable.ic_tab_statistic_white);
        tblMain.getTabAt(3).setIcon(R.drawable.ic_tab_tabelle_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
