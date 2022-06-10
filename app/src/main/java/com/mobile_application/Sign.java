package com.mobile_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mobile_application.databinding.ActivitySignBinding;
import com.mobile_application.databinding.RegisterBinding;

public class Sign extends AppCompatActivity {
    private ActivitySignBinding viewBinding;
    public int connectFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivitySignBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        Intent intent = getIntent();
        connectFlag = Integer.valueOf(intent.getStringExtra("connectFlag")).intValue();
    }
}