package com.mobile_application;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.widget.CalendarView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mobile_application.databinding.ActivityHomeBinding;
import com.mobile_application.utils.LocalDb;
import com.mobile_application.utils.UserDAO;

import java.util.List;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding viewBinding;
    public int connectFlag;
    public String myAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        Intent intent = getIntent();
        connectFlag = Integer.valueOf(intent.getStringExtra("connectFlag")).intValue();
        myAccount = intent.getStringExtra("myAccount");

        Time time = new Time();
        time.setToNow();
        String curDate = String.valueOf(time.year) + "-" + String.valueOf(time.month) + "-" + String.valueOf(time.monthDay);
        LocalDb localDb = new LocalDb(Home.this, "app.db", null, 1, myAccount);
        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
        UserDAO userDAO = new UserDAO();
        List<String> curData = userDAO.selectUserSign(sqliteDatabase, myAccount, curDate);
        String curDisplay = "您今日已完成" + "<font color=black><b><big><big>" + curData.get(0) + "</big></big></b></font>" + "项任务，学习时间为" + "<font color=black><b><big><big>" + curData.get(1) + "</big></big></b></font>" + "分钟";
        viewBinding.textSign.setText(Html.fromHtml(curDisplay));
        List<Integer> curTotal = userDAO.selectUserTotal(sqliteDatabase, myAccount);
        String curListDisplay = "<font color=black><b><big><big>" + String.valueOf(curTotal.get(0)) + "</big></big></b></font>项";
        String curTimeDisplay = "<font color=black><b><big><big>" + String.valueOf(curTotal.get(1)) + "</big></big></b></font>分钟";
        viewBinding.totalListNum.setText(Html.fromHtml(curListDisplay));
        viewBinding.totalTimeNum.setText(Html.fromHtml(curTimeDisplay));
        //userDAO.insertUserSign(sqliteDatabase, myAccount, curDate, 5, 10);

        viewBinding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = String.valueOf(i) + "-" + String.valueOf(i1) + "-" + String.valueOf(i2);
                List<String> selectRes = userDAO.selectUserSign(sqliteDatabase, myAccount, date);
                String display;
                if(selectRes.isEmpty()){
                    display = "您在该日完成" + "<font color=black><b><big><big>0</big></big></b></font>项任务，学习时间为<font color=black><b><big><big>0</big></big></b></font>分钟";
                }
                else {
                    display = "您在该日完成" + "<font color=black><b><big><big>" + selectRes.get(0) + "</big></big></b></font>" + "项任务，学习时间为" + "<font color=black><b><big><big>" + selectRes.get(1) + "</big></big></b></font>" + "分钟";
                }
                viewBinding.textSign.setText(Html.fromHtml(display));
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(viewBinding.navView, navController);
    }

}