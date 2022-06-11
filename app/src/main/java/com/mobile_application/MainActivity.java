package com.mobile_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.mobile_application.utils.*;

import com.mobile_application.databinding.ActivityMainBinding;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        viewBinding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        UserDAO userDao = new UserDAO();
                        int res = 0;
                        try {
                            res = userDao.select(viewBinding.editTextAccount.getText().toString(), viewBinding.editTextPassword.getText().toString());
                            hand.sendEmptyMessage(res);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @SuppressLint("HandlerLeak")
            final Handler hand = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == 2) {
                        Toast.makeText(MainActivity.this, "登录失败，请检查用户名或密码", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int connectFlag = 0;
                        if(msg.what == 0) {
                            Toast.makeText(MainActivity.this, "连接失败，进入本地模式", Toast.LENGTH_SHORT).show();
                            connectFlag = 0;
                        }
                        else {
                            Toast.makeText(MainActivity.this, "登录成功，进行同步", Toast.LENGTH_SHORT).show();
                            connectFlag = 1;
                        }

                        LocalDb localDb = new LocalDb(MainActivity.this, "app.db", null, 1, viewBinding.editTextAccount.getText().toString());
                        SQLiteDatabase sqliteDatabase = localDb.getReadableDatabase();

                        Intent intent = new Intent(MainActivity.this, Sign.class);
                        intent.putExtra("connectFlag", String.valueOf(connectFlag));
                        startActivity(intent);
                    }
                }
            };
        });

        viewBinding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Sign.class);
                startActivity(intent);
            }
        });

    }
}