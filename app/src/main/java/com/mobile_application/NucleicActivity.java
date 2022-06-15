package com.mobile_application;

import android.app.Activity;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Message;
import android.os.Bundle;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Timer;
import java.util.TimerTask;
public class NucleicActivity extends Activity {
    private RelativeLayout countDown;
    private TextView hoursTv, minutesTv, secondsTv;
    private long mDay = 10;
    private long mHour = 10;
    private long mMin = 30;
    private long mSecond = 00;// 天 ,小时,分钟,秒
    private boolean isRun = true;
    private Handler timeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1) {
                computeTime();
                hoursTv.setText(mHour+"");
                minutesTv.setText(mMin+"");
                secondsTv.setText(mSecond+"");
                if (mHour==0&&mMin==0&&mSecond==0) {
                    countDown.setVisibility(View.GONE);
                }
            }
        }
    };

    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                mMin = 59;
                mHour--;
                if (mHour < 0) {
                    // 倒计时结束
                    mHour = 23;
                    mDay--;
                    if(mDay < 0){
                        // 倒计时结束
                        mDay = 0;
                        mHour= 0;
                        mMin = 0;
                        mSecond = 0;
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timer mTimer = new Timer();
        hoursTv = findViewById(R.id.hour);
        minutesTv = findViewById(R.id.minute);
        secondsTv = findViewById(R.id.second);

        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 1;
                timeHandler.sendMessage(message);
            }
        };
        mTimer.schedule(mTimerTask,0,1000);
    }
}
