package com.mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.CalendarView;
import android.widget.Toast;

import com.mobile_application.databinding.ActivitySignBinding;
import com.mobile_application.utils.LocalDb;
import com.mobile_application.utils.UserDAO;

import java.util.List;

public class Sign extends AppCompatActivity {
    private ActivitySignBinding viewBinding;
    public int connectFlag;
    public String myAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivitySignBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        Intent intent = getIntent();
        connectFlag = Integer.valueOf(intent.getStringExtra("connectFlag")).intValue();
        myAccount = intent.getStringExtra("myAccount");

        Time time = new Time();
        time.setToNow();
        String curDate = String.valueOf(time.year) + "-" + String.valueOf(time.month) + "-" + String.valueOf(time.monthDay);
        LocalDb localDb = new LocalDb(Sign.this, "app.db", null, 1, myAccount);
        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
        UserDAO userDAO = new UserDAO();
        List<String> curData = userDAO.selectUserSign(sqliteDatabase, myAccount, curDate);
        viewBinding.textSign.setText("您今日已完成" + curData.get(0) + "项任务，学习时间为" + curData.get(1) + "分钟");
        //userDAO.insertUserSign(sqliteDatabase, myAccount, curDate, 5, 10);

        viewBinding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = String.valueOf(i) + "-" + String.valueOf(i1) + "-" + String.valueOf(i2);
                List<String> selectRes = userDAO.selectUserSign(sqliteDatabase, myAccount, date);
                if(selectRes.isEmpty()){
                    viewBinding.textSign.setText("您在该日完成0项任务，学习时间为0分钟");
                }
                else {
                    viewBinding.textSign.setText("您在该日完成" + selectRes.get(0) + "项任务，学习时间为" + selectRes.get(1) + "分钟");
                }
            }
        });
    }
}