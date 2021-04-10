package com.androsov.groupjournal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class TabBarActivity extends AppCompatActivity {
    private BottomNavigationView mTabBarNav;
    private StudentFragmentList collectionFragment;
    private MapsFragment mapFragment;
    private OptionsFragment optionsFragment;
    private FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_bar);
        mTabBarNav = findViewById(R.id.nav_view);


        collectionFragment = new StudentFragmentList();
        mapFragment = new MapsFragment();
        optionsFragment = new OptionsFragment();
//        if (OptionsFragment.optionWasChange) {
//            setTitle(R.string.title_options);
//            setFragment(optionsFragment, getString(R.string.title_options));
//            OptionsFragment.optionWasChange = false;
//        } else {
            setTitle(R.string.student_list);
            setFragment(collectionFragment, getString(R.string.student_list));
//        }

        mTabBarNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_group : {
                        setTitle(R.string.student_list);
                        setFragment(collectionFragment, getString(R.string.student_list));
                        return true;
                    }
                    case R.id.navigation_map : {
                        setTitle(R.string.title_map);
                        setFragment(mapFragment, getString(R.string.title_map));
                        return true;
                    }
                    case R.id.navigation_settings : {
                        setTitle(R.string.title_settings);
                        setFragment(optionsFragment, getString(R.string.title_settings));
                        return true;
                    }
                    default: {
                        return false;
                    }

                }
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void setFragment(Fragment fragment, String str){
        ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(str);
        if (str.equals(getString(R.string.title_settings)) || str.equals(getString(R.string.title_map))) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        ft.replace(R.id.host_fragment, fragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        ft = getSupportFragmentManager().beginTransaction();
        int index = getSupportFragmentManager().getBackStackEntryCount() - 2;
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
        String tag = backEntry.getName();
        if (!tag.equals(getString(R.string.student_list)) && !tag.equals(getString(R.string.title_map)) && !tag.equals(getString(R.string.title_settings))) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        backEntry = getSupportFragmentManager().getBackStackEntryAt(index + 1);
        tag = backEntry.getName();
        getSupportFragmentManager().popBackStack();
        ft.commit();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String savedLocale = preferences.getString("language", null);
        Configuration configuration = newBase.getResources().getConfiguration();
//        if (savedLocale != null){
//            switch (savedLocale) {
//                case "en": {
//                    OptionsFragment.len = 0;
//                    break;
//                }
//                case "ru": {
//                    OptionsFragment.len = 1;
//                    break;
//                }
//            }
//            Locale locale = new Locale(savedLocale);
//            Locale.setDefault(locale);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                configuration.setLocale(locale);
//            } else {
//                preferences.edit().putString("language", Locale.getDefault().getLanguage()).apply();
//            }
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.attachBaseContext(newBase);//.createConfigurationContext(configuration));
//        }
    }
}