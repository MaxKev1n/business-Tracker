package com.mobile_application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mobile_application.databinding.ActivityHomeBinding;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding viewBinding;
    private boolean settingFirstView = true;

    public Bitmap curBitmap;
    public int isView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(viewBinding.navView, navController);

    }

    public boolean getSettingFirstView() {
        return settingFirstView;
    }

    public void setSettingFirstView(boolean isView) {
        settingFirstView = isView;
    }

}