package com.mobile_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mobile_application.databinding.ActivityMainBinding;
import com.mobile_application.utils.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding viewBinding;
    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;
    private static final String TAG = "MainActivity";

    public void synchronizeRecord(String account, UserDAO userDAO) throws Exception {
        LocalDb localDb = new LocalDb(this, "app.db", null, 1, viewBinding.editTextAccount.getText().toString());
        SQLiteDatabase sqliteDatabase = localDb.getWritableDatabase();
        userDAO.synchronizeRecord(account, sqliteDatabase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("rememberPassword", false);
        boolean isAutoLogin = preferences.getBoolean("autoLogin", false);
        if(isRemember) {
            String account = preferences.getString("account", "");
            String password = preferences.getString("password", "");
            viewBinding.editTextAccount.setText(account);
            viewBinding.editTextPassword.setText(password);
            viewBinding.rememberPassword.setChecked(true);
        }

        if(isAutoLogin && isRemember) {
            viewBinding.autoLogin.setChecked(true);
            viewBinding.rememberPassword.setChecked(true);

            viewBinding.buttonLogin.post(new Runnable() {
                @Override
                public void run() {
                    viewBinding.buttonLogin.performClick();
                }
            });
        }

        viewBinding.autoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewBinding.autoLogin.isChecked()) {
                    viewBinding.rememberPassword.setChecked(true);
                }
            }
        });

        viewBinding.rememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!viewBinding.rememberPassword.isChecked()) {
                    viewBinding.autoLogin.setChecked(false);
                }
            }
        });

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
                            if(res == 1) {
                                synchronizeRecord(viewBinding.editTextAccount.getText().toString(), userDao);
                            }
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

                        editor = preferences.edit();
                        if(viewBinding.rememberPassword.isChecked()) {
                            editor.putBoolean("rememberPassword", true);
                            editor.putString("password", viewBinding.editTextPassword.getText().toString());
                            editor.putBoolean("autoLogin", viewBinding.autoLogin.isChecked());
                        }
                        else{
                            editor.clear();
                        }
                        editor.putString("account", viewBinding.editTextAccount.getText().toString());
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, Home.class);
                        intent.putExtra("connectFlag", String.valueOf(connectFlag));
                        intent.putExtra("myAccount", viewBinding.editTextAccount.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                }
            };
        });

        viewBinding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}