package com.mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import com.mobile_application.databinding.ActivitySignBinding;

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

        viewBinding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                viewBinding.textSign.setText(i + "年" + i1 + "月" + i2 + "日");
            }
        });
    }
}