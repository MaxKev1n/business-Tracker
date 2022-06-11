package com.mobile_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.mobile_application.databinding.ActivityMainBinding;
import com.mobile_application.databinding.RegisterBinding;
import com.mobile_application.utils.UserDAO;

public class RegisterActivity extends AppCompatActivity {
    private RegisterBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = RegisterBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        viewBinding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        UserDAO userDao = new UserDAO();
                        int res = 0;
                        try {
                            res = userDao.add(viewBinding.editTextAccount.getText().toString(), viewBinding.editTextName.getText().toString(), viewBinding.editTextPassword.getText().toString());
                            hand.sendEmptyMessage(res);
                            System.out.println(res);
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
                        Toast.makeText(RegisterActivity.this, "注册失败，存在相同账号", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int connectFlag = 0;
                        if(msg.what == 0) {
                            Toast.makeText(RegisterActivity.this, "连接失败，进入本地模式", Toast.LENGTH_SHORT).show();
                            connectFlag = 0;
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            connectFlag = 1;
                        }
                        Intent intent = new Intent(RegisterActivity.this, Display.class);
                        intent.putExtra("connectFlag", String.valueOf(connectFlag));
                        startActivity(intent);
                    }
                }
            };
        });
    }
}