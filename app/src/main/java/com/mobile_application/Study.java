package com.mobile_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mobile_application.databinding.ActivityDisplayBinding;
import com.mobile_application.databinding.ActivityStudyBinding;

public class Study extends AppCompatActivity {
    private ActivityStudyBinding viewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityStudyBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
    }
}