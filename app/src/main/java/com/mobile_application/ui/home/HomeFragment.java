package com.mobile_application.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobile_application.Config;
import com.mobile_application.MainActivity;
import com.mobile_application.databinding.FragmentHomeBinding;
import com.mobile_application.utils.LocalDb;
import com.mobile_application.utils.UserDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding viewBinding;
    public int connectFlag;
    public String myAccount = "default";
    private boolean isVisbleFlag = true;
    private static final String TAG = "HomeFragment";

    public void totalDataDisplay(UserDAO userDAO, SQLiteDatabase sqliteDatabase) throws Exception {
        List<Integer> curTotal = userDAO.selectUserTotal(sqliteDatabase, myAccount);
        if(curTotal != null) {
            String curListDisplay = "<font color=black><b><big><big>" + String.valueOf(curTotal.get(0)) + "</big></big></b></font>项";
            String curTimeDisplay = "<font color=black><b><big><big>" + String.valueOf(curTotal.get(1)) + "</big></big></b></font>分钟";
            viewBinding.totalListNum.setText(Html.fromHtml(curListDisplay));
            viewBinding.totalTimeNum.setText(Html.fromHtml(curTimeDisplay));
        }
        String curDate = new SimpleDateFormat("yyyy-M-dd").format(new Date(viewBinding.calendarView.getDate())).toString();
        List<String> selectRes = userDAO.selectUserSign(sqliteDatabase, myAccount, curDate);
        String display;
        if(selectRes.isEmpty()){
            display = "您在该日完成" + "<font color=black><b><big><big>0</big></big></b></font>项任务，学习时间为<font color=black><b><big><big>0</big></big></b></font>分钟";
        }
        else {
            display = "您在该日完成" + "<font color=black><b><big><big>" + selectRes.get(0) + "</big></big></b></font>" + "项任务，学习时间为" + "<font color=black><b><big><big>" + selectRes.get(1) + "</big></big></b></font>" + "分钟";
        }
        viewBinding.textSign.setText(Html.fromHtml(display));
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        viewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = viewBinding.getRoot();

        final TextView textView = viewBinding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Intent intent = ((AppCompatActivity) getActivity()).getIntent();
        connectFlag = Integer.valueOf(intent.getStringExtra("connectFlag")).intValue();
        myAccount = intent.getStringExtra("myAccount").toString();

        LocalDb localDb = new LocalDb((AppCompatActivity) getActivity(), "app.db", null, 1, myAccount);
        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
        UserDAO userDAO = new UserDAO();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    totalDataDisplay(userDAO, sqliteDatabase);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        viewBinding.freshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserDAO userDAO = new UserDAO();
                            userDAO.synchronizeRecord(myAccount, sqliteDatabase);
                            java.util.Date date = new Date();
                            userDAO.updateUserDate(myAccount, date);
                            totalDataDisplay(userDAO, sqliteDatabase);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                if(connectFlag == 1) {
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                viewBinding.freshlayout.setRefreshing(false);
            }
        });

        //String insertTime = String.valueOf(time.year) + "-" + String.valueOf(time.month + 1) + "-" + String.valueOf(time.monthDay) + " " + String.valueOf(time.hour) + ":" + String.valueOf(time.minute) + ":" + String.valueOf(time.second);
        //userDAO.insertUserSign(sqliteDatabase, myAccount, curDate, 5, insertTime);

        viewBinding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = String.valueOf(i) + "-" + String.valueOf(i1 + 1) + "-" + String.valueOf(i2);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDAO userDAO = new UserDAO();
                SharedPreferences preferences = Config.getConfig((AppCompatActivity) getActivity());
                int timeToLoop;
                if(preferences.getString("synchronizeTime", null) == null) {
                    timeToLoop = 60000;
                }
                else {
                    timeToLoop = Integer.valueOf(preferences.getString("synchronizeTime", null)) * 1000;
                }
                Log.d(TAG, String.valueOf(timeToLoop));
                while(connectFlag == 1 && isVisbleFlag == true) {
                    try {
                        userDAO.synchronizeRecord(myAccount, sqliteDatabase);
                        java.util.Date date = new Date();
                        userDAO.updateUserDate(myAccount, date);
                        totalDataDisplay(userDAO, sqliteDatabase);
                        Thread.sleep(timeToLoop);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisbleFlag = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisbleFlag = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewBinding = null;
    }
}